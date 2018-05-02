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
package fr.paris.lutece.plugins.jcr.business.portlet;

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * this class provides Data Access methods for Jsr170Portlet objects
 */
public final class Jsr170PortletDAO implements IPortletInterfaceDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet, id_view FROM jsr170_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO jsr170_portlet ( id_portlet, id_view ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM jsr170_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE jsr170_portlet SET id_portlet = ?, id_view = ? WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT_BY_VIEWID = "SELECT id_portlet, id_view FROM jsr170_portlet WHERE id_view = ? ";
    private static final String SQL_QUERY_SELECT_BY_PORTLET_ID = "SELECT id_portlet, id_view FROM jsr170_portlet WHERE id_portlet = ? ";

    /** This class implements the Singleton design pattern. */
    private static Jsr170PortletDAO _dao = new Jsr170PortletDAO(  );

    ///////////////////////////////////////////////////////////////////////////////////////
    // Constructor and instanciation methods

    /**
     * Creates a new Jsr170PortletDAO object.
     */
    private Jsr170PortletDAO(  )
    {
    }

    /**
     * Returns the unique instance of the singleton.
     *
     * @return the instance
     */
    static Jsr170PortletDAO getInstance(  )
    {
        return _dao;
    }

    /**
     * Delete record from table
     *
     * @param nPortletId The indentifier of the Portlet
     */
    public void delete( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nPortletId );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Access methods to data

    /**
     * Insert a new record in the table.
     *
     * @param portlet The Instance of the Portlet
     */
    public void insert( Portlet portlet )
    {
        Jsr170Portlet p = (Jsr170Portlet) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setInt( 1, p.getId(  ) );

        if ( p.hasDefaultView(  ) )
        {
            daoUtil.setInt( 2, p.getDefaultView(  ) );
        }
        else
        {
            daoUtil.setInt( 2, -1 );
        }

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of Jsr170Portlet from the table
     *
     * @param nIdPortlet The indentifier of the  portlet
     *
     * @return portlet The instance of the object portlet
     */
    public Portlet load( int nIdPortlet )
    {
        Jsr170Portlet portlet = new Jsr170Portlet(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nIdPortlet );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            portlet.setId( daoUtil.getInt( 1 ) );

            int view = daoUtil.getInt( 2 );

            if ( view > 0 )
            {
                portlet.setDefaultView( view );
            }
        }

        daoUtil.free(  );

        return portlet;
    }

    /**
     * Update the record in the table
     *
     * @param portlet The reference of the portlet
     */
    public void store( Portlet portlet )
    {
        Jsr170Portlet p = (Jsr170Portlet) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setInt( 1, p.getId(  ) );
        daoUtil.setInt( 2, p.getDefaultView(  ) );
        daoUtil.setInt( 3, p.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Find all portlets attached to a view
     * @param nViewId the view Id
     * @return a collection of portlets
     */
    public Collection<Jsr170Portlet> selectByViewId( int nViewId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_VIEWID );
        daoUtil.setInt( 1, nViewId );
        daoUtil.executeQuery(  );

        ArrayList<Jsr170Portlet> result = new ArrayList<Jsr170Portlet>(  );

        while ( daoUtil.next(  ) )
        {
            Jsr170Portlet portlet = new Jsr170Portlet(  );
            portlet.setId( daoUtil.getInt( 1 ) );

            int view = daoUtil.getInt( 2 );

            if ( view > 0 )
            {
                portlet.setDefaultView( view );
            }

            result.add( portlet );
        }

        daoUtil.free(  );

        return result;
    }

    public Jsr170Portlet findByPortletId( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PORTLET_ID );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery(  );

        Jsr170Portlet result = new Jsr170Portlet(  );

        while ( daoUtil.next(  ) )
        {
            Jsr170Portlet portlet = new Jsr170Portlet(  );
            portlet.setId( daoUtil.getInt( 1 ) );

            int view = daoUtil.getInt( 2 );

            if ( view > 0 )
            {
                portlet.setDefaultView( view );
            }

            result = portlet;
        }

        daoUtil.free(  );

        return result;
    }
}
