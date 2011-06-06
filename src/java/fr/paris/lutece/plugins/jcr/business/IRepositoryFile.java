/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import java.io.InputStream;

import java.util.Calendar;


/**
 * Interface for elements contained in a repository
 *
 */
public interface IRepositoryFile
{
    /**
     * Get the path of this file
     * @return the path
     */
    String getPath(  );

    /**
     * Get the absolute path
     * @return the absolute path
     */
    String getAbsolutePath(  );

    /**
     * Get the name of this file
     * @return the name
     */
    String getName(  );

    /**
     * Set the name of this file
     * @param strName the name
     */
    void setName( String strName );

    /**
     * Get the content of the file, if it is a file
     * @return an inputstream with the file content
     */
    InputStream getContent(  );

    /**
     * Test the type of this file
     * @return true if it is a directory, false otherwise
     */
    boolean isDirectory(  );

    /**
     * Test the type of this file
     * @return true if it is a directory, false otherwise
     */
    boolean isFile(  );

    /**
     * Get the date of last modification
     * @return a date
     */
    Calendar lastModified(  );

    /**
     * Get the length of the file content
     * @return length in bytes
     */
    long length(  );

    /**
     * Return the resource id
     * @return an id
     */
    String getResourceId(  );

    /**
     * Return the resource type
     * @return a resource type
     */
    String getResourceTypeCode(  );

    /**
     * Test the existence of the file
     * @return true if a file exists in the repository
     */
    boolean exists(  );

    /**
     * Get the Mime type associated with the file content
     * @return the mime type
     */
    String getMimeType(  );

    /**
     * Get the xml representation of this file
     * @return an XML string
     */
    String getXml(  );

    /**
     * Get the parent id of this file
     * @return the parent id or null if it has no parents
     */
    String getParentId(  );

    /**
     * Set the content of the file
     * @param content an InputStream
     */
    void setContent( InputStream content );

    /**
     * Return true if the current file is locked
     * @return true if the node is locked
     */
    boolean isLocked(  );

    /**
     * Modify lock status
     * @param locked true to set a lock, false otherwise
     */
    void setLock( boolean locked );

    /**
     * Returns if a user owns a lock
     *
     * @return true if current user owns a lock on this file
     */
    boolean ownsLock(  );
}
