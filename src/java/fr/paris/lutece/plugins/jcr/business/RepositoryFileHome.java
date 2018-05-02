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
package fr.paris.lutece.plugins.jcr.business;

import fr.paris.lutece.plugins.jcr.authentication.AbstractJcrPrivilegedAction;
import fr.paris.lutece.plugins.jcr.authentication.JcrRestrictedOperation;
import fr.paris.lutece.plugins.jcr.authentication.JsrUser;
import fr.paris.lutece.plugins.jcr.business.admin.AdminView;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLock;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLockHome;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;

import java.io.File;
import java.io.IOException;

import java.security.PrivilegedAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Home class for JCR repository operations
 *
 */
public class RepositoryFileHome
{
    public static final String CONTEXT_NAME = "jsr170";
    public static final String SPRING_JSR170_PREFIX = "jsr170.";
    public static final String SPRING_JSR170_JCRLIST = "jsr170.JCRlist";
    public static final String SPRING_REPOSITORY_FILEdao = ".repositoryFileDAO";
    public static final String SPRINGworkspaceDao = ".workspaceDAO";
    private static RepositoryFileHome _singleton;
    private Map<String, IRepositoryFileDAO> _cachedIRepositoryFileDAO;
    private Map<String, IWorkspaceDAO> _cachedIWorkspaceDAO;

    /**
     * Default constructor
     */
    protected RepositoryFileHome(  )
    {
        _cachedIRepositoryFileDAO = new HashMap<String, IRepositoryFileDAO>(  );
        _cachedIWorkspaceDAO = new HashMap<String, IWorkspaceDAO>(  );
    }

    /**
     * Returns an IRepositoryFileDAO of type strJcrType from the Spring context
     * @param strJcrType the type of JCR. Available types are defined in the Spring context
     * @return an instance of IRepositoryFileDAO
     */
    protected IRepositoryFileDAO getIRepositoryFileDAO( String strJcrType )
    {
        return getDAOFromSpringContext( strJcrType, SPRING_REPOSITORY_FILEdao, _cachedIRepositoryFileDAO );
    }

    /**
     * Returns an IWorkspaceDAO of type strJcrType from the Spring context
     * @param strJcrType the type of JCR. Available types are defined in the Spring context
     * @return an instance of IWorkspaceDAO
     */
    private IWorkspaceDAO getIWorkspaceDAO( String strJcrType )
    {
        return getDAOFromSpringContext( strJcrType, SPRINGworkspaceDao, _cachedIWorkspaceDAO );
    }

    /**
     * Returns an instance of &lt;DAO&gt; which is named strDaoName working on a JCR of type strJcrType.
     * This instance is cached.
     * @param <DAO> should be IWorkspaceDAO or IRepositoryFileDAO
     * @param strJcrType the type of JCR.
     * @param strDaoName the dao name in the Spring context
     * @param cachedObjectsMap the map of cached instances
     * @return an instance of &lt;DAO&gt;
     */
    private <DAO extends IJsr170DAO> DAO getDAOFromSpringContext( String strJcrType, String strDaoName,
        Map<String, DAO> cachedObjectsMap )
    {
        DAO result = cachedObjectsMap.get( strJcrType );

        if ( ( result != null ) && !result.isAlive(  ) )
        {
            AppLogService.debug( "DAO not available, reloading... " + result );
            result.free(  );
            result = null;
        }

        if ( result == null )
        {
            result = (DAO) SpringContextService.getPluginBean( CONTEXT_NAME, SPRING_JSR170_PREFIX + strJcrType + strDaoName );
            cachedObjectsMap.put( strJcrType, result );
        }

        return result;
    }

    /**
     * Returns the instance of the singleton
     * @return The instance of the singleton
     */
    public static RepositoryFileHome getInstance(  )
    {
        RepositoryFileHome singleton = _singleton;

        if ( singleton == null )
        {
            singleton = new RepositoryFileHome(  );
            _singleton = singleton;
        }

        return singleton;
    }

    /**
     * Get the files in workspace strWorkspace
     * @param workspace the workspace name
     * @return a list of IRepositoryFile
     */
    public List<IRepositoryFile> getRepositoryFileList( AdminWorkspace workspace, JsrUser user )
    {
        return getRepositoryFileList( workspace, "", user );
    }

    /**
     * Get the file in workspace strWorkspace in directory path
     * @param workspace the workspace name
     * @param strFileId an absolute path to the directory
     * @param user the current user
     * @return the IRepositoryFile
     */
    public IRepositoryFile getRepositoryFileById( final AdminWorkspace workspace, final String strFileId,
        final JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<IRepositoryFile>( workspace )
            {
                public IRepositoryFile delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( workspace.getJcrType(  ) );

                    return dao.findById( workspace.getName(  ), strFileId );
                }
            } );
    }

    /**
     * Get the file in workspace strWorkspace in directory path
     * @param workspace the workspace name
     * @param strFileId an absolute path to the directory
     * @param strVersion the version name to retrieve
     * @param user the current user
     * @return the IRepositoryFile
     */
    public IRepositoryFile getRepositoryFileVersionById( final AdminWorkspace workspace, final String strFileId,
        final String strVersion, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<IRepositoryFile>( workspace )
            {
                public IRepositoryFile delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( workspace.getJcrType(  ) );

                    return dao.findById( workspace.getName(  ), strFileId, strVersion );
                }
            } );
    }

    /**
     * Get the file in workspace strWorkspace in directory path
     * @param workspace the workspace
     * @param path an absolute path to the directory
     * @param user the current user
     * @return the IRepositoryFile
     */
    public IRepositoryFile getRepositoryFile( final AdminWorkspace workspace, final String path, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<IRepositoryFile>( workspace )
            {
                public IRepositoryFile delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( workspace.getJcrType(  ) );

                    return dao.findByPath( workspace.getName(  ), path );
                }
            } );
    }

    /**
     * Get the files in workspace strWorkspace in directory parent
     * @param workspace the workspace
     * @param parent the parent directory absolute path
     * @param user the current user
     * @return a list of IRepositoryFile
     */
    public List<IRepositoryFile> getRepositoryFileList( final AdminWorkspace workspace, final String parent,
        JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<List<IRepositoryFile>>( workspace )
            {
                public List<IRepositoryFile> delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( workspace.getJcrType(  ) );

                    return dao.listFiles( workspace.getName(  ), parent );
                }
            } );
    }

    /**
     * Create a file in workspace strWorkspace in directory designed by
     * path with content file
     * @param workspace the workspace
     * @param path the parent directory absolute path
     * @param file the file content
     * @param user the current user
     */
    public void addFile( final AdminWorkspace workspace, final String path, final File file, final String strMimeType,
        JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );
        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( workspace )
            {
                public Object delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( workspace.getJcrType(  ) );
                    dao.create( workspace.getName(  ), path, file, strMimeType );

                    return null;
                }
            } );
    }

    /**
     * Create a directory in workspace strWorkspace at location path
     * @param workspace the workspace
     * @param path the directory absolute path
     * @param user the current user
     */
    public void createFolder( final AdminWorkspace workspace, final String path, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );
        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( workspace )
            {
                public Object delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( workspace.getJcrType(  ) );
                    dao.create( workspace.getName(  ), path );

                    return null;
                }
            } );
    }

    /**
     * Remove element in workspace strWorkspace at path strPath
     * @param workspace the workspace
     * @param strPath the absolute path
     * @param user the current user
     */
    public void removeRepositoryFile( final AdminWorkspace workspace, final String strPath, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );
        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( workspace )
            {
                public Object delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( workspace.getJcrType(  ) );
                    dao.delete( workspace.getName(  ), strPath );

                    return null;
                }
            } );
    }

    /**
     * Get all available workspaces
     * @param strJcrType the type of the jcr repository
     * @return a ReferenceList with all workspace names
     */
    public ReferenceList getAvailableWorkspaces( String strJcrType )
    {
        IWorkspaceDAO workspaceDao = getIWorkspaceDAO( strJcrType );
        ReferenceList result = new ReferenceList(  );

        for ( String workspaceName : workspaceDao.getAvailableWorkspaces(  ) )
        {
            result.addItem( workspaceName, workspaceName );
        }

        return result;
    }

    /**
     * Create a new workspace
     * @param strJcrType the type of the jcr repository
     * @param strWorkspaceName the workspace name
     */
    public void createWorkspace( String strJcrType, String strWorkspaceName )
    {
        IWorkspaceDAO workspaceDao = getIWorkspaceDAO( strJcrType );
        workspaceDao.create( strWorkspaceName );
    }

    /**
     * Get workspace informations
     * @param workspace the workspace
     * @param user the current user
     * @return the workspace
     */
    public IWorkspace getWorkspace( final AdminWorkspace workspace, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, workspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<IWorkspace>( workspace )
            {
                public IWorkspace delegate(  )
                {
                    IWorkspaceDAO workspaceDao = getIWorkspaceDAO( workspace.getJcrType(  ) );

                    return workspaceDao.findByName( workspace.getName(  ) );
                }
            } );
    }

    /**
     * Modify workspace acces rights
     * This is part of admin functionalities
     *
     * @param adminWorkspace the admin workspace
     * @param workspace the workspace instance
     * @param user the current user
     */
    public void modify( final AdminWorkspace adminWorkspace, final IWorkspace workspace, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );

        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( adminWorkspace )
            {
                public Object delegate(  )
                {
                    IWorkspaceDAO workspaceDao = getIWorkspaceDAO( adminWorkspace.getJcrType(  ) );
                    workspaceDao.store( workspace );

                    return null;
                }
            } );
    }

    /**
     * Tests if the workspace strWorkspace can be accessed with roles in userRoles
     * and access of type strAccessType
     * @param adminWorkspace the workspace
     * @param strAccessType the access type, it must be in IWorkspace.AVAILABLE_ACCESS
     * @param userRoles an array of roles
     * @param user the current user
     * @return true if it is accessible, false otherwise
     */
    public boolean canAccess( final AdminWorkspace adminWorkspace, final String strAccessType,
        final String[] userRoles, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Boolean>( adminWorkspace )
            {
                public Boolean delegate(  )
                {
                    IWorkspaceDAO workspaceDao = getIWorkspaceDAO( adminWorkspace.getJcrType(  ) );
                    IWorkspace workspace = workspaceDao.findByName( adminWorkspace.getName(  ) );

                    for ( String userRole : userRoles )
                    {
                        if ( Arrays.binarySearch( workspace.getRoles( strAccessType ), userRole ) >= 0 )
                        {
                            return Boolean.TRUE;
                        }
                    }

                    return Boolean.FALSE;
                }
            } );
    }

    /**
     * Get all available access, given some roles
     * @param adminWorkspace the workspace
     * @param userRoles an array of roles
     * @param user the current user
     * @return a map containing access types associated with access value
     */
    public Map<String, Boolean> getAvailableAccess( final AdminWorkspace adminWorkspace, final String[] userRoles,
        JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Map<String, Boolean>>( adminWorkspace )
            {
                public Map<String, Boolean> delegate(  )
                {
                    List<String> checkedRoles = new ArrayList<String>( Arrays.asList( userRoles ) );
                    checkedRoles.add( "none" );

                    IWorkspaceDAO workspaceDao = getIWorkspaceDAO( adminWorkspace.getJcrType(  ) );
                    HashMap<String, Boolean> availableAccess = new HashMap<String, Boolean>(  );
                    IWorkspace workspace = workspaceDao.findByName( adminWorkspace.getName(  ) );

                    for ( String strAccessType : IWorkspace.AVAILABLE_ACCESS )
                    {
                        for ( String userRole : checkedRoles )
                        {
                            if ( Arrays.binarySearch( workspace.getRoles( strAccessType ), userRole ) >= 0 )
                            {
                                availableAccess.put( strAccessType, Boolean.TRUE );

                                break;
                            }

                            availableAccess.put( strAccessType, Boolean.FALSE );
                        }
                    }

                    return availableAccess;
                }
            } );
    }

    /**
     * @return jcr defined in Spring context
     */
    public ReferenceList getAvailableJcr(  )
    {
        ReferenceList mapJcrNames = new ReferenceList(  );

        for ( String jcrName : getAvailableJcrList(  ) )
        {
            mapJcrNames.addItem( jcrName, jcrName );
        }

        return mapJcrNames;
    }

    /**
     * @return a list of available jcr types from the Spring context and technically instantiable
     */
    public List<String> getAvailableJcrList(  )
    {
        List<String> listJcrNames = new ArrayList<String>(  );

        for ( String jcrName : (List<String>) SpringContextService.getPluginBean( CONTEXT_NAME, SPRING_JSR170_JCRLIST ) )
        {
            try
            {
                getIWorkspaceDAO( jcrName );
                listJcrNames.add( jcrName );
            }
            catch ( Exception e )
            {
                AppLogService.info( e );
            }
        }

        return listJcrNames;
    }

    /**
     * This is part of admin functionalities
     *
     * @param strJcrType the type of jcr
     * @return true if workspace can be created in this jcr
     */
    public boolean canCreateWorkspace( String strJcrType )
    {
        IWorkspaceDAO workspaceDao = getIWorkspaceDAO( strJcrType );

        return workspaceDao.canCreate(  );
    }

    /**
     * Removes a workspace
     *
     * This is part of admin functionalities
     * @param adminWorkspace the name of the workspace to remove
     * @param user the current user
     */
    public void removeWorkspace( final AdminWorkspace adminWorkspace, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );
        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( adminWorkspace )
            {
                public Object delegate(  )
                {
                    IWorkspaceDAO workspaceDao = getIWorkspaceDAO( adminWorkspace.getJcrType(  ) );
                    workspaceDao.delete( adminWorkspace.getName(  ) );

                    return null;
                }
            } );
    }

    /**
     * @param adminWorkspace the workspace to use
     * @param rootFile the file to get the pretty path from
     * @param user the current user
     * @return a path where each element is the name of the file
     */
    public String getPrettyAbsolutePath( final AdminWorkspace adminWorkspace, final IRepositoryFile rootFile,
        final JsrUser jsrUser )
    {
        if ( adminWorkspace.getJcrType(  ).equals( "jackrabbit" ) )
        {
            // the absolute path is already correct in jackrabbit jcr
            return rootFile.getAbsolutePath(  );
        }

        String strPrettyPath = "";
        IRepositoryFile currentFile = rootFile;

        do
        {
            strPrettyPath = "/" + currentFile.getName(  ) + strPrettyPath;
            currentFile = getRepositoryFileById( adminWorkspace, currentFile.getParentId(  ), jsrUser );
        }
        while ( ( currentFile.getParentId(  ) != null ) && !currentFile.getAbsolutePath(  ).equals( "/" ) );

        return strPrettyPath;
    }

    /**
     * Modifies a file in a workspace
     *
     * The file must not be locked by another user
     * @param adminWorkspace the current workspace
     * @param file the new file content
     * @param user the current user
     */
    public void update( final AdminWorkspace adminWorkspace, final IRepositoryFile file, final JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );
        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( adminWorkspace )
            {
                public Object delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );
                    dao.store( adminWorkspace.getName(  ), file );

                    return null;
                }
            } );
    }

    /**
     * Adds a file to the version manager
     * @param adminWorkspace the current workspace
     * @param strFileId the id of the file to modify
     * @param isVersionnable true : sets the file as versionable, if false : remove the file from the file manager
     * @param user the current user
     */
    public void setVersionnable( final AdminWorkspace adminWorkspace, final String strFileId,
        final boolean isVersionnable, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );
        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( adminWorkspace )
            {
                public Object delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );
                    dao.setVersionnable( adminWorkspace.getName(  ), strFileId, isVersionnable );

                    return null;
                }
            } );
    }

    /**
     * Gets the file history
     * @param adminWorkspace the current workspace
     * @param strFileId the id of the file
     * @param user the current user
     * @return a list of all the version of the file as IRepositoryFile instances
     */
    public List<IRepositoryFile> getFileHistory( final AdminWorkspace adminWorkspace, final String strFileId,
        JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );

        return operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<List<IRepositoryFile>>( adminWorkspace )
            {
                public List<IRepositoryFile> delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );

                    return dao.getHistory( adminWorkspace.getName(  ), strFileId );
                }
            } );
    }

    /**
     * Recursively delegates an specific action on a JCR content.
     *
     * If action returns null, no result is appended to the result list.
     *
     * @param <T> the result element type
     * @param <L> the result list type
     * @param adminWorkspace the workspace to work on
     * @param view the view to browse
     * @param strStartPath the start point of the browsing
     * @param action the action to perform on each node
     * @param user the current user
     * @return
     */
    public void doRecursive( final AdminWorkspace adminWorkspace, final AdminView view, final String strStartPath,
        final INodeAction<Document, Collection<Document>> action, JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );

        operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Object>( adminWorkspace )
            {
                public Object delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );
                    IRepositoryFile file = ( view.getPath(  ) == null )
                        ? dao.findByPath( adminWorkspace.getName(  ), "/" )
                        : dao.findById( adminWorkspace.getName(  ), view.getPath(  ) );
                    doRecursive( adminWorkspace, file, action );

                    return null;
                }
            } );
    }

    /**
     * @param <T> the type of result elements
     * @param <L> the collection type
     * @param adminWorkspace the workspace
     * @param parentFile the file to star from
     * @param action the action to perform on each node
     * @return a new list of type <L>
     */
    protected void doRecursive( AdminWorkspace adminWorkspace, IRepositoryFile parentFile,
        INodeAction<Document, Collection<Document>> action )
    {
        Document result = action.doAction( parentFile );

        if ( result != null )
        {
            try
            {
                IndexationService.write( result );
            }
            catch ( CorruptIndexException e )
            {
                e.printStackTrace(  );
            }
            catch ( IOException e )
            {
                e.printStackTrace(  );
            }
        }

        IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );

        for ( IRepositoryFile file : dao.listFiles( adminWorkspace.getName(  ), parentFile.getAbsolutePath(  ) ) )
        {
            result = action.doAction( file );

            if ( result != null )
            {
                try
                {
                    IndexationService.write( result );
                }
                catch ( CorruptIndexException e )
                {
                    e.printStackTrace(  );
                }
                catch ( IOException e )
                {
                    e.printStackTrace(  );
                }
            }

            if ( file.isDirectory(  ) )
            {
                doRecursive( adminWorkspace, file, action );
            }
        }
    }

    /**
     * Puts a lock on a node
     * @param adminWorkspace the workspace
     * @param file the node to lock
     * @param user the current user
     */
    public void setLock( final AdminWorkspace adminWorkspace, final IRepositoryFile file, final JsrUser user )
    {
        final String strIdUser = user.getName(  );
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );
        String strToken = operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<String>( adminWorkspace )
                {
                    public String delegate(  )
                    {
                        IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );

                        return dao.setLock( adminWorkspace.getName(  ), file, strIdUser );
                    }
                } );

        JcrLock lock = new JcrLock(  );
        lock.setIdLock( strToken );
        lock.setIdUser( strIdUser );
        lock.setIdWorkspace( adminWorkspace.getId(  ) );
        lock.setIdFile( file.getResourceId(  ) );
        AppLogService.debug( "Storing lock in database... " + lock );
        JcrLockHome.getInstance(  ).createLock( lock );
    }

    /**
     * Unlock a locked node
     * @param adminWorkspace the workspace
     * @param file the file to unlock
     * @param user the current user
     */
    public void unLock( final AdminWorkspace adminWorkspace, final IRepositoryFile file, final JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );
        unlock( operation, adminWorkspace, file );
    }

    public void unLock( final AdminWorkspace adminWorkspace, final IRepositoryFile file, final JsrUser user,
        final Collection<JcrLock> jcrLocks )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace, jcrLocks );
        unlock( operation, adminWorkspace, file );
    }

    private void unlock( JcrRestrictedOperation operation, final AdminWorkspace adminWorkspace,
        final IRepositoryFile file )
    {
        String strToken = operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<String>( adminWorkspace )
                {
                    public String delegate(  )
                    {
                        IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );

                        return dao.removeLock( adminWorkspace.getName(  ), file );
                    }
                } );

        JcrLock lock = new JcrLock(  );
        lock.setIdLock( strToken );
        lock.setIdWorkspace( adminWorkspace.getId(  ) );
        AppLogService.debug( "removing lock in database... " + lock );
        JcrLockHome.getInstance(  ).removeLock( lock );
    }

    /**
     * Gives a list of files from the root node to the given node
     * @param adminWorkspace the workspace
     * @param strFileId the id of the node
     * @param user the current user
     * @return a list of file (ordered from rootnode to given node)
     */
    public List<IRepositoryFile> getPathToFile( final AdminWorkspace adminWorkspace, final String strFileId,
        final JsrUser user )
    {
        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );

        return (List<IRepositoryFile>) operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<List<IRepositoryFile>>( 
                adminWorkspace )
            {
                public List<IRepositoryFile> delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );

                    return dao.getPathToFile( adminWorkspace.getName(  ), strFileId );
                }
            } );
    }

    /**
     * Returns a list of all files locked
     *
     * @param adminWorkspace the workspace
     * @param view the vie
     * @param user the user
     * @return a map of lock associated with files
     */
    public Map<String, JcrLock> getLocks( final AdminWorkspace adminWorkspace, final AdminView view, final JsrUser user )
    {
        JcrLock criterias = new JcrLock(  );
        criterias.setIdWorkspace( view.getWorkspaceId(  ) );

        final Collection<JcrLock> lockCollection = JcrLockHome.getInstance(  ).getLocks( criterias );

        JcrRestrictedOperation operation = new JcrRestrictedOperation( user, adminWorkspace );

        return (Map<String, JcrLock>) operation.doRestrictedOperation( new AbstractJcrPrivilegedAction<Map<String, JcrLock>>( 
                adminWorkspace )
            {
                public Map<String, JcrLock> delegate(  )
                {
                    IRepositoryFileDAO dao = getIRepositoryFileDAO( adminWorkspace.getJcrType(  ) );
                    HashMap<String, JcrLock> resultMap = new HashMap<String, JcrLock>(  );

                    for ( JcrLock lock : lockCollection )
                    {
                        IRepositoryFile file = dao.findById( adminWorkspace.getName(  ), lock.getIdFile(  ) );

                        if ( lock != null )
                        {
                            resultMap.put( file.getAbsolutePath(  ), lock );
                        }
                    }

                    return resultMap;
                }
            } );
    }
}
