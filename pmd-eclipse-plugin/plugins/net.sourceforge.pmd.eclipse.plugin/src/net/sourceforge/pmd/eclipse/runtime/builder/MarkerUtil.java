package net.sourceforge.pmd.eclipse.runtime.builder;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Brian Remedios
 */
public class MarkerUtil {

    public static final IMarker[] EMPTY_MARKERS = new IMarker[0];
    
	private MarkerUtil() {	}

	public static void deleteAllMarkersIn(IResource resource) throws CoreException {
		deleteMarkersIn(resource, PMDRuntimeConstants.ALL_MARKER_TYPES);
	}
	
	public static void deleteMarkersIn(IResource resource, String markerType) throws CoreException {
		deleteMarkersIn(resource, new String[] {markerType} );
	}
	
	public static void deleteMarkersIn(IResource resource, String[] markerTypes) throws CoreException {
		for (String markerType : markerTypes) {
			resource.deleteMarkers(markerType, true, IResource.DEPTH_INFINITE);
		}
	}

	public static IMarker[] findAllMarkers(IResource resource) throws CoreException {
		return findMarkers(resource, PMDRuntimeConstants.ALL_MARKER_TYPES);
	}

	public static IMarker[] findMarkers(IResource resource, String markerType) throws CoreException {
		return findMarkers(resource, new String[] { markerType} );
	}

	public static IMarker[] findMarkers(IResource resource, String[] markerTypes) throws CoreException { 
		
		List<IMarker> markerList = new ArrayList<IMarker>();
		
		for (String markerType : markerTypes) {
	    	for (IMarker marker : resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE)) {
	    		markerList.add(marker);
	    	}
		}
		
		IMarker[] markerArray = new IMarker[markerList.size()];
		return markerList.toArray(markerArray);
	}

}
