/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.jcr.business;

import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of IWorkspace
 */
public class JcrRepositoryWorkspaceImpl implements IWorkspace
{
    private Map<String, String[]> _mapRoles;
    private String _strId;
    private String _strName;

    /**
     * Basic constructor
     */
    public JcrRepositoryWorkspaceImpl(  )
    {
        _mapRoles = new HashMap<String, String[]>(  );
        _mapRoles.put( READ_ACCESS, new String[0] );
        _mapRoles.put( REMOVE_ACCESS, new String[0] );
        _mapRoles.put( WRITE_ACCESS, new String[0] );
    }

    /**
     * @return the id
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspace#getId()
     */
    public String getId(  )
    {
        return _strId;
    }

    /**
     * @return the name
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspace#getName()
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * @param strAccessType an access type code
     * @return all available roles for this access type
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspace#getRoles(java.lang.String)
     */
    public String[] getRoles( String strAccessType )
    {
        return _mapRoles.get( strAccessType );
    }

    /**
     * @param strId the id
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspace#setId(java.lang.String)
     */
    public void setId( String strId )
    {
        _strId = strId;
    }

    /**
     * @param strName the name
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspace#setName(java.lang.String)
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * @param strAccessType the access type code
     * @param roles an array of roles
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspace#setRoles(java.lang.String, java.lang.String[])
     */
    public void setRoles( String strAccessType, String[] roles )
    {
        _mapRoles.put( strAccessType, roles );
    }

    /**
     * @return the resource id
     * @see fr.paris.lutece.portal.service.rbac.RBACResource#getResourceId()
     */
    public String getResourceId(  )
    {
        return null;
    }

    /**
     * @return a resource type code
     * @see fr.paris.lutece.portal.service.rbac.RBACResource#getResourceTypeCode()
     */
    public String getResourceTypeCode(  )
    {
        return null;
    }

    @Override
    public String toString(  )
    {
        return "workspace=(" + _strId + ", " + _strName + ")";
    }
}
