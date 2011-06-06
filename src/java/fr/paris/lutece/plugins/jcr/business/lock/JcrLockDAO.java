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
package fr.paris.lutece.plugins.jcr.business.lock;

import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Lock DAO implementation
 */
public class JcrLockDAO implements IJcrLockDAO
{
    private static final String SQL_QUERY_REMOVE_LOCK = "DELETE FROM jsr170_lock WHERE id_workspace = ? AND id_lock = ?";
    private static final String SQL_QUERY_INSERT_LOCK = "INSERT INTO jsr170_lock (id_lock ,id_workspace, id_user, id_file) VALUES ( ? , ? , ? , ? )";
    private static final String SQL_QUERY_SELECT_LOCK = "SELECT id_lock, id_workspace, id_user, creation_date, id_file FROM jsr170_lock ";

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.lock.IJcrLockDAO#create(fr.paris.lutece.plugins.jcr.business.lock.JcrLock)
     */
    public void create( JcrLock lock )
    {
        // TODO Auto-generated method stub
        DAOUtil dao = new DAOUtil( SQL_QUERY_INSERT_LOCK );
        dao.setString( 1, lock.getIdLock(  ) );
        dao.setInt( 2, lock.getIdWorkspace(  ) );
        dao.setString( 3, lock.getIdUser(  ) );
        dao.setString( 4, lock.getIdFile(  ) );
        dao.executeUpdate(  );
        dao.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.lock.IJcrLockDAO#delete(java.lang.String, int)
     */
    public void delete( String strLockToken, int nIdWorkspace )
    {
        DAOUtil dao = new DAOUtil( SQL_QUERY_REMOVE_LOCK );
        dao.setInt( 1, nIdWorkspace );
        dao.setString( 2, strLockToken );
        dao.executeUpdate(  );
        dao.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.lock.IJcrLockDAO#findByCriterias(fr.paris.lutece.plugins.jcr.business.lock.JcrLock)
     */
    public Collection<JcrLock> findByCriterias( JcrLock criterias )
    {
        ArrayList<JcrLock> results = new ArrayList<JcrLock>(  );
        String strQuery = SQL_QUERY_SELECT_LOCK + lockToSQL( criterias );
        DAOUtil dao = new DAOUtil( strQuery );
        prepareQuery( dao, criterias );

        dao.executeQuery(  );

        JcrLock lock;

        while ( dao.next(  ) )
        {
            lock = new JcrLock(  );
            lock.setIdLock( dao.getString( "id_lock" ) );
            lock.setIdUser( dao.getString( "id_user" ) );
            lock.setIdWorkspace( dao.getInt( "id_workspace" ) );
            lock.setIdFile( dao.getString( "id_file" ) );
            lock.setCreationDate( dao.getDate( "creation_date" ) );
            results.add( lock );
        }

        dao.free(  );

        return results;
    }

    public void store( JcrLock lock )
    {
        // TODO Auto-generated method stub
    }

    private String lockToSQL( JcrLock lock )
    {
        if ( lock.isEmpty(  ) )
        {
            return "";
        }

        StringBuffer sb = new StringBuffer(  );

        buildQuery( sb, "id_file", lock.getIdFile(  ),
            buildQuery( sb, "id_lock", lock.getIdLock(  ),
                buildQuery( sb, "id_user", lock.getIdUser(  ),
                    buildQuery( sb, "id_workspace", ( lock.getIdWorkspace(  ) == -1 ) ? null : lock.getIdWorkspace(  ),
                        true ) ) ) );

        return sb.toString(  );
    }

    private <T> boolean buildQuery( StringBuffer sb, String strFieldName, T value, boolean first )
    {
        boolean bFirst = first;

        if ( value != null )
        {
            if ( bFirst )
            {
                bFirst = false;
                sb.append( "WHERE " );
            }
            else
            {
                sb.append( " AND " );
            }

            sb.append( strFieldName );
            sb.append( "= ?" );
        }

        return bFirst;
    }

    private void prepareQuery( DAOUtil dao, JcrLock lock )
    {
        if ( lock.isEmpty(  ) )
        {
            return;
        }

        prepareQuery( dao, lock.getIdFile(  ),
            prepareQuery( dao, lock.getIdLock(  ),
                prepareQuery( dao, lock.getIdUser(  ),
                    prepareQuery( dao, ( lock.getIdWorkspace(  ) == -1 ) ? null : lock.getIdWorkspace(  ), 1 ) ) ) );
    }

    private <T> int prepareQuery( DAOUtil dao, T value, int nPosition )
    {
        int nPositionRes = nPosition;

        if ( value != null )
        {
            if ( value instanceof Integer )
            {
                dao.setInt( nPositionRes, (Integer) value );
            }
            else if ( value instanceof String )
            {
                dao.setString( nPositionRes, (String) value );
            }

            nPositionRes++;
        }

        return nPositionRes;
    }
}
