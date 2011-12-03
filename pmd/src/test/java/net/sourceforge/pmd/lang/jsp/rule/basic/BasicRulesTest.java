/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.rule.basic;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class BasicRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "jsp-basic";

    @Before
    public void setUp() {
	addRule(RULESET, "DuplicateJspImports");
	addRule(RULESET, "IframeMissingSrcAttribute");
	addRule(RULESET, "JspEncoding");
	addRule(RULESET, "NoClassAttribute");
	addRule(RULESET, "NoHtmlComments");
	addRule(RULESET, "NoInlineScript");
	addRule(RULESET, "NoInlineStyleInformation");
	addRule(RULESET, "NoJspForward");
	addRule(RULESET, "NoLongScripts");
	addRule(RULESET, "NoScriptlets");
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }
}
