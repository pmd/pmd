/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.settings;

import java.util.List;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface SettingsOwner {


    /**
     * Gets the settings of this specific object.
     *
     * @return The settings
     */
    List<AppSetting> getSettings();


}
