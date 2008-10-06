/*
 * Created on 12.11.2006
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

package net.sourceforge.pmd.eclipse.ui.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

/**
 * 
 * @author Sven
 *
 */

public class FileToMarkerRecord extends AbstractPMDRecord {
    private final MarkerRecord parent;
    
    public FileToMarkerRecord(MarkerRecord parent) {
        super();
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource)
     */
    public AbstractPMDRecord addResource(IResource resource) {
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#createChildren()
     */
    protected AbstractPMDRecord[] createChildren() {
        return AbstractPMDRecord.EMPTY_RECORDS;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getChildren()
     */
    public AbstractPMDRecord[] getChildren() {
        return AbstractPMDRecord.EMPTY_RECORDS;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getName()
     */
    public String getName() {
        return "";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfViolationsToPriority(int, boolean)
     */
    public int getNumberOfViolationsToPriority(int prio, boolean invertMarkerAndFileRecords) {
        return parent.getNumberOfViolationsToPriority(prio, false);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getParent()
     */
    public AbstractPMDRecord getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResource()
     */
    public IResource getResource() {
        return parent.getResource();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResourceType()
     */
    public int getResourceType() {
        return 0;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource)
     */
    public AbstractPMDRecord removeResource(IResource resource) {
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#hasMarkers()
     */
    public boolean hasMarkers() {
        return parent.hasMarkers();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#findMarkers()
     */
    public IMarker[] findMarkers() {
        return parent.findMarkers();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getLOC()
     */
    public int getLOC() {
        return parent.getLOC();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfMethods()
     */
    public int getNumberOfMethods() {
        return parent.getNumberOfMethods();
    }
}
