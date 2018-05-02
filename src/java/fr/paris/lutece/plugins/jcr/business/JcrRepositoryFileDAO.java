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
package fr.paris.lutece.plugins.jcr.business;

import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.springmodules.jcr.JcrTemplate;

import java.io.InputStream;

import java.util.Calendar;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;


/**
 * Implementation of IRepositoryFileDAO for Jackrabbit JCR
 */
public class JcrRepositoryFileDAO extends AbstractRepositoryContentDAO implements IRepositoryFileDAO
{
    protected static final String PATH_SEPARATOR = "/";
    protected static final String RESOURCE_TYPE = "JSR170_FILE";
    private static Properties _properties;

    /** This class implements the Singleton design pattern. */
    private static JcrRepositoryFileDAO _dao;

    ///////////////////////////////////////////////////////////////////////////////////////
    // Constructor and instantiation methods

    /**
     * Returns the unique instance of the singleton.
     * @param jcrTemplate a jcrTemplate
     * @param repositoryInitializer a repositoryInitializer
     * @param strDefaultWorkspaceName a default workspace name
     * @return the instance
     */
    static synchronized IRepositoryFileDAO getInstance( JcrTemplate jcrTemplate,
        IRepositoryInitializer repositoryInitializer, String strDefaultWorkspaceName )
    {
        if ( ( _dao == null ) || !_dao.isAlive(  ) )
        {
            _properties = new Properties(  );
            _properties.setProperty( NODE_TYPE_FOLDER, "nt:folder" );
            _properties.setProperty( NODE_TYPE_FILE, "lutece:file" );
            _properties.setProperty( NODE_TYPE_FILE_RESOURCE, "nt:resource" );
            _properties.setProperty( ATTRIBUTE_NODE_SIZE, "lutece:size" );
            _properties.setProperty( NODE_TYPE_JCR_CONTENT, "jcr:content" );
            _properties.setProperty( REGEXP_ABSOLUTE_PATH, "^/+" );
            _properties.setProperty( MIXIN_REFERENCEABLE, "mix:referenceable" );
            _properties.setProperty( DEFAULT_MIME_TYPE, "application/octet-stream" );
            _properties.setProperty( PROPERTY_JCR_MIMETYPE, "jcr:mimeType" );
            _properties.setProperty( PROPERTY_JCR_DATA, "jcr:data" );
            _properties.setProperty( PROPERTY_JCR_LASTMODIFIED, "jcr:lastModified" );
            _properties.setProperty( MIXIN_VERSIONNABLE, "mix:versionable" );
            _dao = new JcrRepositoryFileDAO(  );
            _dao.init( jcrTemplate, repositoryInitializer, strDefaultWorkspaceName );
        }

        return _dao;
    }

    /**
     * Get the absolute path of a node
     * @param node the node
     * @return a string containing the absolute path or null if node doesn't exist
     */
    protected String getAbsolutePath( Node node )
    {
        if ( node != null )
        {
            try
            {
                return node.getPath(  );
            }
            catch ( RepositoryException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return null;
    }

    /**
     * Get the node content
     * @param node the node
     * @return an inputstream or null if content doesn't exist
     */
    protected InputStream getFileContent( Node node )
    {
        return (InputStream) getFileProperty( node, "jcr:data", InputStream.class );
    }

    /**
     * Get the node name
     * @param node the node
     * @return the node name
     */
    protected String getName( Node node )
    {
        String strName = null;

        try
        {
            strName = node.getName(  );
        }
        catch ( RepositoryException e )
        {
            AppLogService.debug( e );
        }

        return strName;
    }

    /**
     * Get the size of node content
     * @param node the node
     * @return the size in bytes
     */
    protected long getSize( Node node )
    {
        long size = 0;

        try
        {
            size = node.getProperty( _properties.getProperty( ATTRIBUTE_NODE_SIZE ) ).getLong(  );
        }
        catch ( RepositoryException e )
        {
            // nothing to do
            AppLogService.error( e.getLocalizedMessage(  ), e );
        }

        return size;
    }

    /**
     * Get the node type
     * @param node the node
     * @return true if it is a directory, false otherwise
     */
    protected boolean isDirectory( Node node )
    {
        return "/".equals( getPath( node ) ) || isTypeOf( node, _properties.getProperty( NODE_TYPE_FOLDER ) );
    }

    /**
     * Get the node type
     * @param node the node
     * @return true if it is a file, false otherwise
     */
    protected boolean isFile( Node node )
    {
        return isTypeOf( node, _properties.getProperty( NODE_TYPE_FILE ) ) || isTypeOf( node, "nt:frozenNode" );
    }

    /**
     * Get the node existence
     * @param node the node
     * @return true if node exists, false otherwise
     */
    protected boolean exists( Node node )
    {
        return node != null;
    }

    /**
     * Get a node property of type clazz
     * @param node the node
     * @param strPropertyName the property name
     * @param clazz the type of object to return
     * @return an instance of clazz containing the value of the property strPropertyName
     */
    private <T> Object getFileProperty( Node node, String strPropertyName, Class<T> clazz )
    {
        try
        {
            if ( isDirectory( node ) )
            {
                throw new AppException( "Not a file exception" );
            }

            Node resNode = node.getNode( _properties.getProperty( NODE_TYPE_JCR_CONTENT ) );
            Value value = resNode.getProperty( strPropertyName ).getValue(  );

            if ( clazz.equals( String.class ) )
            {
                return value.getString(  );
            }
            else if ( clazz.equals( InputStream.class ) )
            {
                return value.getStream(  );
            }
            else if ( clazz.equals( Calendar.class ) )
            {
                return value.getDate(  );
            }
            else
            {
                throw new AppException( "Unsupported Node type" );
            }
        }
        catch ( RepositoryException e )
        {
            throw new AppException( e.getMessage(  ), e );
        }
    }

    /**
     * Get the Mime type
     * @param node the node
     * @return the mime type
     */
    protected String getMimeType( Node node )
    {
        return (String) getFileProperty( node, "jcr:mimeType", String.class );
    }

    /**
     * Get the creation date
     * @param node the node
     * @return the creation date
     */
    protected Calendar getCreated( Node node )
    {
        Calendar creationDate = null;

        try
        {
            creationDate = node.getProperty( "jcr:created" ).getDate(  );
        }
        catch ( RepositoryException e )
        {
            AppLogService.debug( e );
        }

        return creationDate;
    }

    /**
     * Get the modification date
     * @param node the node
     * @return the modification date
     */
    protected Calendar getLastModified( Node node )
    {
        return (Calendar) getFileProperty( node, "jcr:lastModified", Calendar.class );
    }

    /**
     * Test the node type
     * @param node the node
     * @param strType a type
     * @return return true is node type is strType, false otherwise
     */
    private boolean isTypeOf( Node node, String strType )
    {
        try
        {
            return ( node != null ) && node.isNodeType( strType );
        }
        catch ( RepositoryException e )
        {
            throw new AppException( e.getMessage(  ), e );
        }
    }

    @Override
    protected String getPath( Node node )
    {
        return getName( node );
    }

    @Override
    protected Properties getProperties(  )
    {
        return _properties;
    }
}
