package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.SourceType;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

/**
 * Test the "NoJspForward" rule.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class NoJspForwardTest extends SimpleAggregatorTst {

    public void testViolation() throws Exception {
        Rule rule = new RuleSetFactory().createSingleRuleSet("rulesets/basic-jsp.xml").getRuleByName("NoJspForward");
        runTests(new TestDescriptor[]{
            new TestDescriptor(VIOLATION, "A violation.", 1, rule)
        }, SourceType.JSP);
    }


    private static final String VIOLATION
            = "<jsp:forward page='UnderConstruction.jsp'/>";
}
