<jsp:useBean id="repositoryFileJspBean" scope="session" class="fr.paris.lutece.plugins.jcr.web.portlet.Jsr170PortletJspBean" />


<%
//	repositoryFileJspBean.init(request, poll.RIGHT_MANAGE_POLL);
	response.sendRedirect( repositoryFileJspBean.uploadFile( request ) );
%>


