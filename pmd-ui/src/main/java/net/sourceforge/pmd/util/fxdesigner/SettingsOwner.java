/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.util.Map;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface SettingsOwner {


    /**
     * Puts the owned settings into the accumulator.
     *
     * @param saver Accumulator
     */
    void saveSettings(SettingsAccumulator saver);


    /**
     * Loads the settings.
     *
     * @param settings Settings
     */
    void loadSettings(Map<String, String> settings);


    interface SettingsAccumulator {

        /** Adds a setting key-value pair. */
        SettingsAccumulator put(String key, String value);

    }

}
