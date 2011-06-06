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

import fr.paris.lutece.portal.service.role.RoleRemovalListenerService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.portal.service.workgroup.WorkgroupRemovalListenerService;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 * Maps a workspace on a given JCR.
 *
 * It defines additionnal informations for the connection (login/password).
 */
public class AdminView implements AdminWorkgroupResource
{
    private static final String TAG_ADMIN_VIEW_ELEMENT = "admin-view";
    private static final String TAG_ADMIN_VIEW_ID = "admin-view-id";
    private static final String TAG_ADMIN_VIEW_NAME = "admin-view-name";
    private static AdminViewWorkgroupRemovalListener _listenerWorkgroup;
    private static AdminViewRoleRoleRemovalListener _listenerRole;
    private int _nId;
    private int _nWorkspaceId;
    private String _strWorkgroup;
    private String _strName;
    private String _strPath;

    /**
     * Initialize the AdminView
     */
    public static synchronized void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerWorkgroup == null )
        {
            _listenerWorkgroup = new AdminViewWorkgroupRemovalListener(  );
            WorkgroupRemovalListenerService.getService(  ).registerListener( _listenerWorkgroup );
        }

        if ( _listenerRole == null )
        {
            _listenerRole = new AdminViewRoleRoleRemovalListener(  );
            RoleRemovalListenerService.getService(  ).registerListener( _listenerRole );
        }
    }

    /**
     * @return a reference to the root node (UUID) or null if undef
     */
    public String getPath(  )
    {
        return _strPath;
    }

    /**
     * @param path the UUID
     */
    public void setPath( String path )
    {
        _strPath = path;
    }

    /**
    * @return the id of this view
    */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * @param id the id of this view
     */
    public void setId( int id )
    {
        this._nId = id;
    }

    /**
     * @return the name of this view
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * @param name the name of this view
     */
    public void setName( String name )
    {
        this._strName = name;
    }

    /**
     * @return the workgroup allowed for this view
     * @see fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource#getWorkgroup()
     */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * @param workgroup the workgroup
     */
    public void setWorkgroup( String workgroup )
    {
        this._strWorkgroup = workgroup;
    }

    /**
     * @return the id of the workspace associated with this view
     */
    public int getWorkspaceId(  )
    {
        return _nWorkspaceId;
    }

    /**
     * @param workspaceId the workspace id
     */
    public void setWorkspaceId( int workspaceId )
    {
        this._nWorkspaceId = workspaceId;
    }

    /**
     * Returns the xml representation of this adminview
     * @return an xml string
     */
    public String getXml(  )
    {
        StringBuffer sbXml = new StringBuffer(  );
        XmlUtil.beginElement( sbXml, TAG_ADMIN_VIEW_ELEMENT );
        XmlUtil.addElement( sbXml, TAG_ADMIN_VIEW_ID, _nId );
        XmlUtil.addElement( sbXml, TAG_ADMIN_VIEW_NAME, _strName );
        XmlUtil.endElement( sbXml, TAG_ADMIN_VIEW_ELEMENT );

        return sbXml.toString(  );
    }
}
