<jsp:useBean id="jsr170PortletJspBean" scope="session" class="fr.paris.lutece.plugins.jcr.web.portlet.Jsr170PortletJspBean" />


<%
//	repositoryFileJspBean.init(request, poll.RIGHT_MANAGE_POLL);
	response.sendRedirect( jsr170PortletJspBean.deleteFile( request ) );
%>


