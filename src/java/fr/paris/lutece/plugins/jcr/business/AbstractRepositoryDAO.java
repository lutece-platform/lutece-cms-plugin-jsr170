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
package fr.paris.lutece.plugins.jcr.business;

import fr.paris.lutece.plugins.jcr.business.lock.JcrLock;
import fr.paris.lutece.plugins.jcr.util.JcrRepositoryException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrSessionFactory;
import org.springmodules.jcr.JcrTemplate;
import org.springmodules.jcr.SessionFactoryUtils;

import java.io.IOException;

import java.security.AccessController;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;

import javax.security.auth.Subject;


/**
 * Abstract class for common jcr operations like :
 * <ul>
 * <li>repository initialization</li>
 * <li>jcr operation execution</li>
 * <li>externalized security (planned feature)</li>
 * </ul>
 * @author lutecer
 *
 */
public abstract class AbstractRepositoryDAO implements IJsr170DAO
{
    protected JcrTemplate _jcrTemplate;
    protected String _strDefaultWorkspaceName;
    protected IRepositoryInitializer _repositoryInitializer;

    /**
     * Constructor that MUST be called by derivated classes.
     *
     * Get Spring beans for JcrTemplate, JcrSessionFactory and IRepositoryInitializer,
     * then run the initializer.
     * @param jcrTemplate the jcrTemplate used to execute jcr operations
     * @param repositoryInitializer the repositoryInitializer used to init some specific repository operations
     * @param strDefaultWorkspaceName the name of the default workspace to use (can be null in order to
     *  use the default workspace repository)
     */
    protected void init( JcrTemplate jcrTemplate, IRepositoryInitializer repositoryInitializer,
        String strDefaultWorkspaceName )
    {
        _jcrTemplate = jcrTemplate;
        _repositoryInitializer = repositoryInitializer;

        _strDefaultWorkspaceName = strDefaultWorkspaceName;

        JcrSessionFactory jcrSessionFactory = (JcrSessionFactory) _jcrTemplate.getSessionFactory(  );
        jcrSessionFactory.setWorkspaceName( _strDefaultWorkspaceName );
        _jcrTemplate.setSessionFactory( jcrSessionFactory );
        _jcrTemplate.execute( new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws RepositoryException
                {
                    Workspace ws = session.getWorkspace(  );
                    _repositoryInitializer.init( ws );

                    return null;
                }
            } );
    }

    /**
     * Run a JcrCallback on the current workspace
     * @param callback a JcrCallback
     * @return the result of the JcrCallback
     * @see <a href="https://springmodules.dev.java.net/docs/reference/0.7/html/jcr.html#d0e4980">Spring module manual (JcrCallback)</a>
     */
    protected Object execute( final JcrCallback callback )
    {
        return ( execute( null, callback ) );
    }

    /**
     * Run a JcrCallback on the workspace specified by strWorkspace
     * @param strWorkspace the name of the workspace
     * @param callback a JcrCallback
     * @return the result of the JcrCallback
     * @see <a href="https://springmodules.dev.java.net/docs/reference/0.7/html/jcr.html#d0e4980">Spring module manual (JcrCallback)</a>
     */
    protected Object execute( String strWorkspace, final JcrCallback callback )
    {
        /*
         *  JcrSessionFactory initialization depending on :
         * - Workspace name,
         * - Subject in AccessController context,
         */
        JcrSessionFactory jcrSessionFactory = (JcrSessionFactory) _jcrTemplate.getSessionFactory(  );

        if ( strWorkspace != null )
        {
            jcrSessionFactory.setWorkspaceName( strWorkspace );
        }
        else
        {
            jcrSessionFactory.setWorkspaceName( _strDefaultWorkspaceName );
        }

        Subject subject = Subject.getSubject( AccessController.getContext(  ) );

        if ( subject != null )
        {
            for ( SimpleCredentials credentials : subject.getPublicCredentials( SimpleCredentials.class ) )
            {
                jcrSessionFactory.setCredentials( credentials );

                break;
            }
        }

        AppLogService.debug( "Doing JCR operation with credentials : " +
            ( (SimpleCredentials) jcrSessionFactory.getCredentials(  ) ).getUserID(  ) );

        /*
         * Session initialization, we put the JcrLocks in, then call the operation
         *
         */
        Object result;
        Session session = null;

        try
        {
            session = SessionFactoryUtils.getSession( jcrSessionFactory, true );

            if ( subject != null )
            {
                if ( AppLogService.isDebugEnabled(  ) )
                {
                    AppLogService.debug( "Number of JcrLocks created by the user : " +
                        subject.getPublicCredentials( JcrLock.class ).size(  ) );
                }

                for ( JcrLock lock : subject.getPublicCredentials( JcrLock.class ) )
                {
                    session.addLockToken( lock.getIdLock(  ) );
                }

                if ( subject.getPublicCredentials( JcrLock.class ).size(  ) == 0 )
                {
                    AppLogService.debug( "Error in lock tocken (#session != #subject) : " +
                        session.getLockTokens(  ).length + "/" + subject.getPublicCredentials(  ).size(  ) );
                }
            }

            result = callback.doInJcr( session );
        }
        catch ( RepositoryException e )
        {
            throw new JcrRepositoryException( e );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            throw new AppException( e.getLocalizedMessage(  ) );
        }
        finally
        {
            if ( session != null )
            {
                releaseSession( session );
            }
        }

        return result;
    }

    public boolean isAlive(  )
    {
        try
        {
            _jcrTemplate.itemExists( "/" );
        }
        catch ( Exception e )
        {
            return false;
        }

        return true;
    }

    public void free(  )
    {
        AppLogService.debug( "Call to free() " + this );
    }

    public void releaseSession( Session session )
    {
        AppLogService.debug( "Releasing session... " + _jcrTemplate );

        JcrSessionFactory jcrSessionFactory = (JcrSessionFactory) _jcrTemplate.getSessionFactory(  );

        if ( session != null )
        {
            SessionFactoryUtils.releaseSession( session, jcrSessionFactory );
        }
    }
}
