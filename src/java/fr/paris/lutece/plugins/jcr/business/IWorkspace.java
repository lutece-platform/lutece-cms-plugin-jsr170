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
package fr.paris.lutece.plugins.jcr.business;

import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.resource.Resource;


/**
 * the interface for workspace
 */
public interface IWorkspace extends Resource, RBACResource
{
    /**
     * Constant for read access mode
     */
    static final String READ_ACCESS = "read";

    /**
     * Constant for write access mode
     */
    static final String WRITE_ACCESS = "write";

    /**
     * Constant for remove access mode
     */
    static final String REMOVE_ACCESS = "remove";

    /**
     * Array containing all access modes
     */
    static final String[] AVAILABLE_ACCESS = new String[] { READ_ACCESS, WRITE_ACCESS, REMOVE_ACCESS };

    /**
     * Get the roles associated with the given access type
     * @param strAccessType the access type which must be in array AVAILABLE_ACCESS
     * @return an array of roles
     */
    String[] getRoles( String strAccessType );

    /**
     * Set the roles associated with the given access type
     * @param strAccessType the access type which must be in array AVAILABLE_ACCESS
     * @param roles an array of roles
     */
    void setRoles( String strAccessType, String[] roles );

    /**
     * Get the workspace name
     * @return the workspace name
     */
    String getName(  );

    /**
     * Set the workspace name
     * @param strName the workspace name
     */
    void setName( String strName );

    /**
     * Get the workspace id
     * @return the id
     */
    String getId(  );

    /**
     * Set the workspace id
     * @param strId the id
     */
    void setId( String strId );
}
