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
package fr.paris.lutece.plugins.jcr.authentication;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.security.LuteceUser;

import java.security.Principal;


/**
 * Simple LuteceUser implementation for JCR
 */
public class JsrUser implements Principal
{
    public static final JsrUser ANONYMOUS;
    private static final String ANONYMOUS_NAME = "anonymous";

    static
    {
        ANONYMOUS = new JsrUser(  );
        ANONYMOUS._isAnonymous = true;
        ANONYMOUS._strName = ANONYMOUS_NAME;
    }

    private boolean _isAnonymous = false;
    private String _strName;
    private boolean _isAdminUser;
    private LuteceUser _user;
    private AdminUser _adminUser;

    /**
     * Empty constructor
     */
    private JsrUser(  )
    {
    }

    public JsrUser( String strLogin )
    {
        _strName = strLogin;
    }

    /**
     * Constructor from a LuteceUser
     * @param user a valid LuteceUser
     */
    public JsrUser( LuteceUser user )
    {
        if ( user == null )
        {
            _isAnonymous = true;
        }
        else
        {
            _user = user;
            _isAdminUser = false;
            _strName = _user.getName(  );
        }
    }

    /**
     * Constructor from a LuteceUser
     * @param user a valid AdminUser
     */
    public JsrUser( AdminUser user )
    {
        _adminUser = user;
        _isAdminUser = true;
        _strName = _adminUser.getAccessCode(  );
    }

    /* (non-Javadoc)
     * @see java.security.Principal#getName()
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * @return true if user is not connected
     */
    public boolean isAnonymous(  )
    {
        return _isAnonymous;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(  )
    {
        return "JsrUser(" + getName(  ) + ")";
    }

    public String[] getRoles(  )
    {
        return _isAdminUser ? new String[0] : _user.getRoles(  );
    }
}
