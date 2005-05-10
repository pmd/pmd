/*
 * Created on 24 nov. 2004
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.eclipse.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

/**
 * This class holds methods factory for plugin models
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2005/05/10 21:49:29  phherlin
 * Fix new violations detected by PMD 3.1
 *
 * Revision 1.2  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.1  2004/11/28 20:31:38  phherlin
 * Continuing the refactoring experiment
 *
 *
 */
public class ModelFactory {
    private static final ModelFactory SELF = new ModelFactory();

    // @PMD:REVIEWED:SingularField: by Herlin on 10/05/05 23:42
    private final Map projectPropertiesModels = new HashMap();

    // @PMD:REVIEWED:SingularField: by Herlin on 10/05/05 23:42
    private final PreferencesModel preferencesModel = new PreferencesModelImpl();

    /**
     * Default private constructor. The ModelFactory is a singleton
     */
    private ModelFactory() {
        super();
    }
    
    /**
     * @return the default implementation
     */
    public static ModelFactory getFactory() {
        return SELF;
    }
    
    /**
     * Method factory for ProjectPropertiesModels
     * @param project the project for which properties are requested
     * @return The PMD related properties for that project
     */
    public ProjectPropertiesModel getProperiesModelForProject(final IProject project) throws ModelException {
        if (project == null) {
            throw new ModelException("A project cannot be null");
        }
        
        ProjectPropertiesModel model;
        synchronized (this.projectPropertiesModels) {
            model = (ProjectPropertiesModel) this.projectPropertiesModels.get(project.getName());
            if (model == null) {
                model = new ProjectPropertiesModelImpl(project);
                this.projectPropertiesModels.put(project.getName(), model);
            }
        }
        
        return model;
    }
 
    /**
     * Method factory for Preferences Model.
     * @return the plugin preferences model
     */
    public PreferencesModel getPreferencesModel() {
        return this.preferencesModel;
    }
 

}
