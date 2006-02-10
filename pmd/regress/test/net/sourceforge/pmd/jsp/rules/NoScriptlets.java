package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.SourceType;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NoScriptlets extends SimpleAggregatorTst {

    public void testAll() throws RuleSetNotFoundException {
        Rule rule = new RuleSetFactory()
                .createSingleRuleSet("rulesets/basic-jsp.xml").getRuleByName("NoScriptlets");
        runTests(new TestDescriptor[]{
            new TestDescriptor(VIOLATION1, "Two scriptlets.", 2, rule),
            new TestDescriptor(NO_VIOLATION1, "No scriptlets.", 0, rule),
        }, SourceType.JSP);
    }

    private static final String VIOLATION1 =
            "<HTML>" +
            "<HEAD>" +
            "<% response.setHeader(\"Pragma\", \"No-cache\"); %>" +
            "</HEAD>" +
            "<BODY>" +
            "	<jsp:scriptlet>String title = \"Hello world!\";</jsp:scriptlet>" +
            "</BODY>" +
            "</HTML>";


    private static final String NO_VIOLATION1 =
            "<html><body><p>text</p></body></html>";
}
