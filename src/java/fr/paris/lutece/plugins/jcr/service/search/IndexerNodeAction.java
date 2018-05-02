/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.jcr.service.search;

import fr.paris.lutece.plugins.jcr.business.INodeAction;
import fr.paris.lutece.plugins.jcr.business.IRepositoryFile;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.service.jcrsearch.JcrSearchItem;
import fr.paris.lutece.plugins.lucene.service.indexer.IFileIndexer;
import fr.paris.lutece.plugins.lucene.service.indexer.IFileIndexerFactory;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.url.UrlItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * Implementation of INodeAction for indexing the nodes of a JCR
 * 
 * It stores results using TreeSet and specific Comparator
 */
public class IndexerNodeAction implements INodeAction<Document, Collection<Document>>
{
    private static final String DISPLAY_FILE_BASE_URL = "jsp/site/plugins/jsr170/DisplayFile.jsp";
    private static final String PARAMETER_FILE_ID = "file_id";
    private static final String PARAMETER_WORKSPACE_ID = "workspace_id";
    private Comparator<Document> _nodeComparator;
    private String _strPluginName;
    private AdminWorkspace _adminWorkspace;
    private String _strRole;

    /**
     * Default constructor
     * @param nodeComparator the comparator used to store the results in the
     *            collection
     * @param strPluginName the plugin name to retrieve Spring context
     * @param adminWorkspace the adminWorkspace to work on
     */
    public IndexerNodeAction( Comparator<Document> nodeComparator, String strPluginName, AdminWorkspace adminWorkspace,
            String strRole )
    {
        _nodeComparator = nodeComparator;
        _strPluginName = strPluginName;
        _adminWorkspace = adminWorkspace;
        _strRole = ( strRole == null ) ? "none" : strRole;
    }

    /**
     * @return a collection of document
     * @see fr.paris.lutece.plugins.jcr.business.INodeAction#createCollection()
     */
    public Collection<Document> createCollection( )
    {
        return new TreeSet<Document>( _nodeComparator );
    }

    /**
     * Creates a lucene Document using the JCR file
     * @param file the file to index
     * @return a Document or null if this node should not be indexed
     * @see fr.paris.lutece.plugins.jcr.business.INodeAction#doAction(fr.paris.lutece.plugins.jcr.business.IRepositoryFile)
     */
    public Document doAction( IRepositoryFile file )
    {
        if ( file.isDirectory( ) )
        {
            return null;
        }

        try
        {
            Document document = new Document( );

            FieldType ft = new FieldType( StringField.TYPE_STORED );
            ft.setOmitNorms( false );

            if ( file.isFile( ) )
            {
                String strContent = getContentToIndex( file );
                document.add( new Field( SearchItem.FIELD_CONTENTS, strContent, TextField.TYPE_NOT_STORED ) );

                String strDate = DateTools.dateToString( file.lastModified( ).getTime( ), DateTools.Resolution.DAY );

                document.add( new Field( SearchItem.FIELD_DATE, strDate, ft ) );
            }

            document.add( new Field( SearchItem.FIELD_ROLE, _strRole, ft ) );
            document.add( new Field( SearchItem.FIELD_SUMMARY, file.getName( ), ft ) );
            document.add( new Field( SearchItem.FIELD_TITLE, file.getName( ), ft ) );
            document.add( new Field( SearchItem.FIELD_TYPE, _strPluginName, ft ) );
            document.add( new Field( SearchItem.FIELD_UID, ( file.getResourceId( ) == null ) ? ( String.valueOf( file
                    .hashCode( ) ) + "_" + JcrIndexer.SHORT_NAME ) : ( _adminWorkspace.getId( ) + "-"
                    + file.getResourceId( ) + "_" + JcrIndexer.SHORT_NAME ), ft ) );

            UrlItem url = new UrlItem( DISPLAY_FILE_BASE_URL );
            url.addParameter( PARAMETER_FILE_ID, file.getResourceId( ) );
            url.addParameter( PARAMETER_WORKSPACE_ID, _adminWorkspace.getId( ) );
            document.add( new Field( SearchItem.FIELD_URL, url.getUrl( ), ft ) );

            document.add( new Field( JcrSearchItem.FIELD_MIME_TYPE, file.getMimeType( ), ft ) );

            return document;
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    /**
     * Indexes a file content
     * @param file the file to index
     * @return a Reader that can be used to retrieve indexed content
     * @throws IOException if IO error
     */
    private String getContentToIndex( IRepositoryFile file ) throws IOException
    {
        // TODO
        StringBuilder sbContentToIndex = new StringBuilder( );
        // Gets indexer depending on the ContentType (ie: "application/pdf"
        // should use a PDF indexer)
        IFileIndexerFactory factory = (IFileIndexerFactory) SpringContextService
                .getBean( IFileIndexerFactory.BEAN_FILE_INDEXER_FACTORY );
        IFileIndexer indexer = factory.getIndexer( file.getMimeType( ) );

        AppLogService.debug( "Mime type of indexed file " + file + " is " + file.getMimeType( ) );

        if ( indexer != null )
        {
            try
            {
                ByteArrayInputStream bais = new ByteArrayInputStream( IOUtils.toByteArray( file.getContent( ) ) );
                sbContentToIndex.append( indexer.getContentToIndex( bais ) );
                bais.close( );
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        }
        else if ( file.getMimeType( ).startsWith( "text/" ) )
        {
            ContentHandler handler = new BodyContentHandler( );
            Metadata metadata = new Metadata( );
            try
            {
                new HtmlParser( ).parse( new ByteArrayInputStream( IOUtils.toByteArray( file.getContent( ) ) ),
                        handler, metadata, new ParseContext( ) );
            }
            catch ( SAXException e )
            {
                throw new AppException( "Error during page parsing." );
            }
            catch ( TikaException e )
            {
                throw new AppException( "Error during page parsing." );
            }

            //the content of the article is recovered in the parser because this one
            //had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
            sbContentToIndex = sbContentToIndex.append( handler.toString( ) );
        }

        return sbContentToIndex.toString( );
    }
}
