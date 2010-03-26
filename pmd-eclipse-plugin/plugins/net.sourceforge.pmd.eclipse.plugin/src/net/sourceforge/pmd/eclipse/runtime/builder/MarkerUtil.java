package net.sourceforge.pmd.eclipse.runtime.builder;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Brian Remedios
 */
public class MarkerUtil {

    public static final IMarker[] EMPTY_MARKERS = new IMarker[0];
    
	private MarkerUtil() {	}

	public static boolean hasAnyRuleMarkers(IResource resource) throws CoreException {
		
		final boolean foundOne[] = new boolean[] { false };
		
	    IResourceVisitor ruleMarkerFinder = new IResourceVisitor() {
	 
	        public boolean visit(IResource resource) {
	        	
	        	if (foundOne[0]) return false;
	        	
	            if (resource instanceof IFile) {

	            	IMarker[] ruleMarkers = null;
	            	try {
	            		ruleMarkers = resource.findMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
	            		} catch (CoreException ex) {
	            			// what do to?
	            		}
	            	if (ruleMarkers.length > 0) {
	            		foundOne[0] = true;
	            		return false;
	            	}
	            }

	            return true;
	        }
	    };
		
		try {
			resource.accept(ruleMarkerFinder);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return foundOne[0];
	}
	
    public static String ruleNameFor(IMarker marker) {
    	return marker.getAttribute(PMDUiConstants.KEY_MARKERATT_RULENAME, "");
    }
    
    public static int rulePriorityFor(IMarker marker) throws CoreException {
    	return ((Integer)marker.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY)).intValue();
    }
    
    public static List<Rule> rulesFor(IMarker[] markers) {
    	
    	List<Rule> rules = new ArrayList<Rule>(markers.length);
    	RuleSet ruleset = PMDPlugin.getDefault().getPreferencesManager().getRuleSet();

    	for (IMarker marker : markers) {
    		String name = ruleNameFor(marker);
    		if (StringUtil.isEmpty(name)) continue;
    		Rule rule = ruleset.getRuleByName(name);
    		if (rule == null) continue;
    		rules.add(rule);
    	}
    	
    	return rules;
    }
    
    /**
     * Returns the name of the rule that is common to all markers
     * or null if any one of them differ.
     * 
     * @param IMarker[] markers
     * @return String
     */
	public static String commonRuleNameAmong(IMarker[] markers) {

    	String ruleName = ruleNameFor(markers[0]);
    	for (int i=1; i<markers.length; i++) {
    		if (!ruleName.equals(ruleNameFor(markers[i]))) return null;
    	}
    	
    	return ruleName;
	}
	
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
