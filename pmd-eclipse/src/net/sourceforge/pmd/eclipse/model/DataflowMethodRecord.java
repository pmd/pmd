package net.sourceforge.pmd.eclipse.model;

import java.util.ArrayList;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;



/**
 * This class holds information for use with the dataflow view.
 * It contains a Java-Method and the corresponding PMD-Method (SimpleNode)
 * and can return Dataflow Anomalies for it.
 * 
 * @author SebastianRaffel  ( 07.06.2005 )
 */
public class DataflowMethodRecord {

	private IMethod method;
	private SimpleNode node;
	
	
	/**
	 * Constructor
	 * 
	 * @param javaMethod, the Method of the JavaModel
	 * @param pmdMethod, the corresponding PMD-SimpleNode / ASTMethodDeclaration
	 */
	public DataflowMethodRecord(IMethod javaMethod, SimpleNode pmdMethod) {
		method = javaMethod;
		node = pmdMethod;
	}
	
	/**
	 * @return the PMD-Method
	 */
	public SimpleNode getPMDMethod() {
		return node;
	}
	
	/**
	 * @return the Java-Method
	 */
	public IMethod getJavaMethod() {
		return method;
	}
	
	/**
	 * @return the Resource (File) that contains this Method
	 */
	public IResource getResource() {
		return method.getResource();
	}
	
	/**
	 * Finds Dataflow-Anomalies for a Method
	 * 
	 * @return a List of Anomalies
	 */
	public IMarker[] getMarkers() {
		ArrayList markers = new ArrayList();
		try {
			// we can only find Markers for a file
			// we use the DFA-Marker-ID set for Dataflow Anomalies
			IMarker[] allMarkers = method.getResource().findMarkers(
				PMDPlugin.PMD_DFA_MARKER, true, IResource.DEPTH_INFINITE);
			
			// we only want to get the Markers for this Method,
			// so we need to "extract" them from the whole List
			for (int i=0; i<allMarkers.length; i++) {
				// the Marker should have valid Information in it
				if (!markerIsValid(allMarkers[i]))
					continue;
				
				// ... and we don't want it twice, so we check,
				// if the Marker already exists
				if (!markerIsInList(allMarkers[i], markers))
					markers.add(allMarkers[i]);
			}
		} catch (CoreException ce) {
			PMDPlugin.getDefault().logError(
				PMDConstants.MSGKEY_ERROR_FIND_MARKER + 
				this.toString(), ce);
		}
		
		// return the Arraylist-Markers as an IMarker-Array
		IMarker[] markerArray = new IMarker[markers.size()];
		markers.toArray(markerArray);
		return markerArray;
	}
	
	/**
	 * Returns a list of Attributes for a Dataflow Marker, 
	 * (1.) the Error Message, (2.) the beginning Line of the Error,
	 * (3.) the ending Line and (4.) the Variable (Marker Attribute)
	 * 
	 * @param marker
	 * @return an Array of Attributes
	 */
	private Object[] getMarkerAttributes(IMarker marker) {
		ArrayList values = new ArrayList();
		
		// add Message, default ""
		values.add(marker.getAttribute(IMarker.MESSAGE, ""));
		
		// add the Lines, default 0
		// the default-Values help preventing an Exception
		int line1 = 
			marker.getAttribute(IMarker.LINE_NUMBER, 0);
		int line2 = 
			marker.getAttribute(PMDPlugin.KEY_MARKERATT_LINE2, 0);
		// exchange the Lines if begin > end 
		if (line2 < line1) {
			int temp = line1;
			line1 = line2;
			line2 = temp;
		}
		values.add(new Integer(line1));
		values.add(new Integer(line2));
		
		// add the Variable
		values.add(marker.getAttribute(
			PMDPlugin.KEY_MARKERATT_VARIABLE, ""));
		
		return values.toArray();
	}
	
	/**
	 * Checks, if a Marker is valid, meaning that it
	 * (1.) is set for this Method (between Begin and End-Line)
	 * and (2.) has a Variable and Message set
	 * 
	 * @param marker
	 * @return true if the Marker is valid, false otherwise
	 */
	private boolean markerIsValid(IMarker marker) {
		// get the Markers atrributes
		Object[] values = getMarkerAttributes(marker);
		int line1 = ((Integer) values[1]).intValue();
		int line2 = ((Integer) values[2]).intValue();
		
		if (node == null)
			return false;
		
		// the Marker has to be in this Method
		if ((line1 < node.getBeginLine()) 
			|| (line2 > node.getEndLine()))
			return false;
		
		// check the Values
		for (int k=0; k<values.length; k++) {
			if (values[k] instanceof String) {
				// if it is a String, it has to be the Variable
				// or Message, which shouldn't be empty
				if (((String) values[k]).equals(""))
					return false;
			} else if (values[k] instanceof Integer) {
				// else it is one of the Lines (Line, Line2)
				// and they also should not be 0
				if (((Integer) values[k]).intValue() == 0)
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if a Marker is already in a List
	 * 
	 * @param marker
	 * @param list
	 * @return true, is the marker exists in thelist, false otherwise
	 */
	private boolean markerIsInList(IMarker marker, ArrayList list) {
		if ((list == null) || list.isEmpty())
			return false;
		
		// here we can't simply compare Objects, because the Dataflow
		// Anomaly Calculation sets different Markers for the same Error
		
		// get the Markers Attributes and compare with all other Markers
		Object[] markerAttr = getMarkerAttributes(marker);
		for (int i=0; i<list.size(); i++) {
			// get the Marker from the List and its Attributes 
			Object[] listAttr = 
				getMarkerAttributes((IMarker) list.get(i));
			
			boolean markersAreEqual = true; 
			for (int j=0; j<listAttr.length; j++) {
				// compare the String- and Integer-Values
				if (markerAttr[j] instanceof String) {
					if (!((String) markerAttr[j]).equalsIgnoreCase(
						(String)listAttr[j]) ) {
						markersAreEqual = false;
					}
				} else if (markerAttr[j] instanceof Integer) {
					if (!((Integer) markerAttr[j]).equals(
						(Integer) listAttr[j]) )
						markersAreEqual = false;
				}
			}
			
			// markersAreEqual only stays true, when all Checks above fail
			// we need to do that to compare _all_ Attributes; if they all
			// are equal, the Marker exists, if not we check the next one 
			if (markersAreEqual)
				return true;
			else
				continue;
		}
		return false;
	}
}
