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
package fr.paris.lutece.plugins.jcr.service.jcrsearch;

import org.apache.lucene.document.Document;

import fr.paris.lutece.portal.service.search.SearchItem;

/**
 * JcrSearchItem
 */
public class JcrSearchItem extends SearchItem
{
	public static final String FIELD_MIME_TYPE = "mime_type";
	
	private String _strMIMEType;
	
	/**
	 * Constructor
	 * @param document The Lucene Document
	 */
	public JcrSearchItem( Document document )
	{
		super( document );
		_strMIMEType = document.get( FIELD_MIME_TYPE );
	}
	
	/**
	 * Returns the MIME type of this item
	 * @return the MIME type
	 */
	public String getMIMEType(  )
	{
		return _strMIMEType;
	}
	
	/**
	 * Sets the MIME type of this item to the specified value
	 * @param MIMEType the MIME type to set
	 */
	public void setMIMEType( String MIMEType )
	{
		_strMIMEType = MIMEType;
	}

}
