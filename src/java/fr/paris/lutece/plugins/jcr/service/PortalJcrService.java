/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.jcr.service;

import fr.paris.lutece.plugins.jcr.authentication.JsrUser;
import fr.paris.lutece.plugins.jcr.business.IRepositoryFile;
import fr.paris.lutece.plugins.jcr.business.RepositoryFileHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLock;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLockHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;


/**
 *
 * @author gduge
 */
public class PortalJcrService
{
    private static PortalJcrService _singleton = new PortalJcrService(  );
    private Plugin _plugin;

    /**
     * Initialize the Jcr service
     *
     */
    public void init( Plugin plugin )
    {
        _plugin = plugin;
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static PortalJcrService getInstance(  )
    {
        return _singleton;
    }

    public IRepositoryFile deleteFile( String strFileId, AdminWorkspace workspace, JsrUser jsrUser )
    {
        RepositoryFileHome instance = RepositoryFileHome.getInstance(  );
        IRepositoryFile currentFile = instance.getRepositoryFileById( workspace, strFileId, jsrUser );
        String strPathFile = currentFile.getAbsolutePath(  );
        IRepositoryFile parentFile = instance.getRepositoryFile( workspace,
                strPathFile.substring( 0, strPathFile.lastIndexOf( "/" ) ), jsrUser );

        instance.removeRepositoryFile( workspace, currentFile.getAbsolutePath(  ), jsrUser );

        JcrLockHome lockInstance = JcrLockHome.getInstance(  );
        JcrLock lock = new JcrLock(  );
        lock.setIdFile( strFileId );

        for ( JcrLock currentLock : lockInstance.getLocks( lock ) )
        {
            AppLogService.debug( "Remove lock " + currentLock );

            //            lockInstance.removeLock( currentLock );
        }

        return parentFile;
    }
}
