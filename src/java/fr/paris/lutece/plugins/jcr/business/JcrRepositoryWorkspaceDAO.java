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

import fr.paris.lutece.plugins.jcr.util.JcrExistingRepositoryException;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.jackrabbit.core.WorkspaceImpl;

import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrSessionFactory;
import org.springmodules.jcr.JcrTemplate;
import org.springmodules.jcr.SessionFactoryUtils;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;


/**
 * An implementation of IworkspaceDAO for Jackrabbit JCR
 *
 */
public class JcrRepositoryWorkspaceDAO extends AbstractRepositoryDAO implements IWorkspaceDAO
{
    protected static final String NODE_TYPE_FOLDER = "nt:folder";
    protected static final String NODE_TYPE_FILE = "nt:file";
    protected static final String NODE_ROLES = "lutece:roles";
    protected static final String PROPERTY_DELETED = "lutece:deleted";

    /** This class implements the Singleton design pattern. */
    private static JcrRepositoryWorkspaceDAO _dao;
    private boolean _creationAllowed;

    ///////////////////////////////////////////////////////////////////////////////////////
    // Constructor and instantiation methods
    /**
     * @param jcrTemplate a jcrTemplate
     * @param repositoryInitializer a repositoryInitializer
     * @param strDefaultWorkspaceName a default workspace name
     * @param creationAllowed workspace creation allowed ?
     * @return an instance of this DAO
     */
    public static synchronized IWorkspaceDAO getInstance( JcrTemplate jcrTemplate,
        IRepositoryInitializer repositoryInitializer, String strDefaultWorkspaceName, boolean creationAllowed )
    {
        if ( _dao == null )
        {
            _dao = new JcrRepositoryWorkspaceDAO(  );
            _dao.init( jcrTemplate, repositoryInitializer, null );
            _dao._creationAllowed = creationAllowed;
        }

        return _dao;
    }

    /**
     * @param strWorkspace the workspace to create
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#create(java.lang.String)
     */
    public void create( final String strWorkspace )
    {
        String strExistingWorkspace = (String) execute( new JcrCallback(  )
                {
                    public Object doInJcr( Session session )
                        throws IOException, RepositoryException
                    {
                        WorkspaceImpl workspace = (WorkspaceImpl) session.getWorkspace(  );
                        String[] workspaces = workspace.getAccessibleWorkspaceNames(  );

                        boolean found = false;
                        String strWorkspaceName = null;

                        for ( int i = 0; !found && ( i < workspaces.length ); i++ )
                        {
                            strWorkspaceName = workspaces[i];
                            found = strWorkspace.equals( strWorkspaceName );
                        }

                        if ( found )
                        {
                            // this workspace is considered as deleted
                            // so we don't have to create it
                            AppLogService.debug( "Reuse of old deleted workspace" );

                            return strWorkspaceName;

                            //                    if ( session.getRootNode().hasProperty( PROPERTY_DELETED ) &&
                            //                        session.getRootNode().getProperty( PROPERTY_DELETED ).getBoolean() )
                            //                    {
                            //                    }
                        }
                        else
                        {
                            workspace.createWorkspace( strWorkspace );
                        }

                        return null;
                    }
                } );

        if ( strExistingWorkspace != null )
        {
            Boolean isDeleted = (Boolean) execute( strExistingWorkspace,
                    new JcrCallback(  )
                    {
                        public Object doInJcr( Session session )
                            throws IOException, RepositoryException
                        {
                            return session.getRootNode(  ).hasProperty( PROPERTY_DELETED ) &&
                            session.getRootNode(  ).getProperty( PROPERTY_DELETED ).getBoolean(  );
                        }
                    } );

            if ( !isDeleted )
            {
                throw new JcrExistingRepositoryException( strExistingWorkspace );
            }
        }

        execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    // remove the deleted flag
                    session.getRootNode(  ).setProperty( PROPERTY_DELETED, (Value) null );

                    Node nodeRoles = session.getRootNode(  ).addNode( NODE_ROLES );

                    // roles properties initialization
                    for ( String strAccessType : IWorkspace.AVAILABLE_ACCESS )
                    {
                        nodeRoles.setProperty( strAccessType, new String[0] );
                    }

                    session.save(  );

                    return null;
                }
            } );
    }

    /**
     * We can't delete programmatically a jackrabbit workspace.
     * So we put a flag to say this workspace is considered as deleted.
     * The content of the worskpace is also deleted.
     * @param strWorkspace the workspace to delete
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#delete(java.lang.String)
     */
    public void delete( String strWorkspace )
    {
        execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    NodeIterator nodeIterator = session.getRootNode(  ).getNodes(  );

                    while ( nodeIterator.hasNext(  ) )
                    {
                        Node node = (Node) nodeIterator.next(  );

                        if ( node.isNodeType( NODE_TYPE_FILE ) || node.isNodeType( NODE_TYPE_FOLDER ) )
                        {
                            node.remove(  );
                        }
                    }

                    session.getRootNode(  ).setProperty( PROPERTY_DELETED, true );
                    //                    session.getRootNode().save();
                    session.save(  );

                    //                    WorkspaceImpl workspace = (WorkspaceImpl) session.getWorkspace();
                    //                    String strWorkspaceDir = workspace.getConfig().getHomeDir();
                    //                    
                    return null;
                }
            } );

        //        File workspaceFile = new File( strWorkspacePath );
        //        try
        //        {
        //            FileUtil.delete(workspaceFile);
        //        }
        //        catch (IOException e)
        //        {
        //            AppLogService.info("JSR170 : Failed to delete workspace directory " + workspaceFile + " " + e.getLocalizedMessage( ) );
        //        }
    }

    /**
     * @param id the id to search for
     * @return the IWorkspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#findById(java.lang.String)
     */
    public IWorkspace findById( String id )
    {
        // not implemented
        return null;
    }

    /**
     * @param strWorkspace the name of the workspace to search
     * @return the IWorkspace
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#findByName(java.lang.String)
     */
    public IWorkspace findByName( final String strWorkspace )
    {
        return (IWorkspace) execute( strWorkspace,
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    JcrRepositoryWorkspaceImpl workspace = new JcrRepositoryWorkspaceImpl(  );

                    if ( session.getRootNode(  ).hasNode( NODE_ROLES ) )
                    {
                        Node nodeRoles = session.getRootNode(  ).getNode( NODE_ROLES );
                        setRoles( nodeRoles, workspace );
                    }
                    else
                    {
                        for ( String strAccessType : IWorkspace.AVAILABLE_ACCESS )
                        {
                            workspace.setRoles( strAccessType, new String[] {  } );
                        }
                    }

                    workspace.setName( strWorkspace );
                    workspace.setId( strWorkspace );

                    return workspace;
                }
            } );
    }

    /**
     * @return all availables Workspaces
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#getAvailableWorkspaces()
     */
    public String[] getAvailableWorkspaces(  )
    {
        return (String[]) execute( "default",
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    return session.getWorkspace(  ).getAccessibleWorkspaceNames(  );
                }
            } );
    }

    /**
     * @param workspace the workspace to update
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#store(fr.paris.lutece.plugins.jcr.business.IWorkspace)
     */
    public void store( final IWorkspace workspace )
    {
        execute( workspace.getName(  ),
            new JcrCallback(  )
            {
                public Object doInJcr( Session session )
                    throws IOException, RepositoryException
                {
                    Node nodeRoles;

                    if ( session.getRootNode(  ).hasNode( NODE_ROLES ) )
                    {
                        nodeRoles = session.getRootNode(  ).getNode( NODE_ROLES );
                    }
                    else
                    {
                        nodeRoles = session.getRootNode(  ).addNode( NODE_ROLES );
                    }

                    for ( String strAccessType : IWorkspace.AVAILABLE_ACCESS )
                    {
                        String[] roles = workspace.getRoles( strAccessType );
                        // sort roles before insertion 
                        Arrays.sort( roles );
                        nodeRoles.setProperty( strAccessType, roles );
                    }

                    session.save(  );

                    return null;
                }
            } );
    }

    /**
     * @param nodeRoles the node to update
     * @param workspace the workspace to update
     * @throws RepositoryException an exception
     */
    private void setRoles( Node nodeRoles, IWorkspace workspace )
        throws RepositoryException
    {
        for ( String strAccessType : IWorkspace.AVAILABLE_ACCESS )
        {
            if ( nodeRoles.hasProperty( strAccessType ) )
            {
                Property nodeReadRoles = nodeRoles.getProperty( strAccessType );
                ArrayList<String> listReadRoles = new ArrayList<String>(  );

                for ( Value value : nodeReadRoles.getValues(  ) )
                {
                    listReadRoles.add( value.getString(  ) );
                }

                workspace.setRoles( strAccessType, listReadRoles.toArray( new String[0] ) );
            }
            else
            {
                workspace.setRoles( strAccessType, new String[] {  } );
            }
        }
    }

    /**
     * @return the value declared in Spring context
     * @see fr.paris.lutece.plugins.jcr.business.IWorkspaceDAO#canCreate()
     */
    public boolean canCreate(  )
    {
        return _creationAllowed;
    }
}
