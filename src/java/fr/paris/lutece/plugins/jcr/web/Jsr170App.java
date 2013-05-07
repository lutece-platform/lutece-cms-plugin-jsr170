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

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;

import fr.paris.lutece.plugins.jcr.service.jcrsearch.JcrSearchService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.http.SecurityUtil;

/**
 * This class implements the Jsr170 XPage
 */
public class Jsr170App implements XPageApplication
{
	//Public constants
	
	//Markers
	private static final String MARK_DATE_BEGIN = "date_begin";
	private static final String MARK_DATE_END = "date_end";
	private static final String MARK_FULL_URL = "full_url";
	private static final String MARK_LOCALE = "locale";
	private static final String MARK_MIME_TYPE = "mime_type";
	private static final String MARK_MIME_TYPE_LIST = "mime_type_list";
	private static final String MARK_OPERATOR = "operator";
	private static final String MARK_QUERY = "query";
	private static final String MARK_TITLE_ONLY = "title_only";
	private static final String MARK_RESULT_LIST = "result_list";
	
	//Messages
	private static final String MESSAGE_INVALID_SEARCH_TERMS = "jsr170.message.invalidSearchTerms";
	private static final String MESSAGE_JSR170_PATH_LABEL = "jsr170.xpage.pagePathLabel";
	private static final String MESSAGE_MANDATORY_FIELD = "jsr170.message.mandatoryField";
	private static final String MESSAGE_SEARCH_DATE_VALIDITY = "jsr170.message.dateValidity";
	private static final String MESSAGE_SEARCH_OPERATOR_VALIDITY = "jsr170.message.operatorValidity";
	private static final String MESSAGE_SEARCH_PAGE_TITLE = "jsr170.search.title";
	
	//Parameters
	private static final String PARAMETER_DATE_BEGIN = "date_begin";
	private static final String PARAMETER_DATE_END = "date_end";
	private static final String PARAMETER_MIME_TYPE = "mime_type";
	private static final String PARAMETER_OPERATOR = "operator";
	private static final String PARAMETER_QUERY = "query";
	private static final String PARAMETER_TITLE_ONLY = "title_only";	
	//Templates
	private static final String TEMPLATE_SEARCH_PAGE = "skin/plugins/jsr170/search.html";

	//Others
	private static final String CHECKED = "checked=\"true\"";
	private static final String MIME_TYPE_NONE = "none";
	private static final String OPERATOR_AND = "AND";
	private static final String OPERATOR_OR = "OR";
	
    /**
     * Returns the Jsr170 XPage content depending on the request parameters and the current mode.
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The plugin.
     * @return The page content.
     * @throws SiteMessageException The Site message exception
     */
	public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
			throws UserNotSignedException, SiteMessageException
	{
		XPage page = new XPage(  );
		
		page.setContent( getSearchPage( request, plugin ) );
		page.setPathLabel( I18nService.getLocalizedString( MESSAGE_JSR170_PATH_LABEL, request.getLocale(  ) ) );
		page.setTitle( I18nService.getLocalizedString( MESSAGE_SEARCH_PAGE_TITLE, request.getLocale(  ) ) );
		
		return page;
	}
	
	/**
	 * Returns the html code of the search page
	 * @param request the http request
	 * @param plugin the plugin
	 * @return the html page
	 * @throws SiteMessageException a exception that triggers a site message
	 */
	public String getSearchPage( HttpServletRequest request, Plugin plugin )
		throws SiteMessageException
	{
		String strQuery = request.getParameter( PARAMETER_QUERY );
		
		HashMap<String, Object> model = new HashMap<String, Object>(  );
		
		if( StringUtils.isNotBlank( strQuery ) )
		{
			Date dateBegin = null;
			Date dateEnd = null;
			LuteceUser user = null;
			
			boolean bTitle = request.getParameter( PARAMETER_TITLE_ONLY ) != null;
			String strOperator = request.getParameter( PARAMETER_OPERATOR );
			String strDateBegin = request.getParameter( PARAMETER_DATE_BEGIN );
			String strDateEnd = request.getParameter( PARAMETER_DATE_END );
			String strMIMEType = request.getParameter( PARAMETER_MIME_TYPE );
			
			
			//Mandatory fields
			if( StringUtils.isBlank( strOperator ) || StringUtils.isBlank( strMIMEType ) )
			{
				SiteMessageService.setMessage( request, MESSAGE_MANDATORY_FIELD, SiteMessage.TYPE_STOP );
			}
			
			//Safety checks
			if( StringUtils.isNotBlank( strDateBegin ) )
			{
				dateBegin = DateUtil.formatDate( strDateBegin, request.getLocale(  ) );
				if( dateBegin == null )
				{
					SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_VALIDITY, SiteMessage.TYPE_STOP );
				}
			}
			
			if( StringUtils.isNotBlank( strDateEnd ) )
			{
				dateEnd = DateUtil.formatDate( strDateEnd, request.getLocale(  ) );
				if( dateEnd == null )
				{
					SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_VALIDITY, SiteMessage.TYPE_STOP );
				}
			}
			
			if( !strOperator.equalsIgnoreCase( OPERATOR_AND ) && !strOperator.equalsIgnoreCase( OPERATOR_OR ) )
			{
				SiteMessageService.setMessage( request, MESSAGE_SEARCH_OPERATOR_VALIDITY, SiteMessage.TYPE_STOP );
			}
			
			//Check XSS characters
	        if ( SecurityUtil.containsXssCharacters( request, strQuery ) )
	        {
	        	SiteMessageService.setMessage( request, MESSAGE_INVALID_SEARCH_TERMS, SiteMessage.TYPE_STOP );
	        }
			
			//User
			if( SecurityService.isAuthenticationEnable(  ) )
			{
				user = SecurityService.getInstance(  ).getRegisteredUser( request );
			}
			
			model.put( MARK_RESULT_LIST, 
					JcrSearchService.getInstance(  ).getSearchResults( strQuery,
							bTitle, strOperator, dateBegin, dateEnd, 
							strMIMEType.equals( MIME_TYPE_NONE ) ? null : strMIMEType, 
							user) );
			
			//re-populate search parameters
			model.put( MARK_QUERY, strQuery );
			model.put( MARK_TITLE_ONLY, bTitle ? CHECKED : null );
			model.put( MARK_OPERATOR, strOperator );
			model.put( MARK_DATE_BEGIN, strDateBegin );
			model.put( MARK_DATE_END, strDateEnd );
			model.put( MARK_MIME_TYPE, strMIMEType );
			
		}
		
		model.put( MARK_MIME_TYPE_LIST, JcrSearchService.getInstance(  ).getMIMETypeList(  ) );
		model.put( MARK_LOCALE, request.getLocale(  ) );
		model.put( MARK_FULL_URL, request.getRequestURL(  ) );
		
		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEARCH_PAGE, request.getLocale(  ), model );
		
		return template.getHtml(  );
	}

}
