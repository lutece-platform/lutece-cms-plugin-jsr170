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

import java.io.File;

import java.util.List;


/**
 * Interface for Repository DAO
 */
public interface IRepositoryFileDAO extends IJsr170DAO
{
    /**
     * Create a file in workspace strWorkspace at absolute path strPath, with
     * file content.
     * @param strWorkspace the name of the workspace
     * @param strPath the absolute path of the file
     * @param file the file content
     */
    void create( String strWorkspace, String strPath, File file, String strMimeType );

    /**
     * Create a directory in workspace strWorkspace at absolute path strPath
     * @param strWorkspace the name of the workspace
     * @param strPath the absolute path of the directory
     */
    void create( String strWorkspace, String strPath );

    /**
     * Submit file changes in repository
     * @param strWorkspace the workspace name
     * @param file the file containing the modifications
     */
    void store( String strWorkspace, IRepositoryFile file );

    /**
     * Remove a file from repository
     * @param strWorkspace the workspace name
     * @param strPath the path of the file to remove
     */
    void delete( String strWorkspace, String strPath );

    /**
     * Find a file by its id
     * @param strWorkspace the workspace name
     * @param id the id
     * @return the file associated with the id or null if it doesn't exist
     */
    IRepositoryFile findById( String strWorkspace, String id );

    /**
     * Find a file by its id
     * @param strWorkspace the workspace name
     * @param id the id
     * @param version the version name to retrieve
     * @return the file associated with the id or null if it doesn't exist
     */
    IRepositoryFile findById( String strWorkspace, String id, String version );

    /**
     * Find a file by its path
     * @param strWorkspace the workspace name
     * @param strPath the absolute path
     * @return the file associated with the path
     */
    IRepositoryFile findByPath( String strWorkspace, String strPath );

    /**
     * List all files in a directory
     * @param strWorkspace the workspace name
     * @param strPath the absolute path
     * @return a list of all files
     */
    List<IRepositoryFile> listFiles( String strWorkspace, String strPath );

    /**
     * Set versionable feature for the specified node
     * @param strWorkspace the workspace name
     * @param strNodeId the node id
     * @param isVersionnable true to make the node versionable
     */
    void setVersionnable( String strWorkspace, String strNodeId, boolean isVersionnable );

    /**
     * Get all versions for this file
     * @param strWorkspace the workspace name
     * @param strNodeId the node id
     * @return the versions of the node or an emtpy list if this node isn't versionable
     */
    List<IRepositoryFile> getHistory( String strWorkspace, String strNodeId );

    String setLock( String strWorkspace, IRepositoryFile file, String strUsername );

    String removeLock( String strWorkspace, IRepositoryFile file );

    List<IRepositoryFile> getPathToFile( String strWorkspace, final String strFileId );
}
