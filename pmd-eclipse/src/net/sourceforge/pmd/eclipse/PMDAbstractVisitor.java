/*  
 * <copyright>  
 *  Copyright 1997-2003 PMD for Eclipse Development team
 *  under sponsorship of the Defense Advanced Research Projects  
 *  Agency (DARPA).  
 *   
 *  This program is free software; you can redistribute it and/or modify  
 *  it under the terms of the Cougaar Open Source License as published by  
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).   
 *   
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS   
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR   
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF   
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT   
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT   
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL   
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,   
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR   
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.   
 *   
 * </copyright>
 */
package net.sourceforge.pmd.eclipse;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.ResourceWorkingSetFilter;

/**
 * Factor some usefull features for visitors
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/11/04 13:26:38  phherlin
 * Implement the working set feature (working set filtering)
 *
 */
public abstract class PMDAbstractVisitor {
    private IProgressMonitor monitor;
    private boolean useTaskMarker = false;
    private Map accumulator;

    /**
     * Returns the useTaskMarker.
     * @return boolean
     */
    public boolean isUseTaskMarker() {
        return this.useTaskMarker;
    }

    /**
     * Sets the useTaskMarker.
     * @param useTaskMarker The useTaskMarker to set
     */
    public void setUseTaskMarker(boolean useTaskMarker) {
        this.useTaskMarker = useTaskMarker;
    }

    /**
     * Returns the accumulator.
     * @return Map
     */
    public Map getAccumulator() {
        return this.accumulator;
    }

    /**
     * Sets the accumulator.
     * @param accumulator The accumulator to set
     */
    public void setAccumulator(Map accumulator) {
        this.accumulator = accumulator;
    }

    /**
     * @return
     */
    public IProgressMonitor getMonitor() {
        return this.monitor;
    }

    /**
     * @param monitor
     */
    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Test if a file is in the PMD working set
     * @param file
     * @return true if the file should be checked
     */
    protected boolean isFileInWorkingSet(IFile file) {
        boolean fileInWorkingSet = true;
        IWorkingSet workingSet = PMDPlugin.getDefault().getProjectWorkingSet(file.getProject());
        if (workingSet != null) {
            ResourceWorkingSetFilter filter = new ResourceWorkingSetFilter();
            filter.setWorkingSet(workingSet);
            fileInWorkingSet = filter.select(null, null, file);
        }

        return fileInWorkingSet;
    }

}
