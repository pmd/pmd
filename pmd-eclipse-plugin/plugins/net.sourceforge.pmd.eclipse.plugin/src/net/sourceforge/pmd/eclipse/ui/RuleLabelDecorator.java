package net.sourceforge.pmd.eclipse.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

/**
 * 
 * @author Brian Remedios
 */
public class RuleLabelDecorator implements ILightweightLabelDecorator {

	private Collection<ILabelProviderListener> listeners;
	
	private Map<Integer, ImageDescriptor> overlaysByPriority;
	
	public RuleLabelDecorator() {
		reloadDecorators();
	}

	public void addListener(ILabelProviderListener listener) {
		if (listeners == null) listeners = new HashSet<ILabelProviderListener>();
		listeners.add(listener);
	}

	public void dispose() {
		
	}
	
	public void changed(Collection<IResource> resources) {
		
		if (listeners == null) return;
		
		LabelProviderChangedEvent lpce = new LabelProviderChangedEvent(this, resources.toArray());
		
		for (ILabelProviderListener listener : listeners) {
			listener.labelProviderChanged(lpce);
		}
		
	}
	
	public void reloadDecorators() {
		overlaysByPriority = UISettings.markerImgDescriptorsByPriority();
	}

	public boolean isLabelProperty(Object element, String property) { return false;	}

	public void removeListener(ILabelProviderListener listener) {
		if (listeners == null) return;
		listeners.remove(listener);
	}

	public void decorate(Object element, IDecoration decoration) {
		
		if ( !(element instanceof IResource) ) return;
		
		IResource resource = (IResource)element;
		
		Set<Integer> range = null;
		try {
			range = MarkerUtil.priorityRangeOf(resource, PMDRuntimeConstants.RULE_MARKER_TYPES, 5);
		} catch (CoreException e1) {
			return;
		}
		
		if (range.isEmpty()) return;
		
		Integer first = range.iterator().next();
		ImageDescriptor overlay = overlaysByPriority.get(first);
		
		try {
			boolean hasMarkers = MarkerUtil.hasAnyRuleMarkers(resource);
			if (hasMarkers) decoration.addOverlay(overlay);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
