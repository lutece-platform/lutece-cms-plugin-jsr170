/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLock;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLockHome;

import java.security.Principal;
import java.security.PrivilegedAction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import javax.security.auth.Subject;


/**
 * Wrapper class to use restricted operations
 */
public class JcrRestrictedOperation
{
    private transient Subject _subject;

    /**
     * @param user
     *            the login to connect
     * @param strPassword
     *            the password
     * @param workspace
     */
    public JcrRestrictedOperation( JsrUser user, AdminWorkspace workspace )
    {
        Credentials credentials = new SimpleCredentials( workspace.getUser(  ),
                workspace.getPassword(  ).toCharArray(  ) );

        // retrieve all owned locks
        JcrLock criterias = new JcrLock(  );
        criterias.setIdUser( user.getName(  ) );
        criterias.setIdWorkspace( workspace.getId(  ) );

        Collection<JcrLock> jcrLocks = JcrLockHome.getInstance(  ).getLocks( criterias );

        _subject = initSubject( user, credentials, jcrLocks );
    }

    /**
     * @param user
     *            the login to connect
     * @param strPassword
     *            the password
     * @param workspace
     */
    public JcrRestrictedOperation( JsrUser user, AdminWorkspace workspace, Collection<JcrLock> jcrLocks )
    {
        Credentials credentials = new SimpleCredentials( workspace.getUser(  ),
                workspace.getPassword(  ).toCharArray(  ) );

        // retrieve all owned locks
        JcrLock criterias = new JcrLock(  );
        criterias.setIdUser( user.getName(  ) );
        criterias.setIdWorkspace( workspace.getId(  ) );

        _subject = initSubject( user, credentials, jcrLocks );
    }

    /**
     * Runs a PrivilegedAction by instantiation of a new Subject with
     * _strLogin/_strPassword
     *
     * @param <T> the return type of the action
     * @param action
     *            the PrivilegedAction to run
     * @return the result of the action
     * @see PrivilegedAction
     */
    public <T> T doRestrictedOperation( PrivilegedAction<T> action )
    {
        return (T) Subject.doAs( _subject, action );
    }

    /**
     * Initializes a subject depending on a jsrUser, the credentials, and a collection of jcrlocks
     * @param user
     * @param credentials
     * @param locks
     * @return a subject
     */
    private Subject initSubject( JsrUser user, Credentials credentials, Collection<JcrLock> locks )
    {
        Set<Principal> setUser = new HashSet<Principal>(  );
        setUser.add( user );

        Set<Credentials> setPubCred = new HashSet<Credentials>(  );
        setPubCred.add( credentials );

        if ( locks != null )
        {
            setPubCred.addAll( locks );
        }

        return new Subject( true, setUser, setPubCred, setPubCred );
    }
}
