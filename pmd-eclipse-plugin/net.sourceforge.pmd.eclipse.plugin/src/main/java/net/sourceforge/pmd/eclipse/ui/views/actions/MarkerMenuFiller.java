package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class MarkerMenuFiller extends ContributionItem {

	private final ITextEditor editor;
	private final IVerticalRulerInfo rulerInfo;
	private final List<IMarker> markers;

	public MarkerMenuFiller(ITextEditor thEditor) {
	    editor = thEditor;
	    rulerInfo = getRulerInfo();
	    markers = getMarkers();
	}

	private IVerticalRulerInfo getRulerInfo() {
	    return (IVerticalRulerInfo) editor.getAdapter(IVerticalRulerInfo.class);
	}

	private List<IMarker> getMarkers() {
	    List<IMarker> clickedOnMarkers = new ArrayList<IMarker>();
	    for (IMarker marker : getAllMarkers()){
	        if (markerHasBeenClicked(marker)){
	            clickedOnMarkers.add(marker);
	        }
	    }

	    return clickedOnMarkers;
	}

	//Determine whether the marker has been clicked using the ruler's mouse listener
	private boolean markerHasBeenClicked(IMarker marker) {
	    return marker.getAttribute(IMarker.LINE_NUMBER, 0) == (rulerInfo.getLineOfLastMouseButtonActivity() + 1);
	}

	//Get all My Markers for this source file
	private IMarker[] getAllMarkers() {
		IFile sourceFile = ((FileEditorInput) editor.getEditorInput()).getFile(); 
		try {
			return sourceFile.findMarkers(
					null,	//"defined.in.plugin.xml.mymarker", 
					true, 
					IResource.DEPTH_ZERO
					);
			} catch (CoreException ce) {
				return MarkerUtil.EMPTY_MARKERS;
			}
	}

	@Override
	public void fill(Menu menu, int index) {
		
//		MenuItem separator = new MenuItem(menu, SWT.SEPARATOR, index);
		
	    for (final IMarker marker : markers) {
	    	String ruleName = marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_RULENAME, "");
	    	if (StringUtil.isEmpty(ruleName)) continue;
	    		    	 
	        MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
	        menuItem.setText("Disable rule: " + ruleName);
	        menuItem.addSelectionListener(createDynamicSelectionListener(marker));
	    }
	}

	private static SelectionAdapter createDynamicSelectionListener(final IMarker marker) {
	    return new SelectionAdapter() {
	        public void widgetSelected(SelectionEvent e) {
	        	disableRules(marker, true);
	        }
	    };
	}
	
	private static void disableRules(IMarker marker, boolean removeViolations) {
		
		DisableRuleAction.runWith(
			new IMarker[] {marker}, 
			PMDPlugin.getDefault().loadPreferences(),
			removeViolations
			);
	}
}

