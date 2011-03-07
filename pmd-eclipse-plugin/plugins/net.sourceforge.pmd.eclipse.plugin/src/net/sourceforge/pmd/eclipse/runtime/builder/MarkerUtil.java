package net.sourceforge.pmd.eclipse.runtime.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.model.MarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.RootRecord;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Brian Remedios
 */
public class MarkerUtil {

    public static final IMarker[] EMPTY_MARKERS = new IMarker[0];
    
    private static Map<String, Rule> rulesByName;
    
	private MarkerUtil() {	}

	public static boolean hasAnyRuleMarkers(IResource resource) throws CoreException {
		
		final boolean[] foundOne = new boolean[] { false };
		
	    IResourceVisitor ruleMarkerFinder = new IResourceVisitor() {
	 
	        public boolean visit(IResource resource) {
	        	
	        	if (foundOne[0]) return false;
	        	
	            if (resource instanceof IFile) {

	            	for (String markerType : PMDRuntimeConstants.RULE_MARKER_TYPES) {
		            	IMarker[] ruleMarkers = null;
		            	try {
		            		ruleMarkers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
		            		} catch (CoreException ex) {
		            			// what do to?
		            		}
		            	if (ruleMarkers.length > 0) {
		            		foundOne[0] = true;
		            		return false;
		            	}
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
	
	private static IProject projectFor(IResource resource) {
				
		if (resource instanceof IWorkspaceRoot) return null;
		if (resource instanceof IProject) return (IProject)resource;
		return projectFor(resource.getParent());
	}
	
	public static Set<IProject> commonProjectsOf(IMarker[] markers) {
		
		Set<IProject> projects = new HashSet<IProject>();
		
		for (IMarker marker : markers) {
			IProject project = projectFor(marker.getResource());
			if (project != null) projects.add(project);
		}
		
		return projects;
	}
	
    public static String ruleNameFor(IMarker marker) {
    	return marker.getAttribute(PMDUiConstants.KEY_MARKERATT_RULENAME, "");
    }
    
    public static int rulePriorityFor(IMarker marker) throws CoreException {
    	return (Integer)marker.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY);
    }
    
    public static String messageFor(IMarker marker, String defaultValue) {
    	return marker.getAttribute(IMarker.MESSAGE, defaultValue);
    }
    
    public static Long createdOn(IMarker marker, long onErrorValue) {
    	
    	try {
    		return (Long)marker.getCreationTime();
    		} catch (CoreException ce) {
    			return onErrorValue;
    		}
    }
    
    public static int rulePriorityFor(IMarker marker, int defaultValue) {
   		return marker.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY, defaultValue);
    }

    public static boolean doneState(IMarker marker, boolean defaultValue) {
   		return marker.getAttribute(IMarker.DONE, defaultValue);
    }
    
    public static int deleteViolationsOf(String ruleName, IResource resource) {
    	
    	try {
	    	IMarker[] markers = findAllMarkers(resource);
	    	if (markers.length == 0) return 0;
	    	
	    	List<IMarker> matches = new ArrayList<IMarker>(markers.length);
	    	
	    	for (IMarker marker : markers) {
	    		String name = ruleNameFor(marker);
	    		if (ruleName.equals(name)) {
	    			matches.add(marker);
	    		}
	    	}
	    	
	    	markers = new IMarker[matches.size()];
	    	matches.toArray(markers);
	    	resource.getWorkspace().deleteMarkers(markers);
	    	
	    	return markers.length;
    	} catch (CoreException ex) {
    		return 0;
    	}
    }
    
	public static List<IMarkerDelta> markerDeltasIn(IResourceChangeEvent event) {
		
		List<IMarkerDelta> deltas = new ArrayList<IMarkerDelta>();
		for (String markerType : PMDRuntimeConstants.RULE_MARKER_TYPES) {
			IMarkerDelta[] deltaArray = event.findMarkerDeltas(markerType, true);
			for (IMarkerDelta delta : deltaArray) deltas.add(delta);
		}
		
		return deltas;		
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
		PMDPlugin.getDefault().removedMarkersIn(resource);
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

	public static Set<Integer> priorityRangeOf(IResource resource, String[] markerTypes, int sizeLimit) throws CoreException { 
		
		Set<Integer> priorityLevels = new HashSet<Integer>(sizeLimit);
		
		for (String markerType : markerTypes) {
	    	for (IMarker marker : resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE)) {
	    		priorityLevels.add( rulePriorityFor(marker) );
	    		if (priorityLevels.size() == sizeLimit) return priorityLevels;
	    	}
		}
		
		return priorityLevels;
	}
	
	public static Set<String> currentRuleNames() {
		gatherRuleNames();
		return rulesByName.keySet();
	}
	
	private static void gatherRuleNames() {

		rulesByName = new HashMap<String, Rule>();
		Set<RuleSet> ruleSets = PMDPlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
		for (RuleSet rs : ruleSets) {
			for (Rule rule : rs.getRules()) {
				rulesByName.put(rule.getName(), rule);
			}
		}
	}

	private static Rule ruleFrom(IMarker marker) {
		String ruleName = marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_RULENAME, "");
		if (StringUtil.isEmpty(ruleName)) return null;	//printValues(marker);
		return rulesByName.get(ruleName);		
	}
	
	public static Set<IFile> allMarkedFiles(RootRecord root) {
		
		gatherRuleNames();
		
		Set<IFile> files = new HashSet<IFile>();
		
		for (AbstractPMDRecord projectRecord : root.getChildren()) {
			for (AbstractPMDRecord packageRecord : projectRecord.getChildren()) {
				for (AbstractPMDRecord fileRecord : packageRecord.getChildren()) {
					((FileRecord)fileRecord).updateChildren();
					for (AbstractPMDRecord mRecord : fileRecord.getChildren()) {
						MarkerRecord markerRecord = (MarkerRecord) mRecord;
						for (IMarker marker : markerRecord.findMarkers()) {
							Rule rule = ruleFrom(marker);
							if (rule == null) continue;
							files.add((IFile)fileRecord.getResource());
							break;
						}
					}
				}
			}
		}
		
		return files;
	}
}
