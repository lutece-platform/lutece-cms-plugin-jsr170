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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * An implementation of IAdminViewDAO
 */
public final class AdminViewDAO implements IAdminViewDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_view ) FROM jsr170_view ";
    private static final String SQL_QUERY_REMOVE_VIEW = "DELETE FROM jsr170_view WHERE id_view = ?";
    private static final String SQL_QUERY_INSERT_VIEW = "INSERT INTO jsr170_view (id_view ,id_workspace, workgroup, view_name, path) VALUES ( ? , ? , ?, ?, ? )";
    private static final String SQL_QUERY_SELECT_VIEW = "SELECT id_view, id_workspace, workgroup, view_name, path FROM jsr170_view WHERE id_view = ? ";
    private static final String SQL_QUERY_SELECT_ALL_VIEW = "SELECT id_view, id_workspace, workgroup, view_name, path FROM jsr170_view";
    private static final String SQL_QUERY_UPDATE_VIEW = "UPDATE jsr170_view SET id_workspace=?, workgroup=?, view_name=?, path=? WHERE id_view=?";
    private static final String SQL_QUERY_SELECT_VIEW_BY_WORKSPACE = "SELECT id_view, id_workspace, workgroup, view_name, path FROM jsr170_view WHERE id_workspace = ?";

    /** This class implements the Singleton design pattern. */
    private static AdminViewDAO _dao = new AdminViewDAO(  );

    ///////////////////////////////////////////////////////////////////////////////////////
    // Constructor and instanciation methods

    /**
     * Creates a new AdminViewDAO object.
     */
    private AdminViewDAO(  )
    {
    }

    /**
     * Returns the unique instance of the singleton.
     *
     * @return the instance
     */
    static IAdminViewDAO getInstance(  )
    {
        return _dao;
    }

    /**
     * @param adminView the adminview to insert
     * @param plugin the plugin
     * @see fr.paris.lutece.plugins.jcr.business.admin.IAdminViewDAO#insert(fr.paris.lutece.plugins.jcr.business.admin.AdminView, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void insert( AdminView adminView, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_VIEW, plugin );
        adminView.setId( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, adminView.getId(  ) );
        daoUtil.setInt( 2, adminView.getWorkspaceId(  ) );
        daoUtil.setString( 3, adminView.getWorkgroup(  ) );
        daoUtil.setString( 4, adminView.getName(  ) );
        daoUtil.setString( 5, adminView.getPath(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * @param id the id to delete
     * @param plugin the plugin
     * @see fr.paris.lutece.plugins.jcr.business.admin.IAdminViewDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int id, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_VIEW, plugin );
        daoUtil.setInt( 1, id );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * @param plugin the plugin
     * @return a collection with all AdminView, empty if no AdminView
     * @see fr.paris.lutece.plugins.jcr.business.admin.IAdminViewDAO#selectAll(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Collection<AdminView> selectAll( Plugin plugin )
    {
        ArrayList<AdminView> adminViewList = new ArrayList<AdminView>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_VIEW, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            adminViewList.add( resultSetToAdminView( daoUtil ) );
        }

        daoUtil.free(  );

        return adminViewList;
    }

    /**
     * @param id the id to load
     * @param plugin the plugin
     * @return an AdminView
     * @see fr.paris.lutece.plugins.jcr.business.admin.IAdminViewDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public AdminView load( int id, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_VIEW, plugin );
        daoUtil.setInt( 1, id );
        daoUtil.executeQuery(  );

        AdminView result = null;

        if ( daoUtil.next(  ) )
        {
            result = resultSetToAdminView( daoUtil );
        }

        daoUtil.free(  );

        return result;
    }

    /**
     * @param adminView the AdminView to update
     * @param plugin the plugin
     * @see fr.paris.lutece.plugins.jcr.business.admin.IAdminViewDAO#store(fr.paris.lutece.plugins.jcr.business.admin.AdminView, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( AdminView adminView, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_VIEW, plugin );
        daoUtil.setInt( 1, adminView.getWorkspaceId(  ) );
        daoUtil.setString( 2, adminView.getWorkgroup(  ) );
        daoUtil.setString( 3, adminView.getName(  ) );
        daoUtil.setString( 4, adminView.getPath(  ) );
        daoUtil.setInt( 5, adminView.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Generates a new primary key
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * @param dao an executedQuery DAOUtil
     * @return an AdminView filled with resultSet values
     */
    private AdminView resultSetToAdminView( DAOUtil dao )
    {
        AdminView result = new AdminView(  );
        result.setId( dao.getInt( 1 ) );
        result.setWorkspaceId( dao.getInt( 2 ) );
        result.setWorkgroup( dao.getString( 3 ) );
        result.setName( dao.getString( 4 ) );
        result.setPath( dao.getString( 5 ) );

        return result;
    }

    /**
     * @param nWorkspaceId the workspace id
     * @param plugin the plugin
     * @return a collection of AdminView
     * @see fr.paris.lutece.plugins.jcr.business.admin.IAdminViewDAO#selectAll(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Collection<AdminView> selectAll( int nWorkspaceId, Plugin plugin )
    {
        ArrayList<AdminView> adminViewList = new ArrayList<AdminView>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_VIEW_BY_WORKSPACE, plugin );
        daoUtil.setInt( 1, nWorkspaceId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            adminViewList.add( resultSetToAdminView( daoUtil ) );
        }

        daoUtil.free(  );

        return adminViewList;
    }
}
