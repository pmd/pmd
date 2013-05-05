/*
 * Created on 9 mai 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.eclipse.runtime.properties.impl;

import org.eclipse.core.resources.IProject;

import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.eclipse.runtime.properties.IPropertiesFactory;

/**
 * Implements a factory for the objects of the properties package
 * 
 * @author Herlin
 *
 */

public class PropertiesFactoryImpl implements IPropertiesFactory {
    private IProjectPropertiesManager projectPropertiesManager;

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IPropertiesFactory#getProjectPropertiesManager()
     */
    public IProjectPropertiesManager getProjectPropertiesManager() {
        if (this.projectPropertiesManager == null) {
            this.projectPropertiesManager = new ProjectPropertiesManagerImpl();
        }
        
        return this.projectPropertiesManager;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IPropertiesFactory#newProjectProperties(org.eclipse.core.resources.IProject, net.sourceforge.pmd.eclipse.runtime.properties.IProjectPropertiesManager)
     */
    public IProjectProperties newProjectProperties(IProject project, IProjectPropertiesManager projectPropertiesManager) {
        return new ProjectPropertiesImpl(project, projectPropertiesManager);
    }

}
