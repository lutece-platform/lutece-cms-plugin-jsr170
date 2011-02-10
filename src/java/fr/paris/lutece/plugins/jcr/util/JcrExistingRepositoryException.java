/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.jcr.util;


/**
 *
 * @author gduge
 */
public class JcrExistingRepositoryException extends JcrException
{
    public JcrExistingRepositoryException( String strExistingWorkspace )
    {
        super( strExistingWorkspace );
    }
}
