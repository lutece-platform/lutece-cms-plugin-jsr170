/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.jcr.web;

import fr.paris.lutece.plugins.jcr.authentication.JsrUser;
import fr.paris.lutece.plugins.jcr.business.IRepositoryFile;
import fr.paris.lutece.plugins.jcr.business.IWorkspace;
import fr.paris.lutece.plugins.jcr.business.RepositoryFileHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminJcrHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminView;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLock;
import fr.paris.lutece.plugins.jcr.business.lock.JcrLockHome;
import fr.paris.lutece.plugins.jcr.business.portlet.Jsr170PortletHome;
import fr.paris.lutece.plugins.jcr.service.AdminJcrService;
import fr.paris.lutece.plugins.jcr.util.JcrExistingRepositoryException;
import fr.paris.lutece.plugins.jcr.util.JcrExistingViews;
import fr.paris.lutece.plugins.jcr.util.JcrJspBeanUtils;
import fr.paris.lutece.plugins.jcr.util.JcrUserAccessDeniedException;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.html.EncodingService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.springframework.dao.DataAccessResourceFailureException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides methods needed to serve the user a jsr170 file
 */
public class RepositoryFileJspBean extends PluginAdminPageJspBean
{
    //////////////////////////////////////////////////////////////////////////
    // Constants
    public static final int FILE_NOT_FOUND = 0;
    public static final int FILE_NOT_ALLOWED = 1;
    public static final String RIGHT_JSR170_MANAGEMENT = "JSR170_MANAGEMENT";
    public static final String RIGHT_JSR170_VIEW_MANAGEMENT = "JSR170_VIEW_MANAGEMENT";

    // Parameters
    private static final String PARAMETER_FILE_NAME = "file";
    private static final String PARAMETER_DIRECTORY_NAME = "folder";
    private static final String PARAMETER_PORTLET_ID = "portlet_id";
    private static final String PARAMETER_PAGE_ID = "page_id";
    private static final String PARAMETER_MESSAGE_ERROR = "error";

    //JSP
    private static final String PROPERTY_ERROR_NOT_FOUND = "jsr170.error.NotFound";
    private static final String PROPERTY_ERROR_NOT_ALLOWED = "jsr170.error.NotAllowed";
    private static final String MARK_WORKSPACES_LIST = "workspaces_list";
    private static final String TEMPLATE_MANAGE_WORKSPACES = "/admin/plugins/jsr170/manage_workspaces.html";
    private static final String TEMPLATE_ADD_WORKSPACE = "/admin/plugins/jsr170/add_workspace.html";
    private static final String JSP_MANAGE_WORKSPACES = "ManageWorkspaces.jsp";
    private static final String PARAMETER_WORKSPACE_NAME = "workspace_name";
    private static final String TEMPLATE_MODIFY_WORKSPACE = "/admin/plugins/jsr170/modify_workspace.html";
    private static final String MARK_AVAILABLE_ROLES = "available_roles";
    private static final String PARAMETER_READ_ROLES = "read_roles";
    private static final String MARK_VIEW_READ_ROLES = "workspace_read_roles";
    private static final String MARK_VIEW_WRITE_ROLES = "workspace_write_roles";
    private static final String MARK_VIEW_REMOVE_ROLES = "workspace_remove_roles";
    private static final String PARAMETER_WRITE_ROLES = "write_roles";
    private static final String PARAMETER_REMOVE_ROLES = "remove_roles";
    private static final String PROPERTY_MANAGE_WORKSPACE = "jsr170.workspaces.pageTitle";
    private static final String PROPERTY_MANAGE_VIEW = "jsr170.views.pageTitle";
    private static final String MARK_VIEW_LIST = "views_list";
    private static final String TEMPLATE_MANAGE_VIEWS = "/admin/plugins/jsr170/manage_views.html";
    private static final String PARAMETER_JCR_TYPE = "jcr_type";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_WORKSPACE_ID = "workspace_id";
    private static final String PARAMETER_VIEW_NAME = "view_name";
    private static final String PARAMETER_USER = "view_user";
    private static final String PARAMETER_PASSWORD = "view_password";
    private static final String JSP_MANAGE_VIEWS = "ManageViews.jsp";
    private static final String TEMPLATE_ADD_VIEW = "/admin/plugins/jsr170/add_view.html";
    private static final String MARK_JCR_LIST = "jcr_list";
    private static final String MARK_WORKGROUP_LIST = "workgroup_list";
    private static final String PARAMETER_WORKSPACE_LABEL = "workspace_label";
    private static final String PARAMETER_VIEW_ID = "view_id";
    private static final String MARK_VIEW = "view";
    private static final String TEMPLATE_MODIFY_VIEW = "/admin/plugins/jsr170/modify_view.html";
    private static final String TEMPLATE_MODIFY_VIEW_ROLES = "/admin/plugins/jsr170/modify_view_roles.html";
    private static final String MARK_WORKSPACE = "workspace";
    private static final String MESSAGE_EXISTING_PORTLETS = "jsr170.message.existingPortlets";
    private static final String MESSAGE_EXISTING_VIEWS = "jsr170.message.existingViews";
    private static final String PATH_JSP = "jsp/admin/plugins/jsr170/";
    private static final String JSP_DELETE_WORKSPACE = "DoDeleteWorkspace.jsp";
    private static final String MESSAGE_CONFIRM_DELETE_WORKSPACE = "jsr170.message.confirmDeleteWorkspace";
    private static final String JSP_DELETE_VIEW = "DoDeleteView.jsp";
    private static final String MESSAGE_CONFIRM_DELETE_VIEW = "jsr170.message.confirmDeleteView";
    private static final String PARAMETER_PATH = "path";
    private static final String MARK_VIEW_PATHNAME_LIST = "view_pathname_list";
    private static final String MARK_FILE_LIST = "file_list";
    private static final String TEMPLATE_SELECT_VIEW_ROOT = "/admin/plugins/jsr170/select_view_root.html";
    private static final String TEMPLATE_WORKSPACE_BROWSER = "/admin/plugins/jsr170/browse_workspace.html";
    private static final String PARAMETER_FILE_ID = "file_id";
    private static final String MARK_ACTION = "action";
    private static final String MARK_GO_UP = "go_up";
    private static final String MARK_FILE = "file";
    private static final String MARK_PARENT_ID = "parent_id";
    private static final String MARK_WORKSPACE_BROWSER = "workspace_browser";
    private static final String PARAMETER_BROWSER_SELECTED_FILE = "browser_selected_file";
    private static final String JSP_MODIFY_VIEW = "ModifyView.jsp";
    private static final String MARK_PRETTY_PATH_VIEW = "pretty_path";
    private static final String TEMPLATE_MANAGE_LOCKS = "/admin/plugins/jsr170/manage_locks.html";
    private static final String PROPERTY_MANAGE_LOCK = "todo";
    private static final String MARK_LOCK_LIST = "lock_list";
    private static final String MESSAGE_CONFIRM_DELETE_LOCK = "jsr170.message.confirmDeleteLock";
    private static final String PARAMETER_LOCK_ID = "lock_id";
    private static final String JSP_DELETE_LOCK = "DoDeleteLock.jsp";
    private static final String JSP_MANAGE_LOCKS = "ManageLocks.jsp";
    private static final String MESSAGE_EXISTING_WORKSPACE = "jsr170.message.existingWorkspace";

    /**
     * Public constructor
     */
    public RepositoryFileJspBean(  )
    {
    }

    /**
     * Get the directory path of the file to display from the request
     *
     * @param request the http request
     *
     * @return the path of the directory containing the file to display (given from the root folder defined for the
     *         porltet)
     */
    public String getDirectoryPath( HttpServletRequest request )
    {
        String strDirPath = request.getParameter( PARAMETER_DIRECTORY_NAME );

        return strDirPath;
    }

    /**
     * Return the url of the error page
     * @param request the request
     * @param nErrorType the error type
     * @return the url string
     */
    public String getFileErrorUrl( HttpServletRequest request, int nErrorType )
    {
        String strError = null;
        String strDirPath = request.getParameter( PARAMETER_DIRECTORY_NAME );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );

        switch ( nErrorType )
        {
            case FILE_NOT_FOUND:
                strError = I18nService.getLocalizedString( PROPERTY_ERROR_NOT_FOUND, request.getLocale(  ) );

                break;

            case FILE_NOT_ALLOWED:
                strError = I18nService.getLocalizedString( PROPERTY_ERROR_NOT_ALLOWED, request.getLocale(  ) );

                break;

            default:
                strError = I18nService.getLocalizedString( PROPERTY_ERROR_NOT_FOUND, request.getLocale(  ) );

                break;
        }

        strError = EncodingService.encodeUrl( strError );

        String strBaseUrl = AppPathService.getBaseUrl( request );
        String strParam = PARAMETER_MESSAGE_ERROR + "=" + strError + "&" + PARAMETER_PAGE_ID + "=" + strPageId + "&" +
            PARAMETER_DIRECTORY_NAME + "_" + strPortletId + "=" + strDirPath + "#" + PARAMETER_PORTLET_ID + "_" +
            strPortletId;
        String strUrl = strBaseUrl + AppPathService.getPortalUrl(  ) + "?" + strParam;

        return strUrl;
    }

    /**
     * Get the name of the file to display from the request
     *
     * @param request the http request
     *
     * @return the name of the file to display
     */
    public String getFilename( HttpServletRequest request )
    {
        String strFileName = request.getParameter( PARAMETER_FILE_NAME );

        return strFileName;
    }

    /**
     * Check that the user can view the file requested
     *
     * @param request the http request
     *
     * @return true if the user has the right to view the file, false otherwise
     */
    public boolean checkRights( HttpServletRequest request )
    {
        return true;
    }

    /**
     * Get the workspace management screen
     * @param request the request
     * @return html code
     */
    public String getManageWorkspaces( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_WORKSPACE );

        Collection<AdminWorkspace> availableWorkspaces = AdminJcrHome.getInstance(  ).findAllWorkspaces( getPlugin(  ) );
        Map<String, String> allJcrMap = new HashMap<String, String>(  );

        for ( ReferenceItem jcr : RepositoryFileHome.getInstance(  ).getAvailableJcr(  ) )
        {
            allJcrMap.put( jcr.getCode(  ), jcr.getName(  ) );
        }

        // find availables workspaces that work on available jcr
        Collection<AdminWorkspace> authorizedWorkspaces = new ArrayList<AdminWorkspace>(  );

        for ( AdminWorkspace workspace : (Collection<AdminWorkspace>) AdminWorkgroupService.getAuthorizedCollection( 
                availableWorkspaces, getUser(  ) ) )
        {
            if ( allJcrMap.containsKey( workspace.getJcrType(  ) ) )
            {
                authorizedWorkspaces.add( workspace );
            }
        }

        // transform referenceList of jcr to a map
        Map<String, String> jcrMap = new HashMap<String, String>(  );

        for ( ReferenceItem jcr : getModifiableJcr(  ) )
        {
            jcrMap.put( jcr.getCode(  ), jcr.getName(  ) );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKSPACES_LIST, authorizedWorkspaces );
        model.put( MARK_JCR_LIST, jcrMap );
        model.put( MARK_WORKGROUP_LIST, getWorkgroupMap(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_WORKSPACES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the workspace management screen
     * @param request the request
     * @return html code
     */
    public String getManageViews( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_VIEW );

        Map<String, AdminWorkspace> availableWorkspaces = AdminJcrHome.getInstance(  ).getWorkspacesList( getPlugin(  ) );
        Collection<AdminView> adminViewsList = AdminJcrHome.getInstance(  ).findAllViews( getPlugin(  ) );
        Collection<AdminView> authorizedViews = AdminWorkgroupService.getAuthorizedCollection( adminViewsList,
                getUser(  ) );

        AdminJcrHome adminJcrInstance = AdminJcrHome.getInstance(  );
        HashMap<String, String> mapPathName = new HashMap<String, String>(  );
        RepositoryFileHome instance = RepositoryFileHome.getInstance(  );
        List<String> availableJcr = instance.getAvailableJcrList(  );
        Collection<AdminView> finalAuthorizedViews = new ArrayList<AdminView>(  );

        for ( AdminView view : authorizedViews )
        {
            AdminWorkspace workspace = adminJcrInstance.findWorkspaceById( view.getWorkspaceId(  ), getPlugin(  ) );

            // we check if the jcr is currently accessible
            if ( !availableJcr.contains( workspace.getJcrType(  ) ) )
            {
                continue;
            }

            IRepositoryFile rootFile = null;

            try
            {
                rootFile = instance.getRepositoryFileById( workspace, view.getPath(  ), getJcrUser( request ) );
            }
            catch ( DataAccessResourceFailureException e )
            {
                AppLogService.debug( "Cannot access workspace " + workspace.getName(  ) + " (" +
                    e.getLocalizedMessage(  ) + ")" );
            }
            catch ( NullPointerException e )
            {
                AppLogService.debug( "Cannot access workspace " + workspace.getName(  ) + " (" +
                    e.getLocalizedMessage(  ) + ")" );
            }

            mapPathName.put( String.valueOf( view.getId(  ) ),
                ( rootFile == null ) ? "/" : instance.getPrettyAbsolutePath( workspace, rootFile, getJcrUser( request ) ) );
            finalAuthorizedViews.add( view );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKSPACES_LIST, availableWorkspaces );
        model.put( MARK_VIEW_LIST, finalAuthorizedViews );
        model.put( MARK_VIEW_PATHNAME_LIST, mapPathName );
        model.put( MARK_WORKGROUP_LIST, getWorkgroupMap(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_VIEWS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the workspace creation screen
     * @param request the request
     * @return html code
     */
    public String getAddWorkspace( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_WORKSPACE );

        HashMap<String, ReferenceList> model = new HashMap<String, ReferenceList>(  );
        model.put( MARK_JCR_LIST, getModifiableJcr(  ) );
        model.put( MARK_WORKGROUP_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADD_WORKSPACE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Create a new workspace
     * @param request the request
     * @return the redirect url
     */
    public String doAddWorkspace( HttpServletRequest request )
    {
        String strWorkspaceName = request.getParameter( PARAMETER_WORKSPACE_NAME );
        String strWorkspaceLabel = request.getParameter( PARAMETER_WORKSPACE_LABEL );
        String strJcrType = request.getParameter( PARAMETER_JCR_TYPE );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strUser = request.getParameter( PARAMETER_USER );
        String strPassword = request.getParameter( PARAMETER_PASSWORD );

        String strValidation = validateWorkspaceInput( strWorkspaceName, strWorkspaceLabel, strJcrType, strWorkgroup,
                strUser, strPassword, request );

        if ( strValidation != null )
        {
            return strValidation;
        }

        try
        {
            AdminJcrService.getInstance(  )
                           .createWorkspace( strJcrType, strWorkspaceName, strWorkspaceLabel, strUser, strWorkgroup,
                strPassword );
        }
        catch ( JcrExistingRepositoryException e )
        {
            String[] messageArgs = { (String) e.getObjectCause(  ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_EXISTING_WORKSPACE, messageArgs,
                AdminMessage.TYPE_ERROR );
        }

        return JSP_MANAGE_WORKSPACES;
    }

    /**
     * Get the workspace creation screen
     * @param request the request
     * @return html code
     */
    public String getAddView( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_VIEW );

        Collection<AdminWorkspace> availableWorkspaces = AdminJcrHome.getInstance(  ).findAllWorkspaces( getPlugin(  ) );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKSPACES_LIST,
            AdminWorkgroupService.getAuthorizedCollection( availableWorkspaces, getUser(  ) ) );
        model.put( MARK_WORKGROUP_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADD_VIEW, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Create a new workspace
     * @param request the request
     * @return the redirect url
     */
    public String doAddView( HttpServletRequest request )
    {
        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );
        String strViewName = request.getParameter( PARAMETER_VIEW_NAME );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strPath = request.getParameter( PARAMETER_PATH );

        String validationResult = validateViewInput( strWorkspaceId, strViewName, strWorkgroup, request );

        if ( validationResult != null )
        {
            return validationResult;
        }

        AdminView adminView = new AdminView(  );
        adminView.setName( strViewName );
        adminView.setWorkgroup( strWorkgroup );
        adminView.setWorkspaceId( Integer.parseInt( strWorkspaceId ) );
        adminView.setPath( strPath );

        AdminJcrHome.getInstance(  ).createView( adminView, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_MODIFY_VIEW );
        url.addParameter( PARAMETER_VIEW_ID, adminView.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Get the view modification screen
     * @param request the request
     * @return html code
     */
    public String getModifyViewRoles( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_VIEW );

        AdminView adminView = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );

        ReferenceList allRoles = RoleHome.getRolesList(  );
        ReferenceList readRoles = RoleHome.getRolesList(  );
        ReferenceList writeRoles = RoleHome.getRolesList(  );
        ReferenceList deleteRoles = RoleHome.getRolesList(  );
        readRoles.checkItems( AdminJcrHome.getInstance(  )
                                          .getAuthorizedRoles( adminView, IWorkspace.READ_ACCESS, getPlugin(  ) ) );
        writeRoles.checkItems( AdminJcrHome.getInstance(  )
                                           .getAuthorizedRoles( adminView, IWorkspace.WRITE_ACCESS, getPlugin(  ) ) );
        deleteRoles.checkItems( AdminJcrHome.getInstance(  )
                                            .getAuthorizedRoles( adminView, IWorkspace.REMOVE_ACCESS, getPlugin(  ) ) );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_VIEW, adminView );
        model.put( MARK_VIEW_READ_ROLES, readRoles );
        model.put( MARK_VIEW_WRITE_ROLES, writeRoles );
        model.put( MARK_VIEW_REMOVE_ROLES, deleteRoles );
        model.put( MARK_AVAILABLE_ROLES, allRoles );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_VIEW_ROLES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Modify roles of a view
     * @param request the request
     * @return the redirect url
     */
    public String doModifyViewRoles( HttpServletRequest request )
    {
        final String[] readRoles = request.getParameterValues( PARAMETER_READ_ROLES );
        final String[] writeRoles = request.getParameterValues( PARAMETER_WRITE_ROLES );
        final String[] removeRoles = request.getParameterValues( PARAMETER_REMOVE_ROLES );

        AdminView adminView = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );
        AdminJcrHome.getInstance(  ).updateRoles( adminView, IWorkspace.READ_ACCESS, readRoles, getPlugin(  ) );
        AdminJcrHome.getInstance(  ).updateRoles( adminView, IWorkspace.WRITE_ACCESS, writeRoles, getPlugin(  ) );
        AdminJcrHome.getInstance(  ).updateRoles( adminView, IWorkspace.REMOVE_ACCESS, removeRoles, getPlugin(  ) );

        return JSP_MANAGE_VIEWS;
    }

    /**
     * Get the workspace creation screen
     * @param request the request
     * @return html code
     */
    public String getModifyView( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_VIEW );

        AdminView adminView = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );

        Collection<AdminWorkspace> availableWorkspaces = AdminJcrHome.getInstance(  ).findAllWorkspaces( getPlugin(  ) );
        ReferenceList availableList = ReferenceList.convert( AdminWorkgroupService.getAuthorizedCollection( 
                    availableWorkspaces, getUser(  ) ), "id", "label", true );
        AdminWorkspace adminWorkspace = AdminJcrHome.getInstance(  )
                                                    .findWorkspaceById( adminView.getWorkspaceId(  ), getPlugin(  ) );

        String strPrettyPath = "";

        if ( adminView.getPath(  ) == null )
        {
            strPrettyPath = "/";
        }
        else
        {
            IRepositoryFile rootFile = RepositoryFileHome.getInstance(  )
                                                         .getRepositoryFileById( adminWorkspace, adminView.getPath(  ),
                    getJcrUser( request ) );
            strPrettyPath = RepositoryFileHome.getInstance(  )
                                              .getPrettyAbsolutePath( adminWorkspace, rootFile, getJcrUser( request ) );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKSPACES_LIST, availableList );
        model.put( MARK_VIEW, adminView );
        model.put( MARK_WORKGROUP_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );
        model.put( MARK_PRETTY_PATH_VIEW, strPrettyPath );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_VIEW, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Create a new workspace
     * @param request the request
     * @return the redirect url
     */
    public String doModifyView( HttpServletRequest request )
    {
        String strViewId = request.getParameter( PARAMETER_VIEW_ID );
        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );
        String strViewName = request.getParameter( PARAMETER_VIEW_NAME );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strPath = request.getParameter( PARAMETER_PATH );

        String validationResult = validateViewInput( strWorkspaceId, strViewName, strWorkgroup, request );

        if ( validationResult != null )
        {
            return validationResult;
        }

        AdminView adminView = new AdminView(  );
        adminView.setId( Integer.parseInt( strViewId ) );
        adminView.setName( strViewName );
        adminView.setWorkgroup( strWorkgroup );
        adminView.setWorkspaceId( Integer.parseInt( strWorkspaceId ) );
        adminView.setPath( strPath );

        AdminJcrHome.getInstance(  ).modifyView( adminView, getPlugin(  ) );

        return JSP_MANAGE_VIEWS;
    }

    /**
     * Get the workspace creation screen
     * @param request the request
     * @return html code
     */
    public String getModifyWorkspace( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_WORKSPACE );

        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_JCR_LIST, getModifiableJcr(  ) );
        model.put( MARK_WORKGROUP_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );
        model.put( MARK_WORKSPACE,
            AdminJcrHome.getInstance(  ).findWorkspaceById( Integer.parseInt( strWorkspaceId ), getPlugin(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_WORKSPACE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Create a new workspace
     * @param request the request
     * @return the redirect url
     */
    public String doModifyWorkspace( HttpServletRequest request )
    {
        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );
        String strWorkspaceName = request.getParameter( PARAMETER_WORKSPACE_NAME );
        String strWorkspaceLabel = request.getParameter( PARAMETER_WORKSPACE_LABEL );
        String strJcrType = request.getParameter( PARAMETER_JCR_TYPE );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strUser = request.getParameter( PARAMETER_USER );
        String strPassword = request.getParameter( PARAMETER_PASSWORD );

        String strValidation = validateWorkspaceInput( strWorkspaceName, strWorkspaceLabel, strJcrType, strWorkgroup,
                strUser, strPassword, request );

        if ( strValidation != null )
        {
            return strValidation;
        }

        AdminWorkspace workspace = new AdminWorkspace(  );
        workspace.setId( Integer.parseInt( strWorkspaceId ) );
        workspace.setJcrType( strJcrType );
        workspace.setName( strWorkspaceName );
        workspace.setWorkgroup( strWorkgroup );
        workspace.setLabel( strWorkspaceLabel );
        workspace.setUser( strUser );
        workspace.setPassword( strPassword );
        AdminJcrHome.getInstance(  ).modifyWorkspace( workspace, getPlugin(  ) );

        return JSP_MANAGE_WORKSPACES;
    }

    /**
     * View deletion confirmation
     * @param request the request
     * @return an url
     */
    public String deleteView( HttpServletRequest request )
    {
        AdminView view = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );
        String[] messageArgs = { view.getName(  ) };
        UrlItem url = new UrlItem( PATH_JSP + JSP_DELETE_VIEW );
        url.addParameter( PARAMETER_VIEW_ID, view.getId(  ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_VIEW, messageArgs, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Deletes a view after having done some verification
     * @param request the request
     * @return the url
     */
    public String doDeleteView( HttpServletRequest request )
    {
        AdminView view = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );

        if ( !AdminWorkgroupService.isAuthorized( view, getUser(  ) ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        if ( ( (Jsr170PortletHome) Jsr170PortletHome.getInstance(  ) ).existsPortletWithViewId( view.getId(  ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EXISTING_PORTLETS, AdminMessage.TYPE_STOP );
        }

        AdminJcrHome.getInstance(  ).deleteView( view.getId(  ), getPlugin(  ) );

        return JSP_MANAGE_VIEWS;
    }

    /**
     * Workspace deletion confirmation
     * @param request the request
     * @return an url
     */
    public String deleteWorkspace( HttpServletRequest request )
    {
        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );
        AdminWorkspace workspace = AdminJcrHome.getInstance(  )
                                               .findWorkspaceById( Integer.parseInt( strWorkspaceId ), getPlugin(  ) );
        String[] messageArgs = { workspace.getLabel(  ) };
        UrlItem url = new UrlItem( PATH_JSP + JSP_DELETE_WORKSPACE );
        url.addParameter( PARAMETER_WORKSPACE_ID, strWorkspaceId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_WORKSPACE, messageArgs,
            url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * @param request the request
     * @return the url
     */
    public String doDeleteWorkspace( HttpServletRequest request )
    {
        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );
        int nWorkspaceId = Integer.parseInt( strWorkspaceId );

        try
        {
            AdminJcrService.getInstance(  ).removeWorkspace( nWorkspaceId, getUser(  ) );
        }
        catch ( JcrUserAccessDeniedException e )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }
        catch ( JcrExistingViews e )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EXISTING_VIEWS, AdminMessage.TYPE_STOP );
        }

        return JSP_MANAGE_WORKSPACES;
    }

    /**
     * Control view input screen
     * @param strWorkspaceId workspace id
     * @param strViewName view name
     * @param strWorkgroup workgroup
     * @param request the request
     * @return url to message confirmation
     */
    private String validateViewInput( String strWorkspaceId, String strViewName, String strWorkgroup,
        HttpServletRequest request )
    {
        if ( ( strWorkspaceId == null ) || "".equals( strWorkspaceId ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strViewName == null ) || "".equals( strViewName ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strWorkgroup == null ) || "".equals( strWorkgroup ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * @param strWorkspaceName the workspace name
     * @param strWorkspaceLabel the workspace description
     * @param strJcrType the jcr type
     * @param strWorkgroup the allowed workgroup
     * @param strPassword the password
     * @param strUser the user
     * @param request the request
     * @return the redirect url
     */
    private String validateWorkspaceInput( String strWorkspaceName, String strWorkspaceLabel, String strJcrType,
        String strWorkgroup, String strUser, String strPassword, HttpServletRequest request )
    {
        if ( ( strWorkspaceName == null ) || "".equals( strWorkspaceName ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strWorkspaceLabel == null ) || "".equals( strWorkspaceLabel ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strWorkgroup == null ) || "".equals( strWorkgroup ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strJcrType == null ) || "".equals( strJcrType ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strUser == null ) || "".equals( strUser ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strPassword == null ) || "".equals( strPassword ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     *
     * @return a referenceList of modifiable workspaces
     */
    private ReferenceList getModifiableJcr(  )
    {
        ReferenceList jcrList = new ReferenceList(  );

        for ( ReferenceItem jcr : RepositoryFileHome.getInstance(  ).getAvailableJcr(  ) )
        {
            if ( RepositoryFileHome.getInstance(  ).canCreateWorkspace( jcr.getCode(  ) ) )
            {
                jcrList.add( jcr );
            }
        }

        return jcrList;
    }

    /**
     * Display page of jcr browser
     * @param request the request
     * @return html code
     */
    public String getSelectViewRoot( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_VIEW );

        AdminView view = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );
        AdminWorkspace workspace = AdminJcrHome.getInstance(  ).findWorkspaceById( view.getWorkspaceId(  ),
                getPlugin(  ) );
        Collection<IRepositoryFile> files = RepositoryFileHome.getInstance(  )
                                                              .getRepositoryFileList( workspace, getJcrUser( request ) );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FILE_LIST, files );
        model.put( MARK_VIEW, view );
        model.put( MARK_WORKSPACE, workspace );
        model.put( MARK_WORKSPACE_BROWSER, getWorkspaceBrowser( request, workspace ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECT_VIEW_ROOT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * get the HTML code to display a workspace browser.
     *
     * @param request The HTTP request
     * @param workspace the workspace being browsed
     * @return The HTML form
     */
    private String getWorkspaceBrowser( HttpServletRequest request, AdminWorkspace workspace )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        String strIdCurrentFile = request.getParameter( PARAMETER_FILE_ID );
        IRepositoryFile file;
        List<IRepositoryFile> fileList;
        String strIdFile;
        boolean bGoUp = true;

        RepositoryFileHome instance = RepositoryFileHome.getInstance(  );

        // if current space doesn't exists then set it up
        if ( ( strIdCurrentFile == null ) || "-1".equals( strIdCurrentFile ) )
        {
            strIdFile = instance.getRepositoryFile( workspace, "/", getJcrUser( request ) ).getResourceId(  );
        }
        else
        {
            strIdFile = strIdCurrentFile;
        }

        // set space list
        if ( strIdFile == null )
        {
            file = instance.getRepositoryFile( workspace, "/", getJcrUser( request ) );
        }
        else
        {
            file = instance.getRepositoryFileById( workspace, strIdFile, getJcrUser( request ) );
        }

        if ( file.getAbsolutePath(  ) != null )
        {
            fileList = instance.getRepositoryFileList( workspace, file.getAbsolutePath(  ), getJcrUser( request ) );
        }
        else
        {
            fileList = instance.getRepositoryFileList( workspace, getJcrUser( request ) );
        }

        model.put( MARK_GO_UP, bGoUp );
        model.put( MARK_FILE, file );
        model.put( MARK_FILE_LIST, fileList );
        model.put( MARK_ACTION, request.getRequestURI(  ) );
        // TODO set the right parent id
        model.put( MARK_PARENT_ID, "-1" );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_WORKSPACE_BROWSER, getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Update default root directory
     * @param request the request
     * @return the url
     */
    public String doSelectViewRoot( HttpServletRequest request )
    {
        String strFileId = request.getParameter( PARAMETER_BROWSER_SELECTED_FILE );

        AdminView view = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );

        if ( !AdminWorkgroupService.isAuthorized( view, getUser(  ) ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        view.setPath( strFileId );

        AdminJcrHome.getInstance(  ).modifyView( view, getPlugin(  ) );

        return JSP_MANAGE_VIEWS;
    }

    /**
     * @return a map of available workgroups
     */
    private Map<String, String> getWorkgroupMap(  )
    {
        Map<String, String> workgroupMap = new HashMap<String, String>(  );

        for ( ReferenceItem item : AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) )
        {
            workgroupMap.put( item.getCode(  ), item.getName(  ) );
        }

        return workgroupMap;
    }

    /**
     * Makes a JsrUser from a LuteceUser
     * @param request the request
     * @return a JsrUser
     */
    private JsrUser getJcrUser( HttpServletRequest request )
    {
        return new JsrUser( getUser(  ) );
    }

    /**
     * Displays the list of locks in a given view
     * @param request the request
     * @return the html code
     */
    public String getManageLocks( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_LOCK );

        AdminView view = JcrJspBeanUtils.getAdminViewFromRequest( request, getPlugin(  ) );
        AdminWorkspace workspace = AdminJcrHome.getInstance(  ).findWorkspaceById( view.getWorkspaceId(  ),
                getPlugin(  ) );
        Map<String, JcrLock> locks = RepositoryFileHome.getInstance(  ).getLocks( workspace, view, getJcrUser( request ) );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LOCK_LIST, locks );
        model.put( MARK_VIEW, view );
        model.put( MARK_WORKSPACE, workspace );
        model.put( MARK_WORKSPACE_BROWSER, getWorkspaceBrowser( request, workspace ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_LOCKS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Displays confirmation screens for lock deletion
     * @param request the request
     * @return the sitemessage url
     */
    public String getConfirmDeleteLocks( HttpServletRequest request )
    {
        String strLockId = request.getParameter( PARAMETER_LOCK_ID );
        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );
        String strViewId = request.getParameter( PARAMETER_VIEW_ID );
        String strFileId = request.getParameter( PARAMETER_FILE_ID );
        String[] messageArgs = {  };
        UrlItem url = new UrlItem( PATH_JSP + JSP_DELETE_LOCK );
        url.addParameter( PARAMETER_WORKSPACE_ID, strWorkspaceId );
        url.addParameter( PARAMETER_LOCK_ID, strLockId );
        url.addParameter( PARAMETER_FILE_ID, strFileId );
        url.addParameter( PARAMETER_VIEW_ID, strViewId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_LOCK, messageArgs, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Effectively delete a given lock
     * @param request the request
     * @return a redirect url
     */
    public String doDeleteLocks( HttpServletRequest request )
    {
        String strLockId = request.getParameter( PARAMETER_LOCK_ID );
        String strWorkspaceId = request.getParameter( PARAMETER_WORKSPACE_ID );
        String strViewId = request.getParameter( PARAMETER_VIEW_ID );
        String strFileId = request.getParameter( PARAMETER_FILE_ID );
        AdminWorkspace adminWorkspace = AdminJcrHome.getInstance(  )
                                                    .findWorkspaceById( Integer.parseInt( strWorkspaceId ),
                getPlugin(  ) );
        JsrUser jcrUser = getJcrUser( request );
        IRepositoryFile file = RepositoryFileHome.getInstance(  )
                                                 .getRepositoryFileById( adminWorkspace, strFileId, jcrUser );
        JcrLock criterias = new JcrLock(  );
        criterias.setIdLock( strLockId );
        criterias.setIdWorkspace( Integer.parseInt( strWorkspaceId ) );

        Collection<JcrLock> jcrLocks = JcrLockHome.getInstance(  ).getLocks( criterias );
        RepositoryFileHome.getInstance(  ).unLock( adminWorkspace, file, jcrUser, jcrLocks );

        UrlItem url = new UrlItem( JSP_MANAGE_LOCKS );
        url.addParameter( PARAMETER_VIEW_ID, strViewId );

        return url.getUrl(  );
    }
}
