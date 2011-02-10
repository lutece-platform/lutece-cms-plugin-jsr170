/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.jcr.authentication;

import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;

import java.security.PrivilegedAction;


/**
 *
 * @author gduge
 */
public abstract class AbstractJcrPrivilegedAction<T> implements PrivilegedAction<T>
{
    private AdminWorkspace _workspace;

    public AbstractJcrPrivilegedAction( AdminWorkspace workspace )
    {
        _workspace = workspace;
    }

    final public T run(  )
    {
        // do something before
        return delegate(  );

        // do something after (in a finally block)
    }

    public abstract T delegate(  );
}
