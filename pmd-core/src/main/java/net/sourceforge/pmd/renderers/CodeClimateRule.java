/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

import static net.sourceforge.pmd.renderers.CodeClimateRenderer.REMEDIATION_POINTS_DEFAULT;

/**
 * This interface tags a Rule specifying properties required for the Code Climate output format
 */
public interface CodeClimateRule extends Rule {
	StringMultiProperty CODECLIMATE_CATEGORIES = new StringMultiProperty("categories",
            												 "Issue categories", 
            												 new String[] { "Style" }, 
            												 1.0f, 
            												 ',');

	// Note: We use a multiplier to the Code Climate default of 50000 for the simplest possible remediation 
	IntegerProperty CODECLIMATE_REMEDIATION_MULTIPLIER = new IntegerProperty("remediation_points_multiplier", "Remediation points multiplier", 
																		Integer.MIN_VALUE, 
																		Integer.MAX_VALUE / REMEDIATION_POINTS_DEFAULT, 
																		1, 
																		1.0f);
}
