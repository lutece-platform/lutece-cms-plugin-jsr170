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

import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.compact.CompactNodeTypeDefReader;
import org.apache.jackrabbit.core.nodetype.compact.ParseException;

import java.io.InputStreamReader;
import java.io.Reader;

import java.util.List;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;


public class JackrabbitRepositoryInitializer implements IRepositoryInitializer
{
    public static final String LUTECE_NAMESPACE = "lutece";
    public static final String LUTECE_NAMESPACE_URI = "http://lutece.paris.fr/lut/1.0";
    public static final String LUTECE_NODETYPE_FILENAME = "lutecefile.cnd";

    public void init( Workspace ws )
    {
        Reader fileReader;

        try
        {
            setLuteceNamespace( ws );
            fileReader = new InputStreamReader( getClass(  ).getClassLoader(  )
                                                    .getResourceAsStream( LUTECE_NODETYPE_FILENAME ) );

            // Create a CompactNodeTypeDefReader
            CompactNodeTypeDefReader cndReader = new CompactNodeTypeDefReader( fileReader, LUTECE_NODETYPE_FILENAME );

            // Get the List of NodeTypeDef objects
            List<NodeTypeDef> ntdList = cndReader.getNodeTypeDefs(  );

            // Get the NodeTypeManager from the Workspace.
            // Note that it must be cast from the generic JCR
            // NodeTypeManager to the
            // Jackrabbit-specific implementation.
            NodeTypeManagerImpl ntmgr = (NodeTypeManagerImpl) ws.getNodeTypeManager(  );

            // Acquire the NodeTypeRegistry
            NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry(  );

            // Loop through the prepared NodeTypeDefs
            for ( NodeTypeDef ntd : ntdList )
            {
                // ...and register it
                if ( !ntreg.isRegistered( ntd.getName(  ) ) )
                {
                    ntreg.registerNodeType( ntd );
                }
            }
        }
        catch ( RepositoryException e )
        {
            throw new AppException( e.getLocalizedMessage(  ), e );
        }
        catch ( InvalidNodeTypeDefException e )
        {
            throw new AppException( e.getLocalizedMessage(  ), e );
        }
        catch ( ParseException e )
        {
            throw new AppException( e.getLocalizedMessage(  ), e );
        }
    }

    private void setLuteceNamespace( Workspace ws )
    {
        // set lutece namespace registry
        NamespaceRegistry registry;

        try
        {
            registry = ws.getNamespaceRegistry(  );
            registry.registerNamespace( LUTECE_NAMESPACE, LUTECE_NAMESPACE_URI );
        }
        catch ( RepositoryException e )
        {
            // do nothing
            AppLogService.debug( e );
        }
    }
}
