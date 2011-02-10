
<%@page import="fr.paris.lutece.plugins.jcr.business.IRepositoryFile"%>
<jsp:useBean id="jsr170File" scope="request" class="fr.paris.lutece.plugins.jcr.web.portlet.Jsr170PortletJspBean" />
<jsp:useBean id="repositoryFile" scope="request" class="fr.paris.lutece.plugins.jcr.web.RepositoryFileJspBean" />


<%
				byte[] fileContent = jsr170File.getFileVersionContent( request );
				if ( fileContent != null )
				{
					IRepositoryFile file = jsr170File.getFile(request);
					//the header and also the names set by which user will be prompted to save
					response.setHeader( "Content-Disposition", "attachment;filename=\""+ file.getName() +"\"");
					response.setContentType( file.getMimeType( ) );

					response.getOutputStream(  ).write( fileContent );
				}
				else
				{
                    String strUrl = repositoryFile.getFileErrorUrl(request, jsr170File.FILE_NOT_FOUND );
                    response.sendRedirect(strUrl);
				}
	out.clear();
	out = pageContext.pushBody();
%>


