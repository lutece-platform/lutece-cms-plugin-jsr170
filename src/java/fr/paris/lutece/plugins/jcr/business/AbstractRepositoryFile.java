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
package fr.paris.lutece.plugins.jcr.business;

import fr.paris.lutece.util.xml.XmlUtil;

import java.io.InputStream;

import java.text.DateFormat;

import java.util.Calendar;

import javax.jcr.Session;


public abstract class AbstractRepositoryFile implements IRepositoryFile
{
    public static final String PATH_SEPARATOR = "/";
    public static final String RESOURCE_TYPE = "JSR170_FILE";
    public static final String NODE_TYPE_FOLDER = "nt:folder";
    public static final String NODE_TYPE_FILE = "nt:file";
    public static final String NODE_TYPE_FILE_RESOURCE = "nt:resource";
    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    /**
     * the path in the repository, with the format /&lt;workspace&gt;/&lt;path_to_file&gt;/&lt;name_of_file&gt;
     */
    private String _strPath;
    private InputStream _streamContent;
    private String _strMimeType;
    private Session _sSession;
    private boolean _bIsFile;
    private boolean _bIsDirectory;
    private boolean _bExists;
    private String _strAbsolutePath;
    private Calendar _lastModified;
    private long _lLength;
    private String _resourceId;
    private String _strName;
    private String _strParentId;
    private boolean _bIsLocked;
    private String _strOwner;
    private boolean _bOwnsLock;

    /**
     * An empty constructor
     */
    public AbstractRepositoryFile(  )
    {
    }

    /**
     * @return the _bExists
     */
    public boolean exists(  )
    {
        return _bExists;
    }

    /**
     * @param exists the _bExists to set
     */
    public void setExists( boolean exists )
    {
        _bExists = exists;
    }

    /**
     * @return the _bIsDirectory
     */
    public boolean isDirectory(  )
    {
        return _bIsDirectory;
    }

    /**
     * @param isDirectory the _bIsDirectory to set
     */
    public void setDirectory( boolean isDirectory )
    {
        _bIsDirectory = isDirectory;
    }

    /**
     * @return the _bIsFile
     */
    public boolean isFile(  )
    {
        return _bIsFile;
    }

    /**
     * @param isFile the _bIsFile to set
     */
    public void setFile( boolean isFile )
    {
        _bIsFile = isFile;
    }

    /**
     * @return the _content
     */
    public InputStream getContent(  )
    {
        return _streamContent;
    }

    /**
     * @param content the _content to set
     */
    public void setContent( InputStream content )
    {
        this._streamContent = content;
    }

    /**
     * @return the session
     */
    public Session getSession(  )
    {
        return _sSession;
    }

    /**
     * @param session the _session to set
     */
    public void setSession( Session session )
    {
        this._sSession = session;
    }

    /**
     * @return the _strAbsolutePath
     */
    public String getAbsolutePath(  )
    {
        return _strAbsolutePath;
    }

    /**
     * @param absolutePath the _strAbsolutePath to set
     */
    public void setAbsolutePath( String absolutePath )
    {
        _strAbsolutePath = absolutePath;
    }

    /**
     * @return the _strMimeType
     */
    public String getMimeType(  )
    {
        if ( _strMimeType == null )
        {
            return DEFAULT_MIME_TYPE;
        }

        return _strMimeType;
    }

    /**
     * @param mimeType the _strMimeType to set
     */
    public void setMimeType( String mimeType )
    {
        _strMimeType = mimeType;
    }

    /**
     * @param path the _strPath to set
     */
    public void setPath( String path )
    {
        _strPath = path;
    }

    /**
     * @return the path
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFile#getPath()
     */
    public String getPath(  )
    {
        return _strPath;
    }

    /**
     * @return the name
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFile#getName()
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * @return the last modified date
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFile#lastModified()
     */
    public Calendar lastModified(  )
    {
        return _lastModified;
    }

    /**
     * @return the file size
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFile#length()
     */
    public long length(  )
    {
        return _lLength;
    }

    /**
     * @param length the file size
     */
    public void setLength( long length )
    {
        _lLength = length;
    }

    /**
     * @return the resource ID
     * @see fr.paris.lutece.portal.service.rbac.RBACResource#getResourceId()
     */
    public String getResourceId(  )
    {
        return _resourceId;
    }

    /**
     * @param strResourceId the resource ID
     */
    public void setResourceId( String strResourceId )
    {
        _resourceId = strResourceId;
    }

    /**
     * @return the resource type code
     * @see fr.paris.lutece.portal.service.rbac.RBACResource#getResourceTypeCode()
     */
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * @param o the object to compare
     * @return true has same path or same reference as o
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o )
    {
        if ( !( o instanceof IRepositoryFile ) )
        {
            return false;
        }

        IRepositoryFile otherFile = (IRepositoryFile) o;

        return ( ( otherFile.getPath(  ) == null ) && ( getPath(  ) == null ) ) ||
        ( ( otherFile.getPath(  ) != null ) && otherFile.getPath(  ).equals( getPath(  ) ) ) ||
        ( ( otherFile.getResourceId(  ) == null ) && ( getResourceId(  ) == null ) ) ||
        ( ( otherFile.getResourceId(  ) != null ) && otherFile.getResourceId(  ).equals( getResourceId(  ) ) );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(  )
    {
        return getPath(  ).hashCode(  );
    }

    /**
     * @return custom String representation
     * @see java.lang.Object#toString()
     */
    public String toString(  )
    {
        return getName(  ) + " : " + getAbsolutePath(  ) + " (" + ( isDirectory(  ) ? "d" : "" ) +
        ( isFile(  ) ? "f" : "" ) + ")" + "[ " + getContent(  ) + " ]";
    }

    /**
     * @return xml representation
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFile#getXml()
     */
    public String getXml(  )
    {
        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.beginElement( strXml, getElementName(  ) );
        strXml.append( getXmlContent(  ) );
        XmlUtil.endElement( strXml, getElementName(  ) );

        return strXml.toString(  );
    }

    protected abstract StringBuffer getXmlContent(  );

    /**
     * @param lastModified the date of last modification
     */
    public void setLastModified( Calendar lastModified )
    {
        _lastModified = lastModified;
    }

    /**
     * @param date the date to format
     * @return a formatted date
     */
    protected String getFormattedDate( Calendar date )
    {
        if ( date == null )
        {
            return "";
        }

        return DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).format( date.getTime(  ) );
    }

    /**
     * @param strName the name of the file
     * @see fr.paris.lutece.plugins.jcr.business.IRepositoryFile#setName(java.lang.String)
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    public String getParentId(  )
    {
        return _strParentId;
    }

    public void setParentId( String strParentId )
    {
        _strParentId = strParentId;
    }

    protected abstract String getElementName(  );

    public boolean isLocked(  )
    {
        return _bIsLocked;
    }

    public void setLock( boolean bIsLocked )
    {
        _bIsLocked = bIsLocked;
    }

    /**
     * @return the _strOwner
     */
    public String getOwner(  )
    {
        return _strOwner;
    }

    /**
     * @param strOwner the _strOwner to set
     */
    public void setOwner( String strOwner )
    {
        _strOwner = strOwner;
    }

    public boolean ownsLock(  )
    {
        return _bOwnsLock;
    }

    public void setOwnsLock( boolean bOwnsLock )
    {
        _bOwnsLock = bOwnsLock;
    }
}
