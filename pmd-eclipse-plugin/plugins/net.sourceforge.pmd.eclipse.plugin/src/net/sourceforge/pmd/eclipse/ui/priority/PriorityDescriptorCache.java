package net.sourceforge.pmd.eclipse.ui.priority;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager;

/**
 * 
 * @author Brian Remedios
 */
public class PriorityDescriptorCache {

	private Map <RulePriority, PriorityDescriptor> uiDescriptorsByPriority;
	
	public static final PriorityDescriptorCache instance = new PriorityDescriptorCache();
	
	private PriorityDescriptorCache() {
		uiDescriptorsByPriority = new HashMap<RulePriority, PriorityDescriptor>(5);
		loadFromPreferences();
	}
	
	private IPreferencesManager preferencesManager() {
		return PMDPlugin.getDefault().getPreferencesManager();
	}
	
	public void loadFromPreferences() {
		
		IPreferences preferences = preferencesManager().loadPreferences();
        for (RulePriority rp : UISettings.currentPriorities(true)) {
        	uiDescriptorsByPriority.put(rp, preferences.getPriorityDescriptor(rp).clone());
        }
	}
	
	public void storeInPreferences() {
		
		IPreferencesManager mgr = preferencesManager();
		
		IPreferences prefs = mgr.loadPreferences();
		
		for (Map.Entry<RulePriority, PriorityDescriptor> entry : uiDescriptorsByPriority.entrySet()) {
			prefs.setPriorityDescriptor(entry.getKey(), entry.getValue());
		}
		
		mgr.storePreferences(prefs);
	}
	
	public PriorityDescriptor descriptorFor(RulePriority priority) {
		return uiDescriptorsByPriority.get(priority);
	}
	
	public boolean hasChanges() {
		
		IPreferences preferences = preferencesManager().loadPreferences();
		
        for (RulePriority rp : UISettings.currentPriorities(true)) {
        	PriorityDescriptor newOne = uiDescriptorsByPriority.get(rp);
        	PriorityDescriptor currentOne = preferences.getPriorityDescriptor(rp);
        	if (newOne.equals(currentOne)) continue;
        	return true;
        }
        return false;
	}
}
