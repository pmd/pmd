/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static net.sourceforge.pmd.renderers.CodeClimateRenderer.REMEDIATION_POINTS_DEFAULT;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 * This interface tags a Rule specifying properties required for the Code
 * Climate output format. Each rule implementing this interface must define the
 * two code climate properties "categories" and "remediation multiplier".
 *
 * @deprecated: This will be remove in 7.0.x (see the PR #1702)
 */
@Deprecated
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

    // these properties may not be updated yet

    /**
     * Defines the code climate categories for which this rule will find
     * violations. Possible categories are: Bug Risk, Clarity, Compatibility,
     * Complexity, Duplication, Performance, Security, Style.
     *
     * @see <a href=
     *      "https://github.com/codeclimate/spec/blob/master/SPEC.md#categories">Code
     *      Climate Spec</a>
     */
    EnumeratedMultiProperty<String> CODECLIMATE_CATEGORIES // better would be to use CodeClimateCategory as values but might break the API
        = new EnumeratedMultiProperty<>("cc_categories",
                                        "deprecated! Code Climate Categories",
                                        CodeClimateCategory.categoryMap(),
                                        Collections.singletonList(CodeClimateCategory.STYLE.name),
                                        String.class, 1.0f);

    /**
     * Defines the remediation points for this rule. The remediation points are
     * not set directly but are expressed as a multiplier. The
     * {@link CodeClimateRenderer} takes this and multiplies it with
     * {@link CodeClimateRenderer#REMEDIATION_POINTS_DEFAULT}, which is the
     * baseline points for a trivial fix (value is 50000).
     *
     * @see CodeClimateRenderer#REMEDIATION_POINTS_DEFAULT
     */
    // Note: We use a multiplier to the Code Climate default of 50000 for the
    // simplest possible remediation
    IntegerProperty CODECLIMATE_REMEDIATION_MULTIPLIER = new IntegerProperty("cc_remediation_points_multiplier",
            "deprecated! Code Climate Remediation Points multiplier", Integer.MIN_VALUE,
            Integer.MAX_VALUE / REMEDIATION_POINTS_DEFAULT, 1, 1.0f);

    /**
     * Defines if the whole related block or just the first line of the issue
     * should be highlighted at the Code Climate Platform. By default the block
     * highlighting is disabled for reasons of clarity.
     */
    BooleanProperty CODECLIMATE_BLOCK_HIGHLIGHTING = new BooleanProperty("cc_block_highlighting",
            "deprecated! Code Climate Block Highlighting", false, 1.0f);
}
