/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.jcr.util;

import javax.jcr.PathNotFoundException;


/**
 *
 * @author gduge
 */
public class JcrPathNotFoundException extends JcrException
{
    public JcrPathNotFoundException( PathNotFoundException e )
    {
        super( e );
    }
}
