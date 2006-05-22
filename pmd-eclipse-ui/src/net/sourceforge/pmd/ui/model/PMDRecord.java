package net.sourceforge.pmd.ui.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

/**
 * Abstract class containing  the "Framework" of the PMD-Model
 * Contains Method to check for Resources and Markers and 
 * abstract Methods, that need to bee implemented in classes 
 * to construct the model. The standard PMD-Model contains:
 * the Root -> Project(s) -> Package(s) -> File(s)
 * 
 * @author SebastianRaffel  ( 16.05.2005 )
 */
public abstract class PMDRecord {
	
	public final static int TYPE_ROOT = IResource.ROOT;
	public final static int TYPE_PROJECT = IResource.PROJECT;
	public final static int TYPE_PACKAGE = IResource.FOLDER;
	public final static int TYPE_FILE = IResource.FILE;
	
	/**
	 * @return the Name of the Element
	 */
	public abstract String getName();
	
	/**
	 * Gets the parent Element. For a File this is a Package,
	 * for a Package this is a Project and for a Project this is the Root.
	 * 
	 * @return the parent Element
	 */
	public abstract PMDRecord getParent();
	
	/**
	 * Gets a List for all children. The Structure is 
	 * Root -> Project(s) -> Package(s) -> File(s).
	 * 
	 * @return a List of the child-Elements
	 */
	public abstract PMDRecord[] getChildren();
	
	/**
	 * Returns the children as an ArrayList
	 * 
	 * @return an ArrayList with the child-Elements
	 */
	public ArrayList getChildrenAsList() {
		return new ArrayList(Arrays.asList(getChildren()));
	}
	
	/**
	 * Gets the Resource corresponding to this Element. The Resource can be 
	 * the Root, a Project, a Folder (Packages are Folders) or a File.
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
	 * Adds a Resource to the Model and returns a new PMDRecord for it.
	 * 
	 * @param resource, the Element to insert
	 * @return a new PMDRecord with the given Element in it 
	 * or null if the Resource does not exist
	 */
	public abstract PMDRecord addResource(IResource resource);
	
	/**
	 * Removes a Resource and also deletes the Record for it.
	 * Returns the removed Record.
	 * 
	 * @param resource, the Resource to remove 
	 * @return the Record for the Resource 
	 * or null if the Resource does not exist
	 */
	public abstract PMDRecord removeResource(IResource resource);
	
	/**
	 * Creates the children Elements. This method should be called
	 * in every Constructor to create a Model recursively.  
	 * 
	 * @return an Array of child-Records for the Element.
	 */
	protected abstract PMDRecord[] createChildren();
	
	/**
	 * Checks, if this Element has Error-Markers in it.
	 * Calls the underlying children (Recursion) to also check for Markers.
	 * The Recursion needs to be stopped by overwriting this function in 
	 * one implementing class.   
	 * 
	 * @return true, if the element or one of its children has Markers,
	 * false otherwise
	 */
	public boolean hasMarkers() {
		PMDRecord[] children = getChildren();
		
		for (int i=0; i<children.length; i++) {
			// recusively check children elements
			if (children[i].hasMarkers())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Finds Error-Markers for this element and recursively for its 
	 * children Elements. The Recursion needs to be stopped by overwriting 
	 * this function in some implementing Class.
	 * 
	 * @return an Array of Markers or null, if no Markers were found
	 */
	public IMarker[] findMarkers() {
		ArrayList markerList = new ArrayList();
		PMDRecord[] children = getChildren();
		
		for (int i=0; i<children.length; i++) {
			if (children[i].hasMarkers()) {
				// get the childrens markers
				IMarker[] childrenMarkers = children[i].findMarkers();
				if (childrenMarkers != null)
					// ...and add them to the list
					markerList.addAll(Arrays.asList(childrenMarkers));
			}
		}
		
		if (markerList.isEmpty())
			return null;
		
		// return the Marker-List as an IMarker-Array
		IMarker[] markerArray = new IMarker[markerList.size()];
		markerList.toArray(markerArray);
		return markerArray;
	}
	
	/**
	 * Finds Markers that have a given Attribute with a given Value.
	 * Finds children Markers recursively (needs to be stopped by overwriting). 
	 * 
	 * @param attributeName, the Name of the Attribute to search for
	 * @param value, the Value, the Attribute should have
	 * @return a List of Markers that have the given attribute and Value
	 * or null, if no Markers were found
	 */
	public IMarker[] findMarkersByAttribute(String attributeName, Object value) {
		ArrayList markerList = new ArrayList();
		PMDRecord[] children = getChildren();
		
		for (int i=0; i<children.length; i++) {
			IMarker[] childrenMarkers =
				// search the children for the Markers 
				children[i].findMarkersByAttribute(attributeName, value);
			if (childrenMarkers != null)
				// ... and add their Markers to the List
				markerList.addAll(Arrays.asList(childrenMarkers));
		}
		
		if (markerList.isEmpty())
			return null;
		
		// return the IMarker-Array
		IMarker[] markerArray = new IMarker[markerList.size()];
		markerList.toArray(markerArray);
		return markerArray;
	}
	
	/**
	 * Finds a Resource and returns th corresponding Record.
	 * Recursivel searches the children, needs to be stopped by overwriting.
	 * 
	 * @param resource, the Resource to search for
	 * @return the corresponding PMDRecord for the Resource
	 * or null, if the Resource could not be found
	 */
	public PMDRecord findResource(IResource resource) {
		ArrayList thisChildren = getChildrenAsList();
		for (int l=0; l<thisChildren.size(); l++) {
			PMDRecord thisChild = (PMDRecord) thisChildren.get(l);
			
			// check the children if the Resource exists
			if (thisChild.getResource().equals(resource)) {
				return thisChild;
			} else if (thisChild.getResourceType() == resource.getType()) {
				// if it is not the current children, but has the same Type
				// it could be the next one 
				continue;
			} else {
				// else we check this childs children recursively
				PMDRecord grandChild = thisChild.findResource(resource);
				if (grandChild == null)
					continue;
				else
					return grandChild;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds a Resource with a given Name and Type.
	 * Checks children recursively (needs to be stopped by overwriting).
	 * 
	 * @param name, the Name of the Resource
	 * @param type, the Type, one of ROOT, PROJECT, PACKAGE or FILE
	 * @return the Record for the Resource or null, 
	 * if the Resource could not be found
	 */
	public PMDRecord findResourceByName(String name, int type) {
		ArrayList thisChildren = getChildrenAsList();
		for (int l=0; l<thisChildren.size(); l++) {
			PMDRecord thisChild = (PMDRecord) thisChildren.get(l);
			if (thisChild.getResourceType() == type) {
				if (thisChild.getName().equalsIgnoreCase(name))
					return thisChild;
				else
					// if the Name is wrong but the Type is right
					// it could be another one of the children
					continue;
			} else {
				// else we check the childs children the same way
				PMDRecord grandChild = 
					thisChild.findResourceByName(name, type);
				if (grandChild == null)
					continue;
				else
					return grandChild;
			}
		}
		
		return null;
	}
}
