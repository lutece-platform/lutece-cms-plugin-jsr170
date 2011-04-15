/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.plugins.jcr.authentication.JsrUser;
import fr.paris.lutece.plugins.jcr.business.IRepositoryFile;
import fr.paris.lutece.plugins.jcr.business.RepositoryFileHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminJcrHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminView;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.business.portlet.Jsr170Portlet;
import fr.paris.lutece.plugins.jcr.business.portlet.Jsr170PortletHome;
import fr.paris.lutece.plugins.jcr.service.JcrPlugin;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.lucene.document.Document;

import org.springframework.beans.BeansException;

import org.springframework.dao.DataAccessException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;


/**
 * JCR indexer implementation.
 *
 */
public class JcrIndexer implements SearchIndexer
{
    public static final String SHORT_NAME = "jcr";
    private static final String INDEXER_DESCRIPTION = "Indexer service for JCR";
    private static final String INDEXER_VERSION = "1.0.0";
    private static final String JSP_PAGE_SEARCH = "jsp/site/Portal.jsp?page=jsr170";

    /**
     * @return the description of this indexer
     * @see fr.paris.lutece.portal.service.search.SearchIndexer#getDescription()
     */
    public String getDescription(  )
    {
        return INDEXER_DESCRIPTION;
    }

    /**
     * Indexes all files among all defined views.
     *
     * @return a list of documents
     * @throws IOException IO error
     * @throws InterruptedException when recieving a signal
     * @throws SiteMessageException other cases
     * @see fr.paris.lutece.portal.service.search.SearchIndexer#getDocuments()
     */
    public void indexDocuments(  ) throws IOException, InterruptedException, SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( JcrPlugin.PLUGIN_NAME );

        // definition of the comparator used in the collected files
        // thus there can't be two Documents with the same UID Field 
        final Comparator<Document> documentComparator = new Comparator<Document>(  )
            {
                public int compare( Document o1, Document o2 )
                {
                    if ( ( o1 != null ) && ( o2 != null ) )
                    {
                        return o1.getField( SearchItem.FIELD_UID ).stringValue(  )
                                 .compareTo( o2.getField( SearchItem.FIELD_UID ).stringValue(  ) );
                    }
                    else if ( ( o1 == null ) && ( o2 != null ) )
                    {
                        return -1;
                    }
                    else if ( ( o1 != null ) && ( o2 == null ) )
                    {
                        return 1;
                    }

                    return 0;
                }
            };

        Jsr170Portlet jsr170Portlet;
        int defaultView;
        AdminView view;
        String strRole;

        for ( Portlet portlet : Jsr170PortletHome.findByType( PortletTypeHome.getPortletTypeId( 
                    Jsr170PortletHome.class.getName(  ) ) ) )
        {
            jsr170Portlet = Jsr170PortletHome.getInstance(  ).findByPortletId( portlet.getId(  ) );

            defaultView = jsr170Portlet.getDefaultView(  );

            Page page = PageHome.findByPrimaryKey( portlet.getPageId(  ) );
            strRole = page.getRole(  );

            if ( defaultView > 0 )
            {
                view = AdminJcrHome.getInstance(  ).findViewById( defaultView, plugin );

                final AdminWorkspace adminWorkspace = AdminJcrHome.getInstance(  )
                                                                  .findWorkspaceById( view.getWorkspaceId(  ), plugin );

                try
                {
                    RepositoryFileHome.getInstance(  )
                                      .doRecursive( adminWorkspace, view, view.getPath(  ),
                        new IndexerNodeAction( documentComparator, JcrPlugin.PLUGIN_NAME, adminWorkspace, strRole ),
                        new JsrUser( adminWorkspace.getUser(  ) ) );
                }
                catch ( BeansException e )
                {
                    AppLogService.error( e.getMessage(  ), e );
                }
                catch ( DataAccessException e )
                {
                    AppLogService.error( e.getMessage(  ), e );
                }
            }
        }
    }

    /**
     * Return lucene documents with id to strIdDocument, among all defined views.
     *
     * @return a list of Lucene documents
     */
    public List<Document> getDocuments( String strIdDocument )
    {
        List<Document> documents = new ArrayList<Document>(  );
        Plugin plugin = PluginService.getPlugin( JcrPlugin.PLUGIN_NAME );
        int nIdWorkspaceName = Integer.parseInt( strIdDocument.substring( 0, strIdDocument.indexOf( "-" ) ) );
        AdminWorkspace adminWorkspace = AdminJcrHome.getInstance(  ).findWorkspaceById( nIdWorkspaceName, plugin );

        if ( adminWorkspace == null )
        {
            return documents;
        }

        String strIdFile = strIdDocument.substring( strIdDocument.indexOf( "-" ) + 1 );
        IRepositoryFile file = RepositoryFileHome.getInstance(  )
                                                 .getRepositoryFileById( adminWorkspace, strIdFile,
                new JsrUser( adminWorkspace.getUser(  ) ) );

        if ( file == null )
        {
            return documents;
        }

        //indexing all portlet linked to the document
        Collection<AdminView> views = AdminJcrHome.getInstance(  )
                                                  .findAllViewsByWorkSpaceId( adminWorkspace.getId(  ), plugin );

        for ( AdminView view : views )
        {
            Collection<Jsr170Portlet> portlets = Jsr170PortletHome.getInstance(  ).findPortletsByViewId( view.getId(  ) );

            for ( Jsr170Portlet portlet : portlets )
            {
                Page page = PageHome.findByPrimaryKey( portlet.getId(  ) );
                IndexerNodeAction iNAction = new IndexerNodeAction( null, plugin.getName(  ), adminWorkspace,
                        page.getRole(  ) );
                documents.add( iNAction.doAction( file ) );
            }
        }

        return documents;
    }

    /**
     * @return the name of the indexer
     * @see fr.paris.lutece.portal.service.search.SearchIndexer#getName()
     */
    public String getName(  )
    {
        return this.getClass(  ).getSimpleName(  );
    }

    /**
     * @return the version of the indexer
     * @see fr.paris.lutece.portal.service.search.SearchIndexer#getVersion()
     */
    public String getVersion(  )
    {
        return INDEXER_VERSION;
    }

    /**
     * @return true if indexer is enabled
     * @see fr.paris.lutece.portal.service.search.SearchIndexer#isEnable()
     */
    public boolean isEnable(  )
    {
        if ( PluginService.isPluginEnable( JcrPlugin.PLUGIN_NAME ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
	public List<String> getListType(  )
	{
		List<String> listType = new ArrayList<String>(  );
		listType.add( JcrPlugin.PLUGIN_NAME );
		return listType;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSpecificSearchAppUrl(  )
	{
		return JSP_PAGE_SEARCH;
	}
}
