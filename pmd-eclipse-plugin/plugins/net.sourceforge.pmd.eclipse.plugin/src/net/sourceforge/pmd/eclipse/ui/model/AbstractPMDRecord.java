/*
 * Created on 7 mai 2005
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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

/**
 * Abstract class containing the "Framework" of the PMD-Model Contains Method to
 * check for Resources and Markers and abstract Methods, that need to bee
 * implemented in classes to construct the model. The standard PMD-Model
 * contains: the Root -> Project(s) -> Package(s) -> File(s)
 * 
 * @author SebastianRaffel ( 16.05.2005 ), Philippe Herlin, Sven Jacob
 * 
 */
public abstract class AbstractPMDRecord {
    public final static int TYPE_ROOT = IResource.ROOT;
    public final static int TYPE_PROJECT = IResource.PROJECT;
    public final static int TYPE_PACKAGE = IResource.FOLDER;
    public final static int TYPE_FILE = IResource.FILE;
    public final static int TYPE_MARKER = 16;
    
    public static final AbstractPMDRecord[] EMPTY_RECORDS = new AbstractPMDRecord[0];
    /**
     * @return the Name of the Element
     */
    public abstract String getName();

    /**
     * Gets the parent Element. For a File this is a Package, for a Package this
     * is a Project and for a Project this is the Root.
     * 
     * @return the parent Element
     */
    public abstract AbstractPMDRecord getParent();

    /**
     * Gets a List for all children. The Structure is Root -> Project(s) ->
     * Package(s) -> File(s).
     * 
     * @return a List of the child-Elements
     */
    public abstract AbstractPMDRecord[] getChildren();

    /**
     * Returns the children as an ArrayList
     * 
     * @return an ArrayList with the child-Elements
     */
    public List getChildrenAsList() {
        return new ArrayList(Arrays.asList(getChildren()));
    }

    /**
     * Gets the Resource corresponding to this Element. The Resource can be the
     * Root, a Project, a Folder (Packages are Folders) or a File.
     * 
     * @return the Resource for this Element
     */
    public abstract IResource getResource();

    /**
     * Gets the Resource Type. One of ROOT, PROJECT, PACKAGE or FILE.
     * 
     * @return the Resource type
     */
    public abstract int getResourceType();

    /**
     * Adds a Resource to the Model and returns a new AbstractPMDRecord for it.
     * 
     * @param resource, the Element to insert
     * @return a new AbstractPMDRecord with the given Element in it or null if
     *         the Resource does not exist
     */
    public abstract AbstractPMDRecord addResource(IResource resource);

    /**
     * Removes a Resource and also deletes the Record for it. Returns the
     * removed Record.
     * 
     * @param resource, the Resource to remove
     * @return the Record for the Resource or null if the Resource does not
     *         exist
     */
    public abstract AbstractPMDRecord removeResource(IResource resource);

    /**
     * Gets the number of violations (markers) that belong to a priority.
     * 
     * @param prio priority to search for
     * @return number of found violations
     */
    public abstract int getNumberOfViolationsToPriority(int prio, boolean invertMarkerAndFileRecords);
    
    /**
     * Gets the counted lines of code (loc).
     * This works recursive.
     * 
     * @return loc lines of code
     */
    public abstract int getLOC();
    
    /**
     * Gets the number of methods.
     * This works recursive.
     * 
     * @return number of counted methods
     */
    public abstract int getNumberOfMethods();
    
    /**
     * Creates the children Elements. This method should be called in every
     * Constructor to create a Model recursively.
     * 
     * @return an Array of child-Records for the Element.
     */
    protected abstract AbstractPMDRecord[] createChildren();

    /**
     * Checks, if this Element has Error-Markers in it. Calls the underlying
     * children (Recursion) to also check for Markers. The Recursion needs to be
     * stopped by overwriting this function in one implementing class.
     * 
     * @return true, if the element or one of its children has Markers, false
     *         otherwise
     */
    public boolean hasMarkers() {
        final AbstractPMDRecord[] children = getChildren();
        boolean result = false;

        for (int i = 0; (i < children.length) && !result; i++) {
            // recusively check children elements
            if (children[i].hasMarkers()) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Finds Error-Markers for this element and recursively for its children
     * Elements. The Recursion needs to be stopped by overwriting this function
     * in some implementing Class.
     * 
     * @return an Array of Markers or null, if no Markers were found
     */
    public IMarker[] findMarkers() {
        final List markerList = new ArrayList();
        final AbstractPMDRecord[] children = getChildren();

        for (int i = 0; i < children.length; i++) {
            if (children[i].hasMarkers()) {
                // get the childrens markers
                final IMarker[] childrenMarkers = children[i].findMarkers();
                if (childrenMarkers != null) {
                    // ...and add them to the list
                    markerList.addAll(Arrays.asList(childrenMarkers));
                }
            }
        }

        return markerList.isEmpty() ? null : (IMarker[]) markerList.toArray(new IMarker[markerList.size()]); // NOPMD by Herlin on 07/10/06 15:51
    }

    /**
     * Finds Markers that have a given Attribute with a given Value. Finds
     * children Markers recursively (needs to be stopped by overwriting).
     * 
     * @param attributeName, the Name of the Attribute to search for
     * @param value, the Value, the Attribute should have
     * @return a List of Markers that have the given attribute and Value or
     *         null, if no Markers were found
     */
    public IMarker[] findMarkersByAttribute(String attributeName, Object value) {
        final List markerList = new ArrayList();
        final AbstractPMDRecord[] children = getChildren();

        for (int i = 0; i < children.length; i++) {
            // search the children for the Markers
            final IMarker[] childrenMarkers = children[i].findMarkersByAttribute(attributeName, value);

            // ... and add their Markers to the List
            markerList.addAll(Arrays.asList(childrenMarkers));
        }

        return (IMarker[])markerList.toArray(new IMarker[markerList.size()]);
    }

    /**
     * Finds a Resource and returns th corresponding Record. Recursivel searches
     * the children, needs to be stopped by overwriting.
     * 
     * @param resource, the Resource to search for
     * @return the corresponding AbstractPMDRecord for the Resource or null, if
     *         the Resource could not be found
     */
    public AbstractPMDRecord findResource(IResource resource) {
        AbstractPMDRecord record = null;
        final List thisChildren = getChildrenAsList();

        for (int l = 0; (l < thisChildren.size()) && (record == null); l++) {
            final AbstractPMDRecord thisChild = (AbstractPMDRecord) thisChildren.get(l);

            // check the children if the Resource exists
            if (thisChild.getResource().equals(resource)) {
                record = thisChild;
            }
                        
            // if it is not the current children, but the Type are different
            // it could be one of the grand children
            // check this childs children recursively
            else if (thisChild.getResourceType() != resource.getType()) {
                final AbstractPMDRecord grandChild = thisChild.findResource(resource);
                if (grandChild != null) {
                    record = grandChild;
                }
            }
        }

        return record;
    }

    /**
     * Finds a Resource with a given Name and Type. Checks children recursively
     * (needs to be stopped by overwriting).
     * 
     * @param name, the Name of the Resource
     * @param type, the Type, one of ROOT, PROJECT, PACKAGE or FILE
     * @return the Record for the Resource or null, if the Resource could not be
     *         found
     */
    public AbstractPMDRecord findResourceByName(String name, int type) {
        AbstractPMDRecord record = null;
        final List thisChildren = getChildrenAsList();

        for (int l = 0; (l < thisChildren.size()) && (record == null); l++) {
            final AbstractPMDRecord thisChild = (AbstractPMDRecord) thisChildren.get(l);

            // If type and name are equals, then the record is the one we search
            if ((thisChild.getResourceType() == type) && thisChild.getName().equalsIgnoreCase(name)) {
                record = thisChild;
            }
            
            // else we check the childs children the same way
            else {
                final AbstractPMDRecord grandChild = thisChild.findResourceByName(name, type);
                if (grandChild != null) {
                    record = grandChild;
                }
            }
        }

        return record;
    }
    
    /**
     * Finds all Resources with a given Name and Type. Checks children recursively
     * (needs to be stopped by overwriting).
     * 
     * @param name, the Name of the Resource
     * @param type, the Type, one of ROOT, PROJECT, PACKAGE or FILE
     * @return all resources that match the name and type
     */
    public List findResourcesByName(String name, int type) {
        final List records = new ArrayList();
        final List thisChildren = getChildrenAsList();

        for (int l = 0; l < thisChildren.size(); l++) {
            final AbstractPMDRecord thisChild = (AbstractPMDRecord) thisChildren.get(l);

            // If type and name are equals, then the record is the one we search
            if ((thisChild.getResourceType() == type) && thisChild.getName().equalsIgnoreCase(name)) {
                records.add(thisChild);
            }
            
            // else we check the childs children the same way
            else {
                final List grandChilds = thisChild.findResourcesByName(name, type);
                records.addAll(grandChilds);
            }
        }

        return records;
    }
}
