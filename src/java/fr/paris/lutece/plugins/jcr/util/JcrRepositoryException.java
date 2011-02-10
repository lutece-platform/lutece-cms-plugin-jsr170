/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.jcr.util;

import javax.jcr.RepositoryException;


/**
 *
 * @author gduge
 */
public class JcrRepositoryException extends JcrException
{
    public JcrRepositoryException( RepositoryException e )
    {
        super( e );
    }
}
