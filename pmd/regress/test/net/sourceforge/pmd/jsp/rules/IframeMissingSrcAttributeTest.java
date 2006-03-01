package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.SourceType;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class IframeMissingSrcAttributeTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("jsp", "IframeMissingSrcAttribute");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1_OK, "1, iframe exists with src attribute", 0, rule),
            new TestDescriptor(TEST2_FAIL, "2, iframe is missing src attribute", 1, rule),
            new TestDescriptor(TEST3_FAIL, "3, IFRAME is missing src attribute", 1, rule),
        }, SourceType.JSP);
    }

    private static final String TEST1_OK =
            "<html><body><iframe src=\"foo.html\"></iframe></body></html>";

    private static final String TEST2_FAIL =
        "<html><body><iframe></iframe></body></html>";

    private static final String TEST3_FAIL =
        "<html><body><IFRAME></IFRAME></body></html>";
}
