/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import net.sourceforge.pmd.TestBase;

import org.junit.Test;

/**
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class RuleSetToDocsTest extends TestBase {

    @Test
    public void convertRulesetsTest() throws Exception {
	RuleSetToDocs builder = new RuleSetToDocs();
	builder.setRulesDirectory(TEST_DIR + "rulesets");
	builder.setTargetDirectory(TEST_DIR + "target");

	builder.convertRulesets();
    }
}
