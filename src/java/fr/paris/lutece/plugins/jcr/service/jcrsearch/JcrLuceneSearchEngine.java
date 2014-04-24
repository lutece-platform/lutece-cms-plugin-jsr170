/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.ChainedFilter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.LuceneDictionary;

import fr.paris.lutece.plugins.jcr.service.JcrPlugin;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * JcrLuceneSearchEngine
 */
public class JcrLuceneSearchEngine implements JcrSearchEngine
{
	/**
	 * {@inheritDoc}
	 */
	public List<SearchResult> getSearchResult( String strQuery, boolean bTitle,
			String strOperator, Date dateBegin, Date dateEnd, String strMIMEType,
			LuteceUser user )
	{
		ArrayList<JcrSearchItem> listResults = new ArrayList<JcrSearchItem>(  );
        
		
		try
		{
			Searcher searcher = new IndexSearcher( IndexationService.getDirectoryIndex(  ), true );
			
			BooleanQuery query = new BooleanQuery(  );
			Query queryPart;
			
            //Type (=jsr170)
			queryPart = new TermQuery( new Term( JcrSearchItem.FIELD_TYPE, JcrPlugin.PLUGIN_NAME ) );
			query.add( queryPart, BooleanClause.Occur.MUST );
			
			//Content
			if( StringUtils.isNotBlank( strQuery ) )
			{
				//subquery
				BooleanQuery queryContent = new BooleanQuery(  );
				Query queryContentPart;
				
				if( !bTitle )
				{
					QueryParser parser = new QueryParser( IndexationService.LUCENE_INDEX_VERSION, 
							JcrSearchItem.FIELD_CONTENTS, IndexationService.getAnalyser(  ) );
					
					if( strOperator.equalsIgnoreCase( "AND" ) )
					{
						parser.setDefaultOperator( Operator.AND );
					}
					
					queryContentPart = parser.parse( strQuery );
					
					queryContent.add( queryContentPart, BooleanClause.Occur.SHOULD );
				}
				
				
				//Search on title with both parsed query and "as is" query
				QueryParser parser = new QueryParser( IndexationService.LUCENE_INDEX_VERSION, 
						JcrSearchItem.FIELD_TITLE, IndexationService.getAnalyser(  ) );
				
				if( strOperator.equalsIgnoreCase( "AND" ) )
				{
					parser.setDefaultOperator( Operator.AND );
				}
				
				queryContentPart = parser.parse( strQuery );
				
				queryContent.add( queryContentPart, BooleanClause.Occur.SHOULD );
				
				queryContentPart = new TermQuery( new Term( JcrSearchItem.FIELD_TITLE, strQuery ) );
				queryContent.add( queryContentPart, BooleanClause.Occur.SHOULD );
				
				//add the subquery to the main one
				query.add( queryContent, BooleanClause.Occur.MUST );
			}
			
			//Dates
			String strDateBegin = null;
			String strDateEnd = null;
			
			if( dateBegin != null )
			{
				strDateBegin = DateTools.dateToString( dateBegin, DateTools.Resolution.DAY );
			}
			if( dateEnd != null )
			{
				strDateEnd = DateTools.dateToString( dateEnd, DateTools.Resolution.DAY );
			}
			
			if( strDateBegin != null || strDateEnd != null )
			{
				queryPart = new TermRangeQuery( JcrSearchItem.FIELD_DATE,
						strDateBegin, strDateEnd, true, false );
				query.add( queryPart, BooleanClause.Occur.MUST );
			}
			
			//MIME type
			if( StringUtils.isNotBlank( strMIMEType ) )
			{
				queryPart = new TermQuery( new Term( JcrSearchItem.FIELD_MIME_TYPE, strMIMEType ) );
				query.add( queryPart, BooleanClause.Occur.MUST );
			}
            
			Filter filterRole = getFilterRoles( user );
			
			TopDocs topDocs = searcher.search( query, filterRole, LuceneSearchEngine.MAX_RESPONSES );

            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( int i = 0; i < hits.length; i++ )
            {
            	int docId = hits[i].doc;
                Document document = searcher.doc( docId );
                JcrSearchItem si = new JcrSearchItem( document );
                listResults.add( si );
            }
			
			searcher.close(  );
		}
		catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
		
		return convertList( listResults );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<String> getMIMETypeList(  )
	{
		List<String> listMIMEType = new ArrayList<String>(  );
		
		try
		{
			IndexReader ir = IndexReader.open( IndexationService.getDirectoryIndex(  ), true );
			
			LuceneDictionary dico = new LuceneDictionary( ir, JcrSearchItem.FIELD_MIME_TYPE );
			
			for( Iterator it = dico.getWordsIterator(  ); it.hasNext(  ); )
			{
				listMIMEType.add( (String) it.next( ) );
			}
		}
		catch( Exception e )
		{
			AppLogService.error( e.getMessage(  ), e );
		}
		
		return listMIMEType;
	}
	
	/**
     * Generate the Lutece role filter if necessary
     * @param user the registered lutece user from who the query occured
     * @return The Filter by Lutece Role
     */
    private Filter getFilterRoles( LuteceUser user )
    {
        Filter filterRole = null;
        Filter[] filtersRole = null;
        boolean bFilterResult = false;

        if ( user != null && SecurityService.isAuthenticationEnable(  ) )
        {
            String[] userRoles = SecurityService.getInstance(  ).getRolesByUser( user );

            if ( userRoles != null )
            {
                filtersRole = new Filter[userRoles.length + 1];

                for ( int i = 0; i < userRoles.length; i++ )
                {
                    Query queryRole = new TermQuery( new Term( SearchItem.FIELD_ROLE, userRoles[i] ) );
                    filtersRole[i] = new CachingWrapperFilter( new QueryWrapperFilter( queryRole ) );
                }
            }
        }
        else if ( SecurityService.isAuthenticationEnable(  ) )
        {
        	bFilterResult = true;
            filtersRole = new Filter[1];
        }

        if ( bFilterResult )
        {
            Query queryRole = new TermQuery( new Term( SearchItem.FIELD_ROLE, Page.ROLE_NONE ) );
            filtersRole[filtersRole.length - 1] = new CachingWrapperFilter( new QueryWrapperFilter( queryRole ) );
            filterRole = new ChainedFilter( filtersRole, ChainedFilter.OR );
        }

        return filterRole;
    }
	
	/**
     * Convert the SearchItem list on SearchResult list
     * @param listSource The source list
     * @return The result list
     */
    private List<SearchResult> convertList( List<JcrSearchItem> listSource )
    {
        List<SearchResult> listDest = new ArrayList<SearchResult>(  );

        for ( JcrSearchItem item : listSource )
        {
            SearchResult result = new SearchResult(  );
            result.setId( item.getId(  ) );

            try
            {
                result.setDate( DateTools.stringToDate( item.getDate(  ) ) );
            }
            catch ( ParseException e )
            {
                AppLogService.error( "Bad Date Format for indexed item \"" + item.getTitle(  ) + "\" : " +
                    e.getMessage(  ) );
            }

            result.setUrl( item.getUrl(  ) );
            result.setTitle( item.getTitle(  ) );
            result.setType( item.getMIMEType(  ) );
            listDest.add( result );
        }

        return listDest;
    }
    
    
}
