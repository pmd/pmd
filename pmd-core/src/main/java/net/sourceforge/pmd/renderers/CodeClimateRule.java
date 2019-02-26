/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.Rule;

/**
 * This interface tags a Rule specifying properties required for the Code
 * Climate output format. Each rule implementing this interface must define the
 * two code climate properties "categories" and "remediation multiplier".
 */
public interface CodeClimateRule extends Rule {

    /** Represent a CodeClimate category. */
    enum CodeClimateCategory {
        BUG_RISK("Bug Risk"),
        CLARITY("Clarity"),
        COMPATIBILITY("Compatibility"),
        COMPLEXITY("Complexity"),
        DUPLICATION("Duplication"),
        PERFORMANCE("Performance"),
        SECURITY("Security"),
        STYLE("Style");

        private String name;

        CodeClimateCategory(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        /** Makes a map to define the categories for use in the descriptor. */
        private static Map<String, String> categoryMap() {
            Map<String, String> result = new HashMap<>();
            for (CodeClimateCategory cat : values()) {
                result.put(cat.name, cat.name);
            }
            return result;
        }
    }
}
