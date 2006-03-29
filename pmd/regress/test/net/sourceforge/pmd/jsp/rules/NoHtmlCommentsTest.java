package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.SourceType;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NoHtmlCommentsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("jsp", "NoHtmlComments");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1_OK, "No HTML comments", 0, rule),
            new TestDescriptor(TEST2_FAIL, "HTML Comment", 1, rule),
            new TestDescriptor(TEST3_OK, "JSP Comments", 0, rule),
        }, SourceType.JSP);
    }

    private static final String TEST1_OK =
            "<html><body></body></html>";

    private static final String TEST2_FAIL =
        "<html><body><!-- HTML Comment --></body></html>";

    private static final String TEST3_OK =
        "<html><body><%-- JSP Comment --%></body></html>";
}
