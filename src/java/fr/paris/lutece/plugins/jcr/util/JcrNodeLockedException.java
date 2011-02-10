/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.jcr.util;

import javax.jcr.Node;


/**
 *
 * @author gduge
 */
public class JcrNodeLockedException extends JcrException
{
    public JcrNodeLockedException( Node currentNode )
    {
        super(  );
        setNode( currentNode );
    }
}
