/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.jcr.business.portlet;

import fr.paris.lutece.plugins.jcr.authentication.JsrUser;
import fr.paris.lutece.plugins.jcr.business.IRepositoryFile;
import fr.paris.lutece.plugins.jcr.business.IWorkspace;
import fr.paris.lutece.plugins.jcr.business.RepositoryFileHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminJcrHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminView;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents business objects Jsr170Portlet
 */
public class Jsr170Portlet extends Portlet
{
    ///////////////////////////////////////////////////////////////////////
    // Constants

    // Xml tags
    private static final String TAG_PORTLET_JSR170 = "jsr170-portlet";
    private static final String TAG_DIRECTORY_PATH = "directory-path";
    private static final String TAG_CURRENT_VIEW_ID = "current-view-id";
    private static final String TAG_CAN_READ = "canRead";
    private static final String TAG_CAN_WRITE = "canWrite";
    private static final String TAG_CAN_REMOVE = "canRemove";
    private static final String TAG_BREADCRUMBS = "breadcrumbs";
    private static final String TAG_BREADCRUMB = "breadcrumb";
    private static final String TAG_BREADCRUMB_NAME = "breadcrumb-name";
    private static final String TAG_BREADCRUMB_PATH = "breadcrumb-path-id";
    private static final String TAG_ERROR_UPLOAD = "error-upload";
    private static final String TAG_PORTLET_JSR170_ERROR = "jsr170-portlet-error";
    private static final String TAG_ERROR = "error";
    private static final String PARAMETER_FILE_ID = "file_id";
    private static final String PARAMETER_ERROR_UPLOAD = "error_upload";
    private static final String PARAMETER_PORTLET_ID = "portlet_id";
    private static final String PARAMETER_ACTION = "action";
    private static final String ACTION_MODIFY = "modify";
    private static final String TAG_PORTLET_JSR170_MODIFY = "jsr170-portlet-modify";
    private static final String ACTION_HISTORY = "history";
    private static final String TAG_PORTLET_JSR170_HISTORY = "jsr170-portlet-history";
    private static final String TAG_FILE_ID = "file-id";
    private static final String PARAMETER_VIEW_ID = "view_id";
    private static final String TAG_ADMIN_VIEW_COMBO = "admin-view-combo";
    private static final String TAG_PARENT_ID = "parent-id";

    // Variables declarations
    private int _nFolderListingFileId;
    private boolean _hasDefaultView;
    private int _nDefaultView = -1;
    private String _strFileId = "";
    private Plugin _plugin;
    private AdminView _view;
    private String _action;

    /**
     * Creates a new Jsr170Portlet object.
     */
    public Jsr170Portlet(  )
    {
        setPortletTypeId( Jsr170PortletHome.getInstance(  ).getPortletTypeId(  ) );
    }

    /**
     * Sets the Id of the folderlistingfile
     *
     * @param nFolderListingFileId the new Id
     */
    public void setFolderListingFileId( int nFolderListingFileId )
    {
        _nFolderListingFileId = nFolderListingFileId;
    }

    /**
     * Returns the Id of this folderlistingfile
     *
     * @return the folderlistingfile Id
     */
    public int getFolderListingFileId(  )
    {
        return _nFolderListingFileId;
    }

    /**
     * Returns the Xml code of the FolderListing portlet without XML heading
     *
     * @param request The http request
     * @return the Xml code of the FolderListing portlet content
     */
    public String getXml( HttpServletRequest request )
    {
        // happens during indexation
        if ( request == null )
        {
            return addPortletTags( new StringBuffer(  ) );
        }

        StringBuffer sbXml = new StringBuffer(  );

        try
        {
            // retrieve the folder path from the request
            getParameters( request );

            final AdminJcrHome adminJcrInstance = AdminJcrHome.getInstance(  );
            final RepositoryFileHome instance = RepositoryFileHome.getInstance(  );

            StringBuffer sbAdminViewCombo = new StringBuffer(  );

            if ( !_hasDefaultView )
            {
                // creation of combo list which contains available views
                Collection<AdminView> filteredViews = new ArrayList<AdminView>(  );
                Collection<AdminView> allViews = AdminJcrHome.getInstance(  ).findAllViews( _plugin );
                XmlUtil.beginElement( sbAdminViewCombo, TAG_ADMIN_VIEW_COMBO );

                List<String> availableJcrType = instance.getAvailableJcrList(  );

                for ( AdminView view : allViews )
                {
                    if ( adminJcrInstance.getAvailableAccess( view, getUserRoles( request ), IWorkspace.READ_ACCESS,
                                _plugin ) &&
                            availableJcrType.contains( adminJcrInstance.findJcrType( view.getId(  ), _plugin ) ) )
                    {
                        filteredViews.add( view );
                        sbAdminViewCombo.append( view.getXml(  ) );
                    }
                }

                XmlUtil.endElement( sbAdminViewCombo, TAG_ADMIN_VIEW_COMBO );

                String strViewId = request.getParameter( PARAMETER_VIEW_ID + "_" + getId(  ) );

                if ( strViewId == null )
                {
                    // we choose the first view in the list
                    _view = ( (ArrayList<AdminView>) filteredViews ).get( 0 );
                }
                else
                {
                    _view = AdminJcrHome.getInstance(  ).findViewById( Integer.parseInt( strViewId ), _plugin );
                }
            }

            AdminWorkspace adminWorkspace = adminJcrInstance.findWorkspaceById( _view.getWorkspaceId(  ), _plugin );

            if ( ACTION_MODIFY.equals( _action ) )
            {
                sbXml.append( getModifyXml( request, adminWorkspace ) );

                return addPortletTags( sbXml );
            }
            else if ( ACTION_HISTORY.equals( _action ) )
            {
                sbXml.append( getHistoryXml( request, adminWorkspace ) );

                return addPortletTags( sbXml );
            }

            sbXml = getBrowserXml( request, adminWorkspace, instance, sbAdminViewCombo, adminJcrInstance );
        }
        catch ( Exception e )
        {
            AppLogService.error( e, e );

            StringBuffer sbErrorXml = new StringBuffer(  );
            XmlUtil.beginElement( sbErrorXml, TAG_PORTLET_JSR170_ERROR );
            XmlUtil.addElement( sbErrorXml, TAG_ERROR, e.getLocalizedMessage(  ) );
            XmlUtil.endElement( sbErrorXml, TAG_PORTLET_JSR170_ERROR );

            return addPortletTags( sbErrorXml );
        }

        return addPortletTags( sbXml );
    }

    /**
     * Return the history list of a file
     * @param request the request
     * @param adminWorkspace the workspace which contains the file
     * @return an xml fragment with the file history
     */
    private StringBuffer getHistoryXml( HttpServletRequest request, AdminWorkspace adminWorkspace )
    {
        StringBuffer sbXml = new StringBuffer(  );
        String strFileId = request.getParameter( PARAMETER_FILE_ID );
        final RepositoryFileHome instance = RepositoryFileHome.getInstance(  );
        IRepositoryFile file = instance.getRepositoryFileById( adminWorkspace, strFileId, getJcrUser( request ) );
        XmlUtil.beginElement( sbXml, TAG_PORTLET_JSR170_HISTORY );

        if ( !hasDefaultView(  ) )
        {
            XmlUtil.addElement( sbXml, TAG_CURRENT_VIEW_ID, _view.getId(  ) );
        }

        XmlUtil.addElement( sbXml, TAG_FILE_ID, strFileId );
        XmlUtil.addElement( sbXml, TAG_PARENT_ID, file.getParentId(  ) );

        List<IRepositoryFile> history = instance.getFileHistory( adminWorkspace, strFileId, getJcrUser( request ) );

        for ( IRepositoryFile fileVersion : history )
        {
            sbXml.append( fileVersion.getXml(  ) );
            AppLogService.debug( fileVersion );
        }

        XmlUtil.endElement( sbXml, TAG_PORTLET_JSR170_HISTORY );

        return sbXml;
    }

    /**
     * Return the form used to modify a file
     * @param request the request
     * @param adminWorkspace the workspace which contains the file
     * @return an xml fragment which contains the form
     */
    private StringBuffer getModifyXml( HttpServletRequest request, AdminWorkspace adminWorkspace )
    {
        StringBuffer sbXml = new StringBuffer(  );
        final RepositoryFileHome instance = RepositoryFileHome.getInstance(  );
        IRepositoryFile repoFile = instance.getRepositoryFileById( adminWorkspace,
                request.getParameter( PARAMETER_FILE_ID ), getJcrUser( request ) );
        XmlUtil.beginElement( sbXml, TAG_PORTLET_JSR170_MODIFY );

        if ( !hasDefaultView(  ) )
        {
            XmlUtil.addElement( sbXml, TAG_CURRENT_VIEW_ID, _view.getId(  ) );
        }

        sbXml.append( repoFile.getXml(  ) );
        XmlUtil.addElement( sbXml, TAG_PARENT_ID, repoFile.getParentId(  ) );
        XmlUtil.endElement( sbXml, TAG_PORTLET_JSR170_MODIFY );

        return sbXml;
    }

    /**
     * Returns the Xml code of the FolderListing portlet with XML heading
     *
     * @param request the HttpServletRequest
     * @return the Xml code of the FolderListing portlet
     */
    public String getXmlDocument( HttpServletRequest request )
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request );
    }

    /**
     * Update the portlet
     */
    public void update(  )
    {
        Jsr170PortletHome.getInstance(  ).update( this );
    }

    /**
     * Remove portlet
     */
    public void remove(  )
    {
        Jsr170PortletHome.getInstance(  ).remove( this );
    }

    /**
     * Get portlet and workspace parameters from request
     * @param request the request
     */
    private void getParameters( HttpServletRequest request )
    {
        // this happens when the portlet is indexed
        if ( request == null )
        {
            return;
        }

        _strFileId = request.getParameter( PARAMETER_FILE_ID + "_" + this.getId(  ) );
        _plugin = PluginService.getPlugin( "jsr170" );

        if ( _hasDefaultView )
        {
            _view = AdminJcrHome.getInstance(  ).findViewById( _nDefaultView, _plugin );
        }

        _action = request.getParameter( PARAMETER_ACTION + "_" + this.getId(  ) );
    }

    /**
     * Get user roles from request
     * @param request the request
     * @return an array containing the user roles
     */
    private String[] getUserRoles( HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

        return ( ( user == null ) || ( user.getRoles(  ) == null ) ) ? new String[] { "none" } : user.getRoles(  );
    }

    /**
     * Sets the view of this portlet.
     * @param nViewId the view id
     */
    public void setDefaultView( int nViewId )
    {
        _hasDefaultView = nViewId > -1;
        _nDefaultView = nViewId;
    }

    /**
     * Gets the default view
     * @return the view id
     */
    public int getDefaultView(  )
    {
        return _nDefaultView;
    }

    /**
     * @return true if this portlet has a default view
     */
    public boolean hasDefaultView(  )
    {
        return _hasDefaultView;
    }

    /**
     * Builds the breadcrumbs given strPath.
     * @param workspace the workspace to use
     * @param strPath the path used to create the breadcrumbs
     * @param view the view
     * @return an xml fragment with each path element as (path name, path url)
     */
    private StringBuffer getBreadCrumbs( final AdminWorkspace workspace, final String strPath, AdminView view,
        JsrUser user )
    {
        final RepositoryFileHome instance = RepositoryFileHome.getInstance(  );

        StringBuffer sb = new StringBuffer(  );
        XmlUtil.beginElement( sb, TAG_BREADCRUMBS );

        IRepositoryFile defaultFolder;

        if ( view.getPath(  ) != null )
        {
            defaultFolder = instance.getRepositoryFileById( workspace, view.getPath(  ), user );
        }
        else
        {
            defaultFolder = instance.getRepositoryFile( workspace, "/", user );
        }

        List<IRepositoryFile> listOfFiles = instance.getPathToFile( workspace, strPath, user );
        int nStartIndex = listOfFiles.indexOf( defaultFolder ) + 1;

        for ( int i = nStartIndex; ( 0 <= i ) && ( i < listOfFiles.size(  ) ); i++ )
        {
            IRepositoryFile folder = listOfFiles.get( i );

            if ( folder.isDirectory(  ) )
            {
                XmlUtil.beginElement( sb, TAG_BREADCRUMB );
                XmlUtil.addElement( sb, TAG_BREADCRUMB_NAME, folder.getName(  ) );
                XmlUtil.addElement( sb, TAG_BREADCRUMB_PATH, folder.getResourceId(  ) );
                XmlUtil.endElement( sb, TAG_BREADCRUMB );
            }
        }

        XmlUtil.endElement( sb, TAG_BREADCRUMBS );

        return sb;
    }

    private static JsrUser getJcrUser( HttpServletRequest request )
    {
        return new JsrUser( SecurityService.getInstance(  ).getRegisteredUser( request ) );
    }

    private StringBuffer getBrowserXml( HttpServletRequest request, AdminWorkspace adminWorkspace,
        RepositoryFileHome instance, StringBuffer sbAdminViewCombo, AdminJcrHome adminJcrInstance )
    {
        StringBuffer sbXml = new StringBuffer(  );
        IRepositoryFile currentDir = null;

        if ( ( _strFileId == null ) || "".equals( _strFileId ) || "null".equals( _strFileId ) )
        {
            if ( ( _view.getPath(  ) == null ) || "".equals( _view.getPath(  ) ) )
            {
                currentDir = instance.getRepositoryFile( adminWorkspace, "/", getJcrUser( request ) );
            }
            else
            {
                currentDir = instance.getRepositoryFileById( adminWorkspace, _view.getPath(  ), getJcrUser( request ) );
            }
        }
        else
        {
            currentDir = instance.getRepositoryFileById( adminWorkspace, _strFileId, getJcrUser( request ) );
        }

        XmlUtil.beginElement( sbXml, TAG_PORTLET_JSR170 );

        if ( !hasDefaultView(  ) )
        {
            sbXml.append( sbAdminViewCombo );
            XmlUtil.addElement( sbXml, TAG_CURRENT_VIEW_ID, _view.getId(  ) );
        }

        String strErrorUpload = request.getParameter( PARAMETER_ERROR_UPLOAD );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = ( strPortletId == null ) ? 0 : Integer.parseInt( strPortletId );

        if ( ( getId(  ) == nPortletId ) && ( strErrorUpload != null ) && !strErrorUpload.equals( "" ) )
        {
            XmlUtil.addElement( sbXml, TAG_ERROR_UPLOAD, strErrorUpload );
        }

        XmlUtil.addElement( sbXml, TAG_DIRECTORY_PATH, currentDir.getAbsolutePath(  ) );
        sbXml.append( getBreadCrumbs( adminWorkspace, currentDir.getAbsolutePath(  ), _view, getJcrUser( request ) ) );

        boolean canRead = false;
        boolean canWrite = false;
        boolean canRemove = false;

        if ( adminWorkspace != null )
        {
            // check some rights
            canRead = adminJcrInstance.getAvailableAccess( _view, getUserRoles( request ), IWorkspace.READ_ACCESS,
                    _plugin );
            canWrite = adminJcrInstance.getAvailableAccess( _view, getUserRoles( request ), IWorkspace.WRITE_ACCESS,
                    _plugin );
            canRemove = adminJcrInstance.getAvailableAccess( _view, getUserRoles( request ), IWorkspace.REMOVE_ACCESS,
                    _plugin );

            if ( canRead )
            {
                for ( IRepositoryFile repoFile : instance.getRepositoryFileList( adminWorkspace,
                        currentDir.getAbsolutePath(  ), getJcrUser( request ) ) )
                {
                    sbXml.append( repoFile.getXml(  ) );
                }
            }
        }

        XmlUtil.addElement( sbXml, TAG_CAN_READ, String.valueOf( canRead ) );
        XmlUtil.addElement( sbXml, TAG_CAN_WRITE, String.valueOf( canWrite ) );
        XmlUtil.addElement( sbXml, TAG_CAN_REMOVE, String.valueOf( canRemove ) );
        XmlUtil.endElement( sbXml, TAG_PORTLET_JSR170 );

        return sbXml;
    }
}
