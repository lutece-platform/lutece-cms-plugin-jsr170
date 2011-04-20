/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.springmodules.jcr.JcrCallback;

import fr.paris.lutece.plugins.jcr.business.admin.AdminJcrHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.service.JcrPlugin;
import fr.paris.lutece.plugins.jcr.service.search.JcrIndexer;
import fr.paris.lutece.plugins.jcr.util.JcrIndexerUtils;
import fr.paris.lutece.plugins.jcr.util.JcrNodeLockedException;
import fr.paris.lutece.plugins.jcr.util.JcrPathNotFoundException;
import fr.paris.lutece.plugins.jcr.util.JcrRepositoryException;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.util.AppLogService;


/**
 * Provides basic methods for node manipulation
 * @author lutecer
 *
 */
public abstract class AbstractRepositoryContentDAO extends AbstractRepositoryDAO implements IRepositoryFileDAO
{
    protected static final String NODE_TYPE_FOLDER = "NODE_TYPE_FOLDER";
    protected static final String NODE_TYPE_FILE = "NODE_TYPE_FILE";
    protected static final String NODE_TYPE_FILE_RESOURCE = "NODE_TYPE_FILE_RESOURCE";
    protected static final String ATTRIBUTE_NODE_SIZE = "ATTRIBUTE_NODE_SIZE";
    protected static final String NODE_TYPE_JCR_CONTENT = "NODE_TYPE_JCR_CONTENT";
    protected static final String REGEXP_ABSOLUTE_PATH = "REGEXP_ABSOLUTE_PATH";
    protected static final String MIXIN_REFERENCEABLE = "MIXIN_REFERENCEABLE";
    protected static final String MIXIN_VERSIONNABLE = "MIXIN_VERSIONNABLE";
    protected static final String ROOT_NODE_PATH = "ROOT_NODE_PATH";
    protected static final String DEFAULT_MIME_TYPE = "DEFAULT_MIME_TYPE";
    protected static final String PROPERTY_JCR_MIMETYPE = "PROPERTY_JCR_MIMETYPE";
    protected static final String PROPERTY_JCR_DATA = "PROPERTY_JCR_DATA";
    protected static final String PROPERTY_JCR_LASTMODIFIED = "PROPERTY_JCR_LASTMODIFIED";
    protected static final String PROPERTY_JCR_NAME = "PROPERTY_JCR_NAME";

    /**
     * @param strWorkspace the workspace name
     * @param strPath the absolute path
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#create(java.lang.String, java.lang.String)
     */
    public void create( String strWorkspace, final String strPath )
    {
        final AdminWorkspace workspace = AdminJcrHome.getInstance(  )
                                                     .getWorkspacesListByNameKey( PluginService.getPlugin( 
                    JcrPlugin.PLUGIN_NAME ) ).get( strWorkspace );

        execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    String strRelPath = strPath.replaceFirst( getProperty( REGEXP_ABSOLUTE_PATH ), "" );
                    Node addedNode = getRootNode( session ).addNode( strRelPath, getProperty( NODE_TYPE_FOLDER ) );

                    if ( getProperty( MIXIN_REFERENCEABLE ) != null )
                    {
                        addedNode.addMixin( getProperty( MIXIN_REFERENCEABLE ) );
                    }

                    setName( addedNode, strRelPath );
                    session.save(  );

                    /*IndexationService.addIndexerAction(workspace.getId()+"-"+addedNode.getUUID(), JcrIndexer.class.getSimpleName(), IndexerAction.TASK_CREATE);
                            */
                    return null;
                }
            } );
    }

    /**
     * @param strWorkspace the workspace name
     * @param strFileName the absolute filename to create
     * @param file the file content
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#create(java.lang.String, java.lang.String, java.io.File)
     */
    public void create( String strWorkspace, final String strFileName, final File file, final String strMimeType )
    {
        final AdminWorkspace workspace = AdminJcrHome.getInstance(  )
                                                     .getWorkspacesListByNameKey( PluginService.getPlugin( 
                    JcrPlugin.PLUGIN_NAME ) ).get( strWorkspace );

        execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    String strFilePath = strFileName.replaceAll( getProperty( REGEXP_ABSOLUTE_PATH ), "" );
                    Node addedNode = getRootNode( session ).addNode( strFilePath, getProperty( NODE_TYPE_FILE ) );

                    if ( getProperty( ATTRIBUTE_NODE_SIZE ) != null )
                    {
                        addedNode.setProperty( getProperty( ATTRIBUTE_NODE_SIZE ), file.length(  ) );
                    }

                    if ( getProperty( MIXIN_REFERENCEABLE ) != null )
                    {
                        addedNode.addMixin( getProperty( MIXIN_REFERENCEABLE ) );
                    }

                    setName( addedNode, strFilePath );

                    setContent( addedNode, new FileInputStream( file ), strFilePath, strMimeType );
                    session.save(  );
                    String strIdResource = workspace.getId(  ) + "-" + addedNode.getUUID(  );
                    IndexationService.addIndexerAction( strIdResource,
                        JcrIndexer.class.getSimpleName(  ), IndexerAction.TASK_CREATE );
                    JcrIndexerUtils.addIndexerAction( strIdResource, IndexerAction.TASK_CREATE );
                    
                    return null;
                }
            } );
    }

    /**
     * @param strWorkspace the workspace name
     * @param strPath the absolute path to delete
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#delete(java.lang.String, java.lang.String)
     */
    public void delete( String strWorkspace, final String strPath )
    {
        Map<String, AdminWorkspace> adminWorkspaces = AdminJcrHome.getInstance(  )
                                                                  .getWorkspacesListByNameKey( PluginService.getPlugin( 
                    JcrPlugin.PLUGIN_NAME ) );
        final AdminWorkspace workspace = adminWorkspaces.get( strWorkspace );

        execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    Node currentNode = getNode( session, strPath );

                    if ( isLocked( currentNode ) && !ownsLock( currentNode ) )
                    {
                        throw new JcrNodeLockedException( currentNode );
                    }

                    String strIdResource = workspace.getId(  ) + "-" + currentNode.getUUID(  );
                    IndexationService.addIndexerAction( strIdResource + "_" +
                        JcrIndexer.SHORT_NAME, JcrIndexer.class.getSimpleName(  ), IndexerAction.TASK_DELETE );
                    JcrIndexerUtils.addIndexerAction( strIdResource, IndexerAction.TASK_DELETE );
                    
                    currentNode.remove(  );
                    session.save(  );

                    return null;
                }
            } );
    }

    /**
     * @param strWorkspace the workspace name
     * @param strPath the absolute path
     * @return a collection of IRepositoryFile contained in strPath
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#listFiles(java.lang.String, java.lang.String)
     */
    @SuppressWarnings( "unchecked" )
    public List<IRepositoryFile> listFiles( String strWorkspace, final String strPath )
    {
        return (List<IRepositoryFile>) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    Node currentNode = getNode( session, strPath );

                    if ( currentNode == null )
                    {
                        return null;
                    }

                    ArrayList<IRepositoryFile> listFiles = new ArrayList<IRepositoryFile>(  );

                    NodeIterator it = currentNode.getNodes(  );

                    while ( it.hasNext(  ) )
                    {
                        Node node = it.nextNode(  );

                        // only select files and directory
                        if ( isDirectory( node ) || isFile( node ) )
                        {
                            listFiles.add( nodeToRepositoryFile( node ) );
                        }
                    }

                    return listFiles;
                }
            } );
    }

    /**
     * @param strWorkspace the workspace name
     * @param strId the id to find
     * @return an IRepositoryFile
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#findById(java.lang.String, java.lang.String)
     */
    public IRepositoryFile findById( String strWorkspace, final String strId )
    {
        return (IRepositoryFile) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    if ( strId == null )
                    {
                        throw new NullPointerException(  );
                    }

                    Node currentNode = getNodeById( session, strId );

                    if ( currentNode == null )
                    {
                        return null;
                    }
                    else
                    {
                        return nodeToRepositoryFile( currentNode );
                    }
                }
            } );
    }

    /**
     * @param strWorkspace the workspace name
     * @param strId the id to find
     * @param strVersion the version name to retrieve
     * @return an IRepositoryFile
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#findById(java.lang.String, java.lang.String)
     */
    public IRepositoryFile findById( String strWorkspace, final String strId, final String strVersion )
    {
        return (IRepositoryFile) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    if ( strId == null )
                    {
                        throw new NullPointerException(  );
                    }

                    Node currentNode = getNodeById( session, strId );

                    if ( currentNode == null )
                    {
                        return null;
                    }
                    else
                    {
                        Version version = currentNode.getVersionHistory(  ).getVersion( strVersion );

                        return nodeToRepositoryFile( version );
                    }
                }
            } );
    }

    /**
     * Set the content of a node
     * @param node the node
     * @param inputStream a stream with the content
     * @param strFileName the file name, the mime type is determined with this file name
     * @return true if the transfer is ok and node is a file, false otherwise
     * @throws FileNotFoundException
     */
    protected boolean setContent( Node node, InputStream inputStream, String strFileName, String strMimeType )
    {
        try
        {
            if ( isDirectory( node ) )
            {
                return false;
            }

            if ( strMimeType == null )
            {
                strMimeType = getProperty( DEFAULT_MIME_TYPE );
            }

            Node resNode = null;

            if ( !node.hasNode( getProperty( NODE_TYPE_JCR_CONTENT ) ) )
            {
                resNode = node.addNode( getProperty( NODE_TYPE_JCR_CONTENT ), getProperty( NODE_TYPE_FILE_RESOURCE ) );
            }
            else
            {
                resNode = node.getNode( getProperty( NODE_TYPE_JCR_CONTENT ) );
            }

            resNode.setProperty( getProperty( PROPERTY_JCR_MIMETYPE ), strMimeType );
            resNode.setProperty( getProperty( PROPERTY_JCR_DATA ), inputStream );
            resNode.setProperty( getProperty( PROPERTY_JCR_LASTMODIFIED ), Calendar.getInstance(  ) );

            return true;
        }
        catch ( RepositoryException e )
        {
            throw new JcrRepositoryException( e );
        }
    }

    /**
     * @param strWorkspace the workspace name
     * @param strPath the absolute path
     * @return an IRepositoryFile
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#findByPath(java.lang.String, java.lang.String)
     */
    public IRepositoryFile findByPath( String strWorkspace, final String strPath )
    {
        return (IRepositoryFile) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    AppLogService.debug( strPath );

                    Node currentNode = getNode( session, strPath );

                    return nodeToRepositoryFile( currentNode );
                }
            } );
    }

    /**
     * Get a node by its path
     * @param session the session
     * @param strPath the absolute path
     * @return a node
     */
    private Node getNode( Session session, String strPath )
    {
        String strRelPath = ( strPath == null ) ? "" : strPath.replaceFirst( getProperty( REGEXP_ABSOLUTE_PATH ), "" );

        try
        {
            if ( "".equals( strRelPath ) )
            {
                // we're looking for the root node
                return getRootNode( session );
            }
            else
            {
                return getRootNode( session ).getNode( strRelPath );
            }
        }
        catch ( PathNotFoundException e )
        {
            throw new JcrPathNotFoundException( e );
        }
        catch ( RepositoryException e )
        {
            throw new JcrRepositoryException( e );
        }
    }

    /**
     * Return the root node of this repository depending on ROOT_NODE_PATH
     * @param session the session
     * @return the root node
     * @throws RepositoryException occurs on other error
     */
    private Node getRootNode( Session session ) throws RepositoryException
    {
        return ( getProperty( ROOT_NODE_PATH ) == null ) ? session.getRootNode(  )
                                                         : session.getRootNode(  ).getNode( getProperty( ROOT_NODE_PATH ) );
    }

    /**
     * @param session the session to use
     * @param strId the id to find
     * @return a node or null if node is not found
     * @throws RepositoryException occurs on other error
     */
    private Node getNodeById( Session session, String strId )
        throws RepositoryException
    {
        Node result = null;

        try
        {
            result = session.getNodeByUUID( strId );
        }
        catch ( ItemNotFoundException e )
        {
            AppLogService.debug( "Item not found : " + strId + "(" + e.getMessage(  ) + ")" );
        }

        return result;
    }

    /**
     * Instanciate a IRepositoryFile from node informations
     * @param node the node
     * @return an IRepositoryFile associated with this node
     */
    private IRepositoryFile nodeToRepositoryFile( Node node )
    {
        IRepositoryFile addedFile;

        if ( isVersionnable( node ) )
        {
            VersionnableRepositoryFileImpl version = new VersionnableRepositoryFileImpl(  );
            nodeToRepositoryFile( node, version );
            version.setVersion( getVersion( node ) );
            addedFile = version;
        }
        else
        {
            addedFile = nodeToRepositoryFile( node, new JcrRepositoryFileImpl(  ) );
        }

        return addedFile;
    }

    /**
     * Instanciate a IRepositoryFile from node informations
     * @param node the node
     * @param addedFile the file to initialize
     * @return an IRepositoryFile associated with this node
     */
    private IRepositoryFile nodeToRepositoryFile( Node node, AbstractRepositoryFile addedFile )
    {
        AppLogService.debug( "Node to convert : " + node );
        addedFile.setAbsolutePath( getAbsolutePath( node ) );
        addedFile.setExists( exists( node ) );
        addedFile.setDirectory( isDirectory( node ) );
        addedFile.setFile( isFile( node ) );
        addedFile.setName( getName( node ) );

        addedFile.setParentId( getParentUUID( node ) );
        addedFile.setResourceId( getUUID( node ) );
        addedFile.setPath( getPath( node ) );

        if ( addedFile.isFile(  ) )
        {
            addedFile.setMimeType( getMimeType( node ) );
            addedFile.setContent( getFileContent( node ) );
            addedFile.setLastModified( getLastModified( node ) );
            addedFile.setLength( getSize( node ) );
            addedFile.setLock( isLocked( node ) );
            addedFile.setOwnsLock( ownsLock( node ) );
        }

        if ( addedFile.isDirectory(  ) )
        {
            addedFile.setLastModified( getCreated( node ) );
        }

        AppLogService.debug( "Converted node : " + addedFile );

        return addedFile;
    }

    /**
     * Instantiates a IRepositorfyFile from a version
     * @param version the node version
     * @return an IRepositoryFile
     */
    private IRepositoryFile nodeToRepositoryFile( Version version )
    {
        VersionFileImpl file = new VersionFileImpl(  );

        try
        {
            // we get the first child of this version
            Node childNode = version.getNodes(  ).nextNode(  );
            nodeToRepositoryFile( childNode, file );
            file.setName( version.getName(  ) );

            //            file.setContent( getFileContent( childNode ) );
            //            file.setLastModified( getLastModified( childNode ) );
            //            file.setResourceId( getUUID( childNode ) );
        }
        catch ( RepositoryException e )
        {
            AppLogService.debug( "Can't convert " + version + " to RepositoryFile (" + e + ")" );
        }

        return file;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#store(java.lang.String, fr.paris.lutece.plugins.jcr.business.IRepositoryFile)
     */
    public void store( String strWorkspace, final IRepositoryFile file )
    {
        final AdminWorkspace workspace = AdminJcrHome.getInstance(  )
                                                     .getWorkspacesListByNameKey( PluginService.getPlugin( 
                    JcrPlugin.PLUGIN_NAME ) ).get( strWorkspace );

        execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    Node currentNode = getNodeById( session, file.getResourceId(  ) );

                    if ( isLocked( currentNode ) && !ownsLock( currentNode ) )
                    {
                        throw new JcrNodeLockedException( currentNode );
                    }

                    boolean bIsVersionable = isVersionnable( currentNode );

                    if ( bIsVersionable )
                    {
                        currentNode.checkout(  );
                    }

                    setContent( currentNode, file.getContent(  ), file.getName(  ), file.getMimeType(  ) );

                    if ( bIsVersionable )
                    {
                        currentNode.save(  );

                        Version v = currentNode.checkin(  );
                        AppLogService.debug( "New version is : " + v.getName(  ) );
                    }

                    String strIdResource = workspace.getId(  ) + "-" + currentNode.getUUID(  );
                    IndexationService.addIndexerAction( strIdResource,
                        JcrIndexer.class.getSimpleName(  ), IndexerAction.TASK_MODIFY );
                    JcrIndexerUtils.addIndexerAction( strIdResource, IndexerAction.TASK_MODIFY );
                    
                    session.save(  );

                    return null;
                }
            } );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#setVersionnable(java.lang.String, java.lang.String, boolean)
     */
    public void setVersionnable( final String strWorkspace, final String strNodeId, final boolean isVersionnable )
    {
        execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    String strVersionable = getProperty( MIXIN_VERSIONNABLE );

                    if ( strVersionable == null )
                    {
                        return null;
                    }

                    Node currentNode = getNodeById( session, strNodeId );

                    if ( isLocked( currentNode ) && !ownsLock( currentNode ) )
                    {
                        throw new JcrNodeLockedException( currentNode );
                    }

                    boolean isCurrentNodeVersionnable = isVersionnable( currentNode );

                    if ( !isVersionnable && isCurrentNodeVersionnable )
                    {
                        currentNode.removeMixin( strVersionable );
                        currentNode.save(  );
                    }
                    else if ( isVersionnable && !isCurrentNodeVersionnable )
                    {
                        currentNode.addMixin( strVersionable );
                        currentNode.save(  );

                        Version firstVersion = currentNode.checkin(  );
                        AppLogService.debug( "Creating version " + firstVersion.getName(  ) + " for file " +
                            currentNode.getPath(  ) );
                    }

                    session.save(  );

                    return null;
                }
            } );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#getHistory(java.lang.String, java.lang.String)
     */
    @SuppressWarnings( "unchecked" )
    public List<IRepositoryFile> getHistory( String strWorkspace, final String strNodeId )
    {
        return (List<IRepositoryFile>) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    Node currentNode = getNodeById( session, strNodeId );
                    boolean isCurrentNodeVersionnable = isVersionnable( currentNode );

                    if ( !isCurrentNodeVersionnable )
                    {
                        return null;
                    }

                    ArrayList<IRepositoryFile> resultList = new ArrayList<IRepositoryFile>(  );
                    VersionHistory history = currentNode.getVersionHistory(  );

                    for ( VersionIterator it = history.getAllVersions(  ); it.hasNext(  ); )
                    {
                        Version version = (Version) it.next(  );

                        if ( !"jcr:rootVersion".equals( version.getName(  ) ) )
                        {
                            resultList.add( nodeToRepositoryFile( version ) );
                        }
                    }

                    return resultList;
                }
            } );
    }

    /**
     * Tells whether a node is versionable
     * @param node to test
     * @return true if it is versionable (ie contains a mixin cm:versionable)
     */
    protected boolean isVersionnable( Node node )
    {
        String strMixinVersionnable = getProperty( MIXIN_VERSIONNABLE );

        if ( strMixinVersionnable == null )
        {
            return false;
        }

        try
        {
            for ( NodeType mixinNode : node.getMixinNodeTypes(  ) )
            {
                if ( ( mixinNode != null ) && strMixinVersionnable.equals( mixinNode.getName(  ) ) )
                {
                    return true;
                }
            }
        }
        catch ( RepositoryException e )
        {
            // nothing to do, we just log the exception
            AppLogService.error( node, e );
        }

        return false;
    }

    /**
     * Gets the version of a node
     * @param node the node
     * @return a verion name (ie 1.0, 1.1, ...)
     */
    private String getVersion( Node node )
    {
        String currentVersion = null;

        try
        {
            Version baseVersion = node.getBaseVersion(  );

            if ( baseVersion != null )
            {
                currentVersion = baseVersion.getName(  );
            }
        }
        catch ( RepositoryException e )
        {
            // log the exception
            AppLogService.error( node, e );
        }

        return currentVersion;
    }

    /**
     * Updates the name of a node
     * @param node the node
     * @param strPath the new name
     * @throws RepositoryException when error occurs
     */
    private void setName( Node node, String strPath ) throws RepositoryException
    {
        if ( getProperty( PROPERTY_JCR_NAME ) != null )
        {
            node.setProperty( getProperty( PROPERTY_JCR_NAME ), new File( strPath ).getName(  ) );
        }
    }

    /**
     * Retrieves a configuration property
     * @param strPropertyName the property key
     * @return the value
     */
    protected String getProperty( String strPropertyName )
    {
        return getProperties(  ).getProperty( strPropertyName );
    }

    /**
     * Tells if a node is locked
     * @param node the node
     * @return true if the node is locked, false otherwise
     */
    private boolean isLocked( Node node )
    {
        try
        {
            return node.isLocked(  );
        }
        catch ( RepositoryException e )
        {
            AppLogService.error( node, e );
        }

        return false;
    }

    /**
     * Tells if the current user owns a lock on node
     * @param node the node
     * @return true if the user has a lock on this node
     */
    protected boolean ownsLock( Node node )
    {
        try
        {
            boolean ownsLock = false;

            if ( node.isLocked(  ) )
            {
                Lock lock = node.getLock(  );
                String strLockToken = lock.getLockToken(  );
                ownsLock = ( ( strLockToken != null ) && !"".equals( strLockToken ) );
            }

            return ownsLock;
        }
        catch ( RepositoryException e )
        {
            AppLogService.error( node, e );
        }

        return false;
    }

    /**
     * Gets the creation date of the node
     *
     * It must be overloaded by children classes
     *
     * @param node the node
     * @return the creation date of the node
     */
    protected abstract Calendar getCreated( Node node );

    /**
     * Gets the size of the content node
     *
     * It must only apply to node of type "file"
     * It must be overloaded by children classes
     * @param node the node
     * @return the size (in bytes) of the node. Undetermined if the node is a directory.
     */
    protected abstract long getSize( Node node );

    /**
     * Gets the date of last modification
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return the last modification date
     */
    protected abstract Calendar getLastModified( Node node );

    /**
     * Gives access to node content.
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return an inputstream to the file content
     */
    protected abstract InputStream getFileContent( Node node );

    /**
     * Gives the Mime Type of a node
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return the mime type (if unknown, it should return application/octet-stream)
     */
    protected abstract String getMimeType( Node node );

    /**
     * Gives the path of a node
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return the path in the JCR
     */
    protected abstract String getPath( Node node );

    /**
     * Gives the reference of a node
     *
     * @param node the node
     * @return a reference (like 60c92f6b-a6d2-49d9-b9ec-03345a00e606)
     */
    protected String getUUID( Node node )
    {
        String result = null;

        try
        {
            result = node.getUUID(  );
        }
        catch ( RepositoryException e )
        {
            // do nothing: this node is not referenceable
            AppLogService.debug( "Node not referenceable : " + node );
        }

        return result;
    }

    /**
     * Gives the reference of the parent
     *
     * @param node the node
     * @return a reference of the parent if it exists, null otherwise.
     */
    protected String getParentUUID( Node node )
    {
        String result = null;

        try
        {
            result = getUUID( node.getParent(  ) );
        }
        catch ( RepositoryException e )
        {
            // do nothing: this node is not referenceable
            AppLogService.debug( "Parent node not referenceable for : " + node );
        }

        return result;
    }

    /**
     * Gives the name of a node
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return the name (generally, it is the file name)
     */
    protected abstract String getName( Node node );

    /**
     * Tells if a node is a file.
     *
     * Note on data structure : a file has content and has no child.
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return true if the node is a file, false otherwise
     */
    protected abstract boolean isFile( Node node );

    /**
     * Tells if a node is a directory
     *
     * Note on data structure : a directory doesn't contain anything except other
     * directories and files.
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return true if the node is a directory, false otherwise
     */
    protected abstract boolean isDirectory( Node node );

    /**
     * Tells if a node is present in the repository
     *
     * It must be overloaded by children classes
     * @param node the node
     * @return true if the node exists, false otherwise
     */
    protected abstract boolean exists( Node node );

    /**
     * Returns the entire path (absolute)
     * @param node the node
     * @return the absolute path of the node
     */
    protected abstract String getAbsolutePath( Node node );

    /**
     * Gives a way to the configuration properties
     * @return a Properties object
     */
    protected abstract Properties getProperties(  );

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#setLock(java.lang.String, fr.paris.lutece.plugins.jcr.business.IRepositoryFile, java.lang.String)
     */
    public String setLock( String strWorkspace, final IRepositoryFile file, final String strUsername )
    {
        return (String) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    Node node = getNodeById( session, file.getResourceId(  ) );

                    // we add the mixin "mix:lockable" on the first time
                    if ( node.canAddMixin( "mix:lockable" ) )
                    {
                        node.addMixin( "mix:lockable" );
                        node.save(  );
                    }

                    Lock lock = node.lock( false, false );
                    session.save(  );

                    String strLockToken = lock.getLockToken(  );
                    session.removeLockToken( strLockToken );
                    AppLogService.debug( "Lock token is " + strLockToken );

                    return strLockToken;
                }
            } );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#removeLock(java.lang.String, fr.paris.lutece.plugins.jcr.business.IRepositoryFile)
     */
    public String removeLock( String strWorkspace, final IRepositoryFile file )
    {
        return (String) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    Node node = getNodeById( session, file.getResourceId(  ) );

                    Lock lock = node.getLock(  );
                    node.unlock(  );
                    session.save(  );

                    return lock.getLockToken(  );
                }
            } );
    }

    protected int getDepth(  )
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFileDAO#getPathToFile(java.lang.String, java.lang.String)
     */
    public List<IRepositoryFile> getPathToFile( String strWorkspace, final String strFilePath )
    {
        return (List<IRepositoryFile>) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    ArrayList<IRepositoryFile> listOfFiles = new ArrayList<IRepositoryFile>(  );
                    Node currentNode = getNode( session, strFilePath );

                    nodeToRepositoryFile( currentNode );

                    Item parentNode;
                    int currentDepth = currentNode.getDepth(  );

                    for ( int depth = getDepth(  ); depth <= currentDepth; depth++ )
                    {
                        try
                        {
                            parentNode = currentNode.getAncestor( depth );
                        }
                        catch ( Exception e )
                        {
                            parentNode = null;
                        }

                        if ( ( parentNode != null ) && parentNode.isNode(  ) )
                        {
                            listOfFiles.add( nodeToRepositoryFile( (Node) parentNode ) );
                        }
                    }

                    return listOfFiles;
                }
            } );
    }
}
