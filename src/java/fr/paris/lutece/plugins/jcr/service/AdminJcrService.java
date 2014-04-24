/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.jcr.service;

import fr.paris.lutece.plugins.jcr.authentication.JsrUser;
import fr.paris.lutece.plugins.jcr.business.RepositoryFileHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminJcrHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminView;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.util.JcrExistingViews;
import fr.paris.lutece.plugins.jcr.util.JcrUserAccessDeniedException;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;

import org.springframework.dao.DataAccessResourceFailureException;

import javax.jcr.NoSuchWorkspaceException;


/**
 * This Service manage AdminView and AdminWorkspace
 */
public class AdminJcrService
{
    private static AdminJcrService _singleton = new AdminJcrService(  );
    private Plugin _plugin;

    /**
    * Initialize the Jcr service
    *
    */
    public void init( Plugin plugin )
    {
        _plugin = plugin;
        AdminView.init(  );

        AdminWorkspace.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static AdminJcrService getInstance(  )
    {
        return _singleton;
    }

    public void createWorkspace( String strJcrType, String strWorkspaceName, String strWorkspaceLabel, String strUser,
        String strWorkgroup, String strPassword )
    {
        RepositoryFileHome repositoryFileHome = RepositoryFileHome.getInstance(  );

        if ( repositoryFileHome.canCreateWorkspace( strJcrType ) )
        {
            repositoryFileHome.createWorkspace( strJcrType, strWorkspaceName );

            AdminWorkspace workspace = new AdminWorkspace(  );
            workspace.setJcrType( strJcrType );
            workspace.setName( strWorkspaceName );
            workspace.setWorkgroup( strWorkgroup );
            workspace.setLabel( strWorkspaceLabel );
            workspace.setUser( strUser );
            workspace.setPassword( strPassword );
            AdminJcrHome.getInstance(  ).createWorkspace( workspace, getPlugin(  ) );
        }
    }

    public void removeWorkspace( int nWorkspaceId, AdminUser user )
    {
        AdminJcrHome adminJcrHome = AdminJcrHome.getInstance(  );

        AdminWorkspace workspace = adminJcrHome.findWorkspaceById( nWorkspaceId, getPlugin(  ) );
        RepositoryFileHome repositoryFileHome = RepositoryFileHome.getInstance(  );

        // check if user is allowed and if workspaces are deletable
        if ( !AdminWorkgroupService.isAuthorized( workspace, user ) ||
                !repositoryFileHome.canCreateWorkspace( workspace.getJcrType(  ) ) )
        {
            throw new JcrUserAccessDeniedException( (Object) repositoryFileHome );
        }

        if ( adminJcrHome.existsViewWithWorkspaceId( nWorkspaceId, getPlugin(  ) ) )
        {
            throw new JcrExistingViews( workspace );
        }

        try
        {
            repositoryFileHome.removeWorkspace( workspace, new JsrUser( user ) );
        }
        catch ( DataAccessResourceFailureException e )
        {
            // do nothing only if the the workspace is not found
            if ( !e.contains( NoSuchWorkspaceException.class ) )
            {
                throw e;
            }
        }

        AdminJcrHome.getInstance(  ).deleteWorkspace( nWorkspaceId, getPlugin(  ) );
    }

    private Plugin getPlugin(  )
    {
        return _plugin;
    }
}
