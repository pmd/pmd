/*
 * Created on 29 mai 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.pmd.eclipse.properties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import net.sourceforge.pmd.eclipse.dao.DAOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * This class is the Data Access Object that manages the persistances of the
 * ProjectProperies Data Object
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2005/05/31 20:44:40  phherlin
 * Continuing refactoring
 *
 * Revision 1.1  2005/05/31 20:33:01  phherlin
 * Continuing refactoring
 *
 *
 */
public class ProjectPropertiesDAOCastor implements ProjectPropertiesDAO {
    private static final String PROPERTIES_FILE = ".pmd";
    private static final String PROPERTIES_MAPPING = "/net/sourceforge/pmd/eclipse/properties/mapping.xml";
    
    /**
     * Load a project properties
     * @param project a project
     */
    public ProjectPropertiesTO readProjectProperties(final IProject project) throws DAOException {
        ProjectPropertiesTO projectProperties = null;
        
        try {
            final Mapping mapping = new Mapping();
            final URL mappingSpecUrl = this.getClass().getResource(PROPERTIES_MAPPING);
            mapping.loadMapping(mappingSpecUrl);

            final IFile propertiesFile = project.getFile(PROPERTIES_FILE);
            if (propertiesFile.exists() && propertiesFile.isAccessible()) {
                final Reader reader = new InputStreamReader(propertiesFile.getContents());
                final Unmarshaller unmarshaller = new Unmarshaller(mapping);
                projectProperties = (ProjectPropertiesTO) unmarshaller.unmarshal(reader);
                reader.close();
            }
        } catch (MarshalException e) {
            throw new DAOException(e);
        } catch (ValidationException e) {
            throw new DAOException(e);
        } catch (IOException e) {
            throw new DAOException(e);
        } catch (MappingException e) {
            throw new DAOException(e);
        } catch (CoreException e) {
            throw new DAOException(e);
        }
        
        return projectProperties;
    }
    
    /**
     * Save project properties
     * @param project a project
     * @param projectProperties the project properties to save
     * @param monitor a progress monitor
     * @throws DAOException
     */
    public void writeProjectProperties(final IProject project, final ProjectPropertiesTO projectProperties, final IProgressMonitor monitor) throws DAOException {
        try {
            final Mapping mapping = new Mapping();
            final URL mappingSpecUrl = this.getClass().getResource(PROPERTIES_MAPPING);
            mapping.loadMapping(mappingSpecUrl);

            final StringWriter writer = new StringWriter();
            final Marshaller marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
            marshaller.marshal(projectProperties);
            writer.flush();
            writer.close();

            final IFile propertiesFile = project.getFile(PROPERTIES_FILE);
            if (propertiesFile.exists() && propertiesFile.isAccessible()) {
                propertiesFile.setContents(new ByteArrayInputStream(writer.toString().getBytes()), false, false, monitor);
            } else {
                propertiesFile.create(new ByteArrayInputStream(writer.toString().getBytes()), false, monitor);
            }
        } catch (MarshalException e) {
            throw new DAOException(e);
        } catch (ValidationException e) {
            throw new DAOException(e);
        } catch (IOException e) {
            throw new DAOException(e);
        } catch (MappingException e) {
            throw new DAOException(e);
        } catch (CoreException e) {
            throw new DAOException(e);
        }
    }
}
