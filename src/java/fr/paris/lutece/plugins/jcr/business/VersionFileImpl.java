/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.jcr.business;

import fr.paris.lutece.util.xml.XmlUtil;


/**
 * Implementation of file version
 */
public class VersionFileImpl extends AbstractRepositoryFile
{
    private static final String TAG_VERSION = "version";
    private static final String TAG_VERSION_NAME = "version-name";
    private static final String TAG_VERSION_DATE = "version-date";
    private static final String TAG_VERSION_ID = "version-id";

    /**
     * @return the tag name of this element type
     * @see fr.paris.lutece.plugins.jcr.business.AbstractRepositoryFile#getElementName()
     */
    @Override
    protected String getElementName(  )
    {
        return TAG_VERSION;
    }

    /**
     * @return xml representation of this version
     * @see fr.paris.lutece.plugins.jcr.business.AbstractRepositoryFile#getXmlContent()
     */
    @Override
    protected StringBuffer getXmlContent(  )
    {
        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.addElement( strXml, TAG_VERSION_ID, getResourceId(  ) );
        XmlUtil.addElement( strXml, TAG_VERSION_NAME, getName(  ) );
        XmlUtil.addElement( strXml, TAG_VERSION_DATE, "" + getFormattedDate( lastModified(  ) ) );

        return strXml;
    }
}
