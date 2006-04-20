package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.jsp.rules.DuplicateJspImports;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class DuplicateJspImportTest extends SimpleAggregatorTst {

    public void testAll() {
        
        Rule rule = new DuplicateJspImports();
        rule.setMessage("");
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST1_OK, "Just 1 import", 0, rule),
                new TestDescriptor(TEST2_FAIL, "Duplicate imports", 1, rule),
                new TestDescriptor(TEST3_FAIL, "Duplicate imports", 1, rule),
                new TestDescriptor(TEST4_FAIL, "Duplicate imports", 1, rule),
                new TestDescriptor(TEST5_FAIL, "Duplicate imports", 2, rule),
                new TestDescriptor(TEST6_OK, "Just 1 import", 0, rule),
        }, SourceType.JSP);
    }

    private static final String TEST1_OK =
        "<%@ page import=\"com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>";
    private static final String TEST2_FAIL =
        "<%@ page import=\"com.foo.MyClass,com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>";
    private static final String TEST3_FAIL =
        "<%@ page import=\"com.foo.MyClass\"%><%@ page import=\"com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>";
    private static final String TEST4_FAIL =
        "<%@ page import=\"com.foo.MyClass,com.foo.AClass\"%><%@ page import=\"com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>";
    private static final String TEST5_FAIL =
        "<%@ page import=\"com.foo.MyClass,com.foo.MyClass\"%><%@ page import=\"com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>";
    private static final String TEST6_OK =
        "<%@ page import=\"com.foo.AClass\"%><%@ page import=\"com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>";
}
