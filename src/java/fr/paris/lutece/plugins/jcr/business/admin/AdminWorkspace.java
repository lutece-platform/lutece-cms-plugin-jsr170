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

import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.portal.service.workgroup.WorkgroupRemovalListenerService;


/**
 * An AdminWorkspace
 */
public class AdminWorkspace implements AdminWorkgroupResource
{
    private static AdminWorkspaceWorkgroupRemovalListener _listenerWorkgroup;
    private int _nId;
    private String _strName;
    private String _strWorkgroup;
    private String _strJcrType;
    private String _strLabel;
    private String _strUser;
    private String _strPassword;

    /**
     * Initialize the AdminWorkspace
     */
    public static synchronized void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerWorkgroup == null )
        {
            _listenerWorkgroup = new AdminWorkspaceWorkgroupRemovalListener(  );
            WorkgroupRemovalListenerService.getService(  ).registerListener( _listenerWorkgroup );
        }
    }

    /**
     * @return the label (description) of the workspace
     */
    public String getLabel(  )
    {
        return _strLabel;
    }

    /**
     * @param label the label
     */
    public void setLabel( String label )
    {
        this._strLabel = label;
    }

    /**
     * @return the unique id of this workspace
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * @param id the id
     */
    public void setId( int id )
    {
        this._nId = id;
    }

    /**
     * @return the JCR type of this workspace
     */
    public String getJcrType(  )
    {
        return _strJcrType;
    }

    /**
     * @param jcrType the jcr type
     */
    public void setJcrType( String jcrType )
    {
        this._strJcrType = jcrType;
    }

    /**
     * @return the name of the workspace in th JCR
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * @param name the name
     */
    public void setName( String name )
    {
        this._strName = name;
    }

    /**
     * @return the allowed workgroup
     * @see fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource#getWorkgroup()
     */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * @param workgroup the allowed workgroup
     */
    public void setWorkgroup( String workgroup )
    {
        this._strWorkgroup = workgroup;
    }

    /**
     * @return the password
     */
    public String getPassword(  )
    {
        return _strPassword;
    }

    /**
     * @param password the value to set
     */
    public void setPassword( String password )
    {
        this._strPassword = password;
    }

    /**
     * @return the user login
     */
    public String getUser(  )
    {
        return _strUser;
    }

    /**
     * @param user the user login
     */
    public void setUser( String user )
    {
        this._strUser = user;
    }
}
