package net.sourceforge.pmd.eclipse;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * This class is used to initialize default preferences
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2004/06/29 22:00:30  phherlin
 * Adapting the plugin to the new OSGi standards
 *
 */ 
public class PluginPreferenceInitializer extends AbstractPreferenceInitializer implements PMDPluginConstants {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
        PMDPlugin.getDefault().getPluginPreferences().setDefault(RULESET_PREFERENCE, RULESET_DEFAULT);
        PMDPlugin.getDefault().getPluginPreferences().setDefault(MIN_TILE_SIZE_PREFERENCE, MIN_TILE_SIZE_DEFAULT);		

	}

}
