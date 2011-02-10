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
package fr.paris.lutece.plugins.jcr.business;

import fr.paris.lutece.portal.service.util.AppLogService;

import org.springmodules.jcr.JcrTemplate;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * An IRepositoryFileDAO implementation for Alfresco JCR
 */
public class RemoteAlfrescoRepositoryFileDAO extends AlfrescoRepositoryFileDAO
{
    /** This class implements the Singleton design pattern. */
    private static RemoteAlfrescoRepositoryFileDAO _dao;

    protected RemoteAlfrescoRepositoryFileDAO(  )
    {
        super(  );
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Constructor and instanciation methods

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
        if ( _dao == null )
        {
            _dao = new RemoteAlfrescoRepositoryFileDAO(  );
            _dao.init( jcrTemplate, repositoryInitializer, strDefaultWorkspaceName );
        }

        return _dao;
    }

    /**
     * Get the size of node content
     * @param node the node
     * @return the size in bytes
     */
    protected long getSize( Node node )
    {
        long size = 0L;
        InputStream fileContent = getFileContent( node );
        long skip;

        try
        {
            byte[] buf = new byte[1024];

            do
            {
                skip = fileContent.read( buf );
                size += skip;
            }
            while ( skip > 0L );
        }
        catch ( IOException e )
        {
            AppLogService.debug( "Error getting size for node " + node + " (" + e.getLocalizedMessage(  ) + ")" );
        }
        finally
        {
            try
            {
                fileContent.close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return size;
    }

    protected boolean isVersionnable( Node node )
    {
        String strMixinVersionnable = getProperties(  ).getProperty( MIXIN_VERSIONNABLE );

        if ( strMixinVersionnable == null )
        {
            return false;
        }

        try
        {
            return node.isNodeType( strMixinVersionnable );
        }
        catch ( RepositoryException e )
        {
            // nothing to do, we just log the exception
            AppLogService.debug( "Node not versionable : " + node + " (" + e.getLocalizedMessage(  ) + ")" );
        }

        return false;
    }

    @Override
    public void releaseSession( Session session )
    {
        AppLogService.debug( "Release remote Alfresco session unavailable " );
    }
}
