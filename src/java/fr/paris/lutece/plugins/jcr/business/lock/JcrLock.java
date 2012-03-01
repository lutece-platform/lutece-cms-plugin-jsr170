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
package fr.paris.lutece.plugins.jcr.business.lock;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Credentials;


/**
 * Implmentation of lock mechanism in JCR
 */
public class JcrLock implements Credentials
{
    private static final long serialVersionUID = 7675667069458507070L;
    private String _strIdUser;
    private int _nIdWorkspace = -1;
    private String _strIdLock;
    private String _strIdFile;
    private Date _creationDate;

    /**
     * The owner of the lock
     * @return the user id
     */
    public String getIdUser(  )
    {
        return _strIdUser;
    }

    /**
     * The owner of the lock
     * @param idUser the user id
     */
    public void setIdUser( String idUser )
    {
        this._strIdUser = idUser;
    }

    /**
     * The workspace which contains the lock
     * @return the workspace id
     */
    public int getIdWorkspace(  )
    {
        return _nIdWorkspace;
    }

    /**
     * The workspace which contains the lock
     * @param idWorkspace the workspace id
     */
    public void setIdWorkspace( int idWorkspace )
    {
        this._nIdWorkspace = idWorkspace;
    }

    /**
     * The id of the lock
     * @return the lock id
     */
    public String getIdLock(  )
    {
        return _strIdLock;
    }

    /**
     * The id of the lock
     * @param idLock the lock id
     */
    public void setIdLock( String idLock )
    {
        this._strIdLock = idLock;
    }

    /**
     * @return true if a field is filled, false otherwise
     */
    public boolean isEmpty(  )
    {
        return ( _strIdLock == null ) && ( _strIdUser == null ) && ( _nIdWorkspace == -1 ) && ( _strIdFile == null );
    }

    /**
     * @return a string representation of this lock
     */
    public String toString(  )
    {
        return "[Lock : <" + _strIdLock + ">, <" + _nIdWorkspace + ">, <" + _strIdUser + ">, <" + _strIdFile + ">]";
    }

    /**
     * @return the reference of this file
     */
    public String getIdFile(  )
    {
        return _strIdFile;
    }

    /**
     * @param idFile the reference of this file
     */
    public void setIdFile( String idFile )
    {
        this._strIdFile = idFile;
    }

    /**
     * @return the creation date of this lock
     */
    public Date getCreationDate(  )
    {
        return _creationDate;
    }

    /**
     * @param creationDate the creation date of this lock
     */
    public void setCreationDate( Date creationDate )
    {
        this._creationDate = creationDate;
    }
}
