/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
import java.util.List;


public class AdminViewRoleDAO implements IAdminViewRoleDAO
{
    private static final String SQL_SELECT_BY_ID = "select role from jsr170_view_role where id_view = ? and access_right = ? order by role";
    private static final String SQL_SELECT_COUNT_BY_ROLE = "select count(*) from jsr170_view_role where role = ? ";
    private static final String SQL_INSERT_VIEW_ROLE = "insert into jsr170_view_role (id_view, access_right, role) values (?, ?, ?)";
    private static final String SQL_DELETE_BY_ID_AND_ACCESRIGHT = "delete from jsr170_view_role where id_view = ? and access_right = ?";

    public void delete( int id, String strAccessRight, Plugin plugin )
    {
        DAOUtil dao = new DAOUtil( SQL_DELETE_BY_ID_AND_ACCESRIGHT, plugin );
        dao.setInt( 1, id );
        dao.setString( 2, strAccessRight );
        dao.executeUpdate(  );
        dao.free(  );
    }

    public List<String> findByIdAndAccessRight( int id, String strAccessRight, Plugin plugin )
    {
        List<String> result = new ArrayList<String>(  );
        DAOUtil dao = new DAOUtil( SQL_SELECT_BY_ID, plugin );
        dao.setInt( 1, id );
        dao.setString( 2, strAccessRight );
        dao.executeQuery(  );

        while ( dao.next(  ) )
        {
            result.add( dao.getString( 1 ) );
        }

        dao.free(  );

        return result;
    }

    /**
     * Get the number of AdminViewRole linked with the specified Lutece Role
     * @param strRoleKey The Lutece Role key
     * @param plugin The plugin
     * @return the count of AdminViewRole
     */
    public int findByRole( String strRoleKey, Plugin plugin )
    {
        int nResult = 0;
        DAOUtil dao = new DAOUtil( SQL_SELECT_COUNT_BY_ROLE, plugin );
        dao.setString( 1, strRoleKey );
        dao.executeQuery(  );

        if ( dao.next(  ) )
        {
            nResult = dao.getInt( 1 );
        }

        dao.free(  );

        return nResult;
    }

    public void insert( int id, String strAccessRight, List<String> listRoles, Plugin plugin )
    {
        for ( String strRole : listRoles )
        {
            DAOUtil dao = new DAOUtil( SQL_INSERT_VIEW_ROLE, plugin );
            dao.setInt( 1, id );
            dao.setString( 2, strAccessRight );
            dao.setString( 3, strRole );
            dao.executeUpdate(  );
            dao.free(  );
        }
    }

    public void store( int id, String strAccessRight, List<String> listRoles, Plugin plugin )
    {
        delete( id, strAccessRight, plugin );
        insert( id, strAccessRight, listRoles, plugin );
    }
}
