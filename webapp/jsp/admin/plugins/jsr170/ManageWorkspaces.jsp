<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="jsr170" scope="session" class="fr.paris.lutece.plugins.jcr.web.RepositoryFileJspBean" />

<% jsr170.init( request, jsr170.RIGHT_JSR170_MANAGEMENT ); %>
<%= jsr170.getManageWorkspaces( request ) %>

<%@ include file="../../AdminFooter.jsp" %>