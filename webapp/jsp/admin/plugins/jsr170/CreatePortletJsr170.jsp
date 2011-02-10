<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="jsr170Portlet" scope="session" class="fr.paris.lutece.plugins.jcr.web.portlet.Jsr170PortletJspBean" />

<% jsr170Portlet.init( request, jsr170Portlet.RIGHT_MANAGE_ADMIN_SITE); %>
<%= jsr170Portlet.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>


