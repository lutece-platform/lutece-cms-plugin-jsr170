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


public final class AdminWorkspaceDAO implements IAdminWorkspaceDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_workspace ) FROM jsr170_workspace ";
    private static final String SQL_QUERY_REMOVE_WORKSPACE = "DELETE FROM jsr170_workspace WHERE id_workspace = ?";
    private static final String SQL_QUERY_INSERT_WORKSPACE = "INSERT INTO jsr170_workspace (id_workspace, workspace_name, workgroup, jcr_type, workspace_label, user, password) VALUES ( ? , ? , ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_SELECT_WORKSPACE = "SELECT id_workspace, workspace_name, workgroup, jcr_type, workspace_label, user, password FROM jsr170_workspace WHERE id_workspace = ? ";
    private static final String SQL_QUERY_SELECT_ALL_WORKSPACE = "SELECT id_workspace, workspace_name, workgroup, jcr_type, workspace_label, user, password FROM jsr170_workspace";
    private static final String SQL_QUERY_UPDATE_WORKSPACE = "UPDATE jsr170_workspace SET workspace_name=?, workgroup=?, jcr_type=?, workspace_label=?, user=?, password=? WHERE id_workspace=?";

    /** This class implements the Singleton design pattern. */
    private static AdminWorkspaceDAO _dao = new AdminWorkspaceDAO(  );

    ///////////////////////////////////////////////////////////////////////////////////////
    // Constructor and instanciation methods

    /**
     * Creates a new AdminWorkspaceDAO object.
     */
    private AdminWorkspaceDAO(  )
    {
    }

    /**
     * Returns the unique instance of the singleton.
     *
     * @return the instance
     */
    static IAdminWorkspaceDAO getInstance(  )
    {
        return _dao;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.admin.IAdminWorkspaceDAO#insert(fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void insert( AdminWorkspace adminWorkspace, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_WORKSPACE, plugin );
        daoUtil.setInt( 1, newPrimaryKey( plugin ) );
        daoUtil.setString( 2, adminWorkspace.getName(  ) );
        daoUtil.setString( 3, adminWorkspace.getWorkgroup(  ) );
        daoUtil.setString( 4, adminWorkspace.getJcrType(  ) );
        daoUtil.setString( 5, adminWorkspace.getLabel(  ) );
        daoUtil.setString( 6, adminWorkspace.getUser(  ) );
        daoUtil.setString( 7, adminWorkspace.getPassword(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    public void delete( int id, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_WORKSPACE, plugin );
        daoUtil.setInt( 1, id );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    public Collection<AdminWorkspace> selectAll( Plugin plugin )
    {
        ArrayList<AdminWorkspace> adminWorkspaceList = new ArrayList<AdminWorkspace>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_WORKSPACE, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            adminWorkspaceList.add( resultSetToAdminWorkspace( daoUtil ) );
        }

        daoUtil.free(  );

        return adminWorkspaceList;
    }

    public AdminWorkspace load( int id, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WORKSPACE, plugin );
        daoUtil.setInt( 1, id );
        daoUtil.executeQuery(  );

        AdminWorkspace result = null;

        if ( daoUtil.next(  ) )
        {
            result = resultSetToAdminWorkspace( daoUtil );
        }

        daoUtil.free(  );

        return result;
    }

    public void store( AdminWorkspace adminWorkspace, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_WORKSPACE, plugin );
        daoUtil.setString( 1, adminWorkspace.getName(  ) );
        daoUtil.setString( 2, adminWorkspace.getWorkgroup(  ) );
        daoUtil.setString( 3, adminWorkspace.getJcrType(  ) );
        daoUtil.setString( 4, adminWorkspace.getLabel(  ) );
        daoUtil.setString( 5, adminWorkspace.getUser(  ) );
        daoUtil.setString( 6, adminWorkspace.getPassword(  ) );
        daoUtil.setInt( 7, adminWorkspace.getId(  ) );
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

    private AdminWorkspace resultSetToAdminWorkspace( DAOUtil dao )
    {
        AdminWorkspace result = new AdminWorkspace(  );
        result.setId( dao.getInt( 1 ) );
        result.setName( dao.getString( 2 ) );
        result.setWorkgroup( dao.getString( 3 ) );
        result.setJcrType( dao.getString( 4 ) );
        result.setLabel( dao.getString( 5 ) );
        result.setUser( dao.getString( 6 ) );
        result.setPassword( dao.getString( 7 ) );

        return result;
    }
}
