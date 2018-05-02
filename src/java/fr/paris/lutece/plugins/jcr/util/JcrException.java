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
package fr.paris.lutece.plugins.jcr.util;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * Customized exceptions for managing JSR170 exceptions
 *
 * Error codes are :
 * <ul>
 * <li>PATH_NOT_FOUND_EXCEPTION</li>
 * <li>REPOSITORY_EXCEPTION</li>
 * <li>IO_EXCEPTION</li>
 * <li>NODE_LOCKED</li>
 * </ul>
 */
public abstract class JcrException extends RuntimeException
{
    private static final long serialVersionUID = 3840829459755842317L;
    private Node _node;
    private Object _object;

    protected JcrException(  )
    {
    }

    /**
     * @param e the RepositoryException
     * @param nCode the code
     */
    public JcrException( RepositoryException e )
    {
        super( e );
    }

    /**
     * @param e the RepositoryException
     * @param nCode the code
     */
    public JcrException( Object o )
    {
        super(  );
        setObjectCause( o );
    }

    public void setNode( Node node )
    {
        _node = node;
    }

    public void setObjectCause( Object o )
    {
        _object = o;
    }

    public Object getObjectCause(  )
    {
        return _object;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage(  )
    {
        String message = super.getLocalizedMessage(  );

        if ( _node != null )
        {
            try
            {
                message += ( " '" + _node.getPath(  ) + "'" );
            }
            catch ( RepositoryException ex )
            {
                // nothing to do
            }
        }

        if ( _object != null )
        {
            message += ( " '" + _object.toString(  ) + "'" );
        }

        return message;
    }
}
