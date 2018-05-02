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
package fr.paris.lutece.plugins.jcr.business.admin;

import fr.paris.lutece.plugins.jcr.business.IWorkspace;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Home class for JCR administration
 */
public final class AdminJcrHome
{
    public static final String ROLE_NONE = "none";
    private static IAdminViewDAO _adminViewDao = (IAdminViewDAO) SpringContextService.getPluginBean( "jsr170",
            "jsr170.adminViewDAO" );
    private static IAdminWorkspaceDAO _adminWorkspaceDao = (IAdminWorkspaceDAO) SpringContextService.getPluginBean( "jsr170",
            "jsr170.adminWorkspaceDAO" );
    private static IAdminViewRoleDAO _adminViewRoleDao = (IAdminViewRoleDAO) SpringContextService.getPluginBean( "jsr170",
            "jsr170.adminViewRoleDAO" );
    private static AdminJcrHome _instance;
    private static final List<String> _listDefaultAccess;

    static
    {
        _listDefaultAccess = new ArrayList<String>(  );
        _listDefaultAccess.add( "none" );
    }

    /**
     * Private constructor
     */
    private AdminJcrHome(  )
    {
    }

    /**
     * @return an instance of AdminJcrHome
     */
    public static AdminJcrHome getInstance(  )
    {
        if ( _instance == null )
        {
            _instance = new AdminJcrHome(  );
        }

        return _instance;
    }

    /**
     * @param workspace the workspace to create
     * @param plugin the plugin
     */
    public void createWorkspace( AdminWorkspace workspace, Plugin plugin )
    {
        _adminWorkspaceDao.insert( workspace, plugin );
    }

    /**
     * @param workspace the workspace to modify
     * @param plugin the plugin
     */
    public void modifyWorkspace( AdminWorkspace workspace, Plugin plugin )
    {
        _adminWorkspaceDao.store( workspace, plugin );
    }

    /**
     * @param id of the workspace to delete
     * @param plugin the plugin
     */
    public void deleteWorkspace( int id, Plugin plugin )
    {
        _adminWorkspaceDao.delete( id, plugin );
    }

    /**
     * @param view the view to create
     * @param plugin the plugin
     */
    public void createView( AdminView view, Plugin plugin )
    {
        _adminViewDao.insert( view, plugin );
        _adminViewRoleDao.insert( view.getId(  ), IWorkspace.READ_ACCESS, _listDefaultAccess, plugin );
        _adminViewRoleDao.insert( view.getId(  ), IWorkspace.WRITE_ACCESS, _listDefaultAccess, plugin );
        _adminViewRoleDao.insert( view.getId(  ), IWorkspace.REMOVE_ACCESS, _listDefaultAccess, plugin );
    }

    /**
     * @param view the view to modify
     * @param plugin the plugin
     */
    public void modifyView( AdminView view, Plugin plugin )
    {
        _adminViewDao.store( view, plugin );
    }

    /**
     * @param id the id of the view to delete
     * @param plugin the plugin
     */
    public void deleteView( int id, Plugin plugin )
    {
        _adminViewDao.delete( id, plugin );
        _adminViewRoleDao.delete( id, IWorkspace.READ_ACCESS, plugin );
        _adminViewRoleDao.delete( id, IWorkspace.WRITE_ACCESS, plugin );
        _adminViewRoleDao.delete( id, IWorkspace.REMOVE_ACCESS, plugin );
    }

    /**
     * @param plugin the plugin
     * @return all available views
     */
    public Collection<AdminView> findAllViews( Plugin plugin )
    {
        return _adminViewDao.selectAll( plugin );
    }

    /**
     * @param plugin the plugin
     * @param nWorkspaceId the workspace id to find
     * @return all available views
     */
    public Collection<AdminView> findAllViewsByWorkSpaceId( int nWorkspaceId, Plugin plugin )
    {
        return _adminViewDao.selectAll( nWorkspaceId, plugin );
    }

    /**
     * @param plugin the plugin
     * @return all available workspaces
     */
    public Collection<AdminWorkspace> findAllWorkspaces( Plugin plugin )
    {
        return _adminWorkspaceDao.selectAll( plugin );
    }

    /**
     * @param plugin the plugin
     * @return a map of all workspaces
     */
    public Map<String, AdminWorkspace> getWorkspacesList( Plugin plugin )
    {
        HashMap<String, AdminWorkspace> result = new HashMap<String, AdminWorkspace>(  );

        for ( AdminWorkspace workspace : _adminWorkspaceDao.selectAll( plugin ) )
        {
            result.put( String.valueOf( workspace.getId(  ) ), workspace );
        }

        return result;
    }

    /**
     * @param plugin the plugin
     * @return a map of all workspaces
     */
    public Map<String, AdminWorkspace> getWorkspacesListByNameKey( Plugin plugin )
    {
        HashMap<String, AdminWorkspace> result = new HashMap<String, AdminWorkspace>(  );

        for ( AdminWorkspace workspace : _adminWorkspaceDao.selectAll( plugin ) )
        {
            result.put( String.valueOf( workspace.getName(  ) ), workspace );
        }

        return result;
    }

    /**
     * @param nWorkspaceId the workspace id to find
     * @param plugin the plugin
     * @return an AdminWorkspace
     */
    public AdminWorkspace findWorkspaceById( int nWorkspaceId, Plugin plugin )
    {
        return _adminWorkspaceDao.load( nWorkspaceId, plugin );
    }

    /**
     * @param nViewId the view ID to fin
     * @param plugin the plugin
     * @return the adminView
     */
    public AdminView findViewById( int nViewId, Plugin plugin )
    {
        return _adminViewDao.load( nViewId, plugin );
    }

    /**
     * @param nViewId the view ID to search the jcr type for
     * @param plugin the plugin
     * @return the jcr type
     */
    public String findJcrType( int nViewId, Plugin plugin )
    {
        AdminView view = findViewById( nViewId, plugin );
        AdminWorkspace workspace = findWorkspaceById( view.getWorkspaceId(  ), plugin );

        return workspace.getJcrType(  );
    }

    /**
     * Test if given workspace has attached views
     * @param nWorkspaceId the workspace id
     * @param plugin the plugin
     * @return true if there are some views attached
     */
    public boolean existsViewWithWorkspaceId( int nWorkspaceId, Plugin plugin )
    {
        return !_adminViewDao.selectAll( nWorkspaceId, plugin ).isEmpty(  );
    }

    /**
     * Computes authorizations
     *
     * @param adminView the view
     * @param userRoles an array of roles to test
     * @param strAccessRight the permission to check
     * @param plugin the plugin
     * @return true if user if authorized
     */
    public Boolean getAvailableAccess( AdminView adminView, String[] userRoles, String strAccessRight, Plugin plugin )
    {
        List<String> authorizedRoles = _adminViewRoleDao.findByIdAndAccessRight( adminView.getId(  ), strAccessRight,
                plugin );

        for ( String strRole : userRoles )
        {
            if ( ( Collections.binarySearch( authorizedRoles, ROLE_NONE ) >= 0 ) ||
                    ( Collections.binarySearch( authorizedRoles, strRole ) >= 0 ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Update roles of a view
     *
     * @param adminView the view
     * @param strAccess the permission
     * @param roles the roles to attach
     * @param plugin the plugin
     */
    public void updateRoles( AdminView adminView, String strAccess, String[] roles, Plugin plugin )
    {
        _adminViewRoleDao.store( adminView.getId(  ), strAccess,
            ( roles == null ) ? new ArrayList<String>(  ) : Arrays.asList( roles ), plugin );
    }

    /**
     * Returns a list of roles authorized on this view
     * @param adminView the vie
     * @param strAccess the permission
     * @param plugin the plugin
     * @return an array of roles
     */
    public String[] getAuthorizedRoles( AdminView adminView, String strAccess, Plugin plugin )
    {
        return _adminViewRoleDao.findByIdAndAccessRight( adminView.getId(  ), strAccess, plugin )
                                .toArray( new String[] {  } );
    }

    /**
     * Get the number of AdminViewRole linked with the specified Lutece Role
     * @param strRoleKey The Lutece Role key
     * @param plugin The plugin
     * @return the count of AdminViewRole
     */
    public int countAdminViewRoleByRole( String strRoleKey, Plugin plugin )
    {
        return _adminViewRoleDao.findByRole( strRoleKey, plugin );
    }
}
