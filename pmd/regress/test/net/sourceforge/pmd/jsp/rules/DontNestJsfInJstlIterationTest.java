package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.SourceType;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

/**
 * Test the "DontNestJsfInJstlIteration" rule.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class DontNestJsfInJstlIterationTest extends SimpleAggregatorTst {

    public void testViolation() throws Exception {
        Rule rule = new RuleSetFactory()
                .createSingleRuleSet("rulesets/basic-jsf.xml").getRuleByName("DontNestJsfInJstlIteration");

        runTests(new TestDescriptor[]{
            new TestDescriptor(VIOLATION, "A violation.", 1, rule)
        }, SourceType.JSP);
    }

    private static final String VIOLATION
            = "<html> <body> <ul> <c:forEach items='${books}' var='b'>"
            + "<li> <h:outputText value='#{b}' /> </li>"
            + "</c:forEach> </ul> </body> </html>";
}
