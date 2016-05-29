/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import static net.sourceforge.pmd.renderers.CodeClimateRenderer.REMEDIATION_POINTS_DEFAULT;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;

/**
 * This interface tags a Rule specifying properties required for the Code
 * Climate output format. Each rule implementing this interface must
 * define the two code climate properties "categories" and "remediation multiplier".
 */
public interface CodeClimateRule extends Rule {
    /**
     * Defines the code climate categories for which this rule will find violations.
     * Possible categories are: Bug Risk, Clarity, Compatibility, Complexity, Duplication,
     * Performance, Security, Style.
     *
     * @see <a href="https://github.com/codeclimate/spec/blob/master/SPEC.md#categories">Code Climate Spec</a>
     */
    EnumeratedMultiProperty<String> CODECLIMATE_CATEGORIES = new EnumeratedMultiProperty<String>(
            "cc_categories", "Code Climate Categories",
            new String[] { "Bug Risk", "Clarity", "Compatibility", "Complexity", "Duplication", "Performance", "Security", "Style" },
            new String[] { "Bug Risk", "Clarity", "Compatibility", "Complexity", "Duplication", "Performance", "Security", "Style" },
            new int[] { 7 }, 1.0f);

    /**
     * Defines the remediation points for this rule. The remediation points are not set directly but are expressed as
     * a multiplier. The {@link CodeClimateRenderer} takes this and multiplies it with {@link CodeClimateRenderer#REMEDIATION_POINTS_DEFAULT},
     * which is the baseline points for a trivial fix (value is 50000).
     *
     * @see CodeClimateRenderer#REMEDIATION_POINTS_DEFAULT
     */
    // Note: We use a multiplier to the Code Climate default of 50000 for the
    // simplest possible remediation
    IntegerProperty CODECLIMATE_REMEDIATION_MULTIPLIER = new IntegerProperty("cc_remediation_points_multiplier",
            "Code Climate Remediation Points multiplier", Integer.MIN_VALUE,
            Integer.MAX_VALUE / REMEDIATION_POINTS_DEFAULT, 1, 1.0f);
    
    /**
     * Defines if the whole related block or just the first line of the issue should be highlighted at the Code Climate Platform.
     * By default the block highlighting is disabled for reasons of clarity.
     */
    BooleanProperty CODECLIMATE_BLOCK_HIGHLIGHTING = new BooleanProperty("cc_block_highlighting", 
    		"Code Climate Block Highlighting", false, 1.0f);
}
