/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * Implementation of Alfresco JSR-170 browsing
 */
public class AlfrescoRepositoryWorkspaceDAO extends AbstractRepositoryDAO implements IWorkspaceDAO
{
    /** This class implements the Singleton design pattern. */
    private static AlfrescoRepositoryWorkspaceDAO _dao;

    ///////////////////////////////////////////////////////////////////////////////////////
    // Constructor and instantiation methods

    /**
     * Creates a new JcrRepositoryFileDAO object.
     * @param jcrTemplate a jcrTemplate
     * @param repositoryInitializer a repositoryInitializer
     * @param strDefaultWorkspaceName a default workspace name
     * @return an instance of this DAO
     */
    public static synchronized IWorkspaceDAO getInstance( JcrTemplate jcrTemplate,
        IRepositoryInitializer repositoryInitializer, String strDefaultWorkspaceName )
    {
        if ( _dao == null )
        {
            _dao = new AlfrescoRepositoryWorkspaceDAO(  );
            _dao.init( jcrTemplate, repositoryInitializer, null );
        }

        return _dao;
    }

    /**
     * Not available
     * @param strWorkspace the workspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#create(java.lang.String)
     */
    public void create( final String strWorkspace )
    {
        // not implemented
    }

    /**
     * Not available
     * @param strWorkspace the workspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#delete(java.lang.String)
     */
    public void delete( String strWorkspace )
    {
        // not implemented
    }

    /**
     * Not available
     * @param id the id
     * @return a workspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#findById(java.lang.String)
     */
    public IWorkspace findById( String id )
    {
        // not implemented
        return null;
    }

    /**
     * @param strWorkspace the workspace name
     * @return the workspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#findByName(java.lang.String)
     */
    public IWorkspace findByName( final String strWorkspace )
    {
        return (IWorkspace) execute( new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    JcrRepositoryWorkspaceImpl workspace = new JcrRepositoryWorkspaceImpl(  );
                    workspace.setRoles( IWorkspace.READ_ACCESS, new String[] { "none" } );
                    workspace.setRoles( IWorkspace.WRITE_ACCESS, new String[] {  } );
                    workspace.setRoles( IWorkspace.REMOVE_ACCESS, new String[] {  } );
                    workspace.setName( strWorkspace );
                    workspace.setId( strWorkspace );

                    return workspace;
                }
            } );
    }

    /**
     * @return all available workspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#getAvailableWorkspaces()
     */
    public String[] getAvailableWorkspaces(  )
    {
        return (String[]) execute( new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    return session.getWorkspace(  ).getAccessibleWorkspaceNames(  );
                }
            } );
    }

    /**
     * Not available
     * @param workspace the workspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#store(fr.paris.lutece.plugins.jcr.business.IWorkspace)
     */
    public void store( final IWorkspace workspace )
    {
        // not implemented
    }

    /**
     * @return always false
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#canCreate()
     */
    public boolean canCreate(  )
    {
        return false;
    }

    @Override
    public void releaseSession( Session session )
    {
        AppLogService.debug( "Release remote Alfresco session unavailable " );
    }
}
