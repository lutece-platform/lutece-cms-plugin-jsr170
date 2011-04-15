/*
 * Copyright (c) 2002-2010, Mairie de Paris
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
package fr.paris.lutece.plugins.jcr.service.jcrsearch;

import java.util.Date;
import java.util.List;

import fr.paris.lutece.plugins.jcr.service.JcrPlugin;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * JcrSearchService
 */
public class JcrSearchService
{
	private static final String BEAN_SEARCH_ENGINE = "jsr170.jcrSearchEngine";
	
	private static JcrSearchService _singleton;
	
	//Private constructor
	private JcrSearchService(  )
	{
	}
	
	/**
	 * Gets the JcrSearchService instance
	 * @return the service instance
	 */
	public static JcrSearchService getInstance(  )
	{
		if( _singleton == null )
		{
			_singleton = new JcrSearchService(  );
		}
		
		return _singleton;
	}
	
	/**
	 * Returns search results
	 * @param strQuery the search query
	 * @param bTitle true if the query must search on the title field only
	 * @param strOperator <ul><li>"OR" to search on each word</li><li>"AND" to search on all words</li>/<ul>
	 * @param dateBegin the date after which results must have been edited (inclusive)
	 * @param dateEnd the date before which results must have been edited (exclusive)
	 * @param strMIMEType the MIME type to search on
	 * @param user the registered lutece user from who the query occurred
	 * @return the search result
	 */
	public List<SearchResult> getSearchResults( String strQuery, boolean bTitle,
			String strOperator, Date dateBegin, Date dateEnd, String strMIMEType,
			LuteceUser user )
	{
		JcrSearchEngine engine = 
			(JcrSearchEngine) SpringContextService.getPluginBean( 
					JcrPlugin.PLUGIN_NAME, BEAN_SEARCH_ENGINE );
		
		return engine.getSearchResult( strQuery, bTitle, strOperator, 
				dateBegin, dateEnd, strMIMEType, user );
	}
	
	/**
	 * Finds MIME types of all jsr170 indexed documents
	 * @return a list of MIME types
	 */
	public List<String> getMIMETypeList(  )
	{
		JcrSearchEngine engine = 
			(JcrSearchEngine) SpringContextService.getPluginBean( 
					JcrPlugin.PLUGIN_NAME, BEAN_SEARCH_ENGINE );
		
		return engine.getMIMETypeList(  );
	}
}
