/*
 * Created on 09.11.2006
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

/**
 *
 * @author Sven
 *
 */

public class MarkerRecord extends AbstractPMDRecord {
    private AbstractPMDRecord[] children;
    private final FileRecord parent;
    private final String ruleName;
    private final int priority;
    private final List<IMarker> markers;

    /**
     * Constructor.
     *
     * @param javaResource the given File
     */
    public MarkerRecord(FileRecord parent, String ruleName, int priority) {
        super();
        this.parent = parent;
        this.ruleName = ruleName;
        this.priority = priority;
        this.markers = new ArrayList<IMarker>();
        this.children = AbstractPMDRecord.EMPTY_RECORDS;
    }

    public void addViolation(IMarker marker) {
        this.markers.add(marker);
    }

    public int getViolationsCounted() {
        return markers.size();
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource)
     */
    @Override
    public AbstractPMDRecord addResource(IResource resource) {
        return null;
    }

    public void updateChildren() {
        this.children = createChildren();
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#createChildren()
     */
    @Override
    public final AbstractPMDRecord[] createChildren() {
        final List<AbstractPMDRecord> children = new ArrayList<AbstractPMDRecord>();

        final List<AbstractPMDRecord> markers = parent.getParent().findResourcesByName(this.ruleName, TYPE_MARKER);
        final Iterator<AbstractPMDRecord> markerIterator = markers.iterator();

        while (markerIterator.hasNext()) {
            final MarkerRecord marker = (MarkerRecord) markerIterator.next();
            children.add(new FileToMarkerRecord(marker)); // NOPMD by Sven on 13.11.06 12:05
        }

        return children.toArray(new AbstractPMDRecord[children.size()]);
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getChildren()
     */
    @Override
    public AbstractPMDRecord[] getChildren() {
        return children; // NOPMD by Sven on 13.11.06 12:05
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getName()
     */
    @Override
    public String getName() {
        return ruleName;
    }

    public int getPriority() {
        return priority;
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getParent()
     */
    @Override
    public AbstractPMDRecord getParent() {
        return parent;
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResource()
     */
    @Override
    public IResource getResource() {
        return parent.getResource();
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResourceType()
     */
    @Override
    public int getResourceType() {
        return TYPE_MARKER;
    }

    /*
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource)
     */
    @Override
    public AbstractPMDRecord removeResource(IResource resource) {
        return null;
    }

    @Override
    public boolean hasMarkers() {
        return markers.size() > 0;
    }

    @Override
    public IMarker[] findMarkers() {
        return markers.toArray(new IMarker[markers.size()]);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfViolationsToPriority(int)
     */
    @Override
    public int getNumberOfViolationsToPriority(int prio, boolean invertMarkerAndFileRecords) {
        int number = 0;
        if (prio == priority) {
            if (invertMarkerAndFileRecords) {
                for (AbstractPMDRecord element : children) {
                    number += element.getNumberOfViolationsToPriority(prio, false);
                }
            } else {
                number = getViolationsCounted();
            }
        }
        return number;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getLOC()
     */
    @Override
    public int getLOC() {
        return parent.getLOC();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfMethods()
     */
    @Override
    public int getNumberOfMethods() {
        return parent.getNumberOfMethods();
    }
}
