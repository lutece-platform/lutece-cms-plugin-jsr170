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

import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.resource.Resource;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 * An implementation of IRepositoryFile for JCR
 */
public class JcrRepositoryFileImpl extends AbstractRepositoryFile implements Resource, RBACResource
{
    // Xml Tags
    private static final String TAG_FILE = "file";
    private static final String TAG_FILE_NAME = "file-name";
    private static final String TAG_FILE_EXTENSION = "file-extension";
    private static final String TAG_FILE_SIZE = "file-size";
    private static final String TAG_FILE_DATE = "file-date";
    private static final String TAG_FILE_PATH = "file-path";
    private static final String TAG_DIRECTORY = "directory";
    private static final String TAG_DIRECTORY_NAME = "directory-name";
    private static final String TAG_DIRECTORY_DATE = "directory-date";
    private static final String TAG_DIRECTORY_PATH = "directory-path";
    private static final String TAG_FILE_ID = "file-id";
    private static final String TAG_FILE_LOCK = "file-lock";
    private static final String TAG_FILE_OWNS_LOCK = "file-owns-lock";

    protected String getElementName(  )
    {
        return isFile(  ) ? TAG_FILE : TAG_DIRECTORY;
    }

    protected StringBuffer getXmlContent(  )
    {
        StringBuffer strXml = new StringBuffer(  );

        if ( isFile(  ) )
        {
            XmlUtil.addElement( strXml, TAG_FILE_NAME, getName(  ) );
            XmlUtil.addElement( strXml, TAG_FILE_EXTENSION, getName(  ).replaceFirst( ".*\\.(...)", "$1" ) );
            XmlUtil.addElement( strXml, TAG_FILE_SIZE, "" + ( length(  ) / 1024 ) );
            XmlUtil.addElement( strXml, TAG_FILE_DATE, "" + getFormattedDate( lastModified(  ) ) );
            XmlUtil.addElement( strXml, TAG_FILE_PATH, getPath(  ) );
            XmlUtil.addElement( strXml, TAG_FILE_ID, getResourceId(  ) );
            XmlUtil.addElement( strXml, TAG_FILE_LOCK, "" + isLocked(  ) );
            XmlUtil.addElement( strXml, TAG_FILE_OWNS_LOCK, "" + ownsLock(  ) );
        }
        else
        {
            XmlUtil.addElement( strXml, TAG_DIRECTORY_NAME, getName(  ) );
            XmlUtil.addElement( strXml, TAG_DIRECTORY_DATE, "" + getFormattedDate( lastModified(  ) ) );
            XmlUtil.addElement( strXml, TAG_DIRECTORY_PATH, getAbsolutePath(  ) );
            XmlUtil.addElement( strXml, TAG_FILE_ID, getResourceId(  ) );
        }

        return strXml;
    }
}
