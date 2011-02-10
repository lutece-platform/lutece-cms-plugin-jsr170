<jsp:useBean id="jsr170PortletJspBean" scope="session" class="fr.paris.lutece.plugins.jcr.web.portlet.Jsr170PortletJspBean" />


<%
	response.sendRedirect( jsr170PortletJspBean.doLockFile( request ) );
%>


