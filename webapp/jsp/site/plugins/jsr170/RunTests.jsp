<jsp:useBean id="testJspBean" scope="session" class="fr.paris.lutece.plugins.jcr.web.TestRepositoryFileJspBean" />


<%
//	repositoryFileJspBean.init(request, poll.RIGHT_MANAGE_POLL);%>
<%= testJspBean.runAllTests( request )  %>


