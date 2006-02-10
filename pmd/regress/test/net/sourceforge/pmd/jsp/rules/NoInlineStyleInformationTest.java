package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.jsp.rules.NoInlineStyleInformation;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NoInlineStyleInformationTest extends SimpleAggregatorTst {

    public void testAll() {
        Rule rule = new NoInlineStyleInformation();
        runTests(new TestDescriptor[]{
            new TestDescriptor(JSP_VIOLATION1, "A <B> element.", 1, rule),
            new TestDescriptor(JSP_VIOLATION2, "A font and align attribute.", 2, rule),
            new TestDescriptor(JSP_NO_VIOLATION1, "No violations.", 0, rule),
        }, SourceType.JSP);
    }

    private static final String JSP_VIOLATION1 =
            "<html><body><b>text</b></body></html>";

    private static final String JSP_VIOLATION2 =
            "<html><body><p font='arial' align='center'>text</p></body></html>";

    private static final String JSP_NO_VIOLATION1 =
            "<html><body><p>text</p></body></html>";
}
