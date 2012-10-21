/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.codesize;

/*
 * Note: This class is here in pmd-build to test the RuntimeRulePropertiesAnalyzer
 */

import net.sourceforge.pmd.AbstractPropertySource;

public class NPathComplexityRule extends AbstractPropertySource {

    public NPathComplexityRule() {
	defineProperty("minimum", "The minimum threshold property.", 200.0d);
    }
}
