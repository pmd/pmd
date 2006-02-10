package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseStringBufferLengthTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("rulesets/strings.xml", "UseStringBufferLength");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "Using length properly", 0, rule),
            new TestDescriptor(TEST2, "StringBuffer.toString.equals(\"\"), bad", 1, rule),
            new TestDescriptor(TEST3, "StringBuffer.toString.equals(\"foo\"), ok", 0, rule),
            new TestDescriptor(TEST4, "StringBuffer.toString.length(), bad", 1, rule),
            new TestDescriptor(TEST5, "no literals", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "void bar() {" + PMD.EOL +
            "    StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "     if ( sb.length() == 0 ){" + PMD.EOL +
            "           sb.append(\",\");" + PMD.EOL +
            "     }" + PMD.EOL +
            "    sb.append( \"whatever\" );" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "void bar() {" + PMD.EOL +
            "    StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "     if ( sb.toString().equals(\"\") ){" + PMD.EOL +
            "           sb.append(\",\");" + PMD.EOL +
            "     }" + PMD.EOL +
            "    sb.append( \"whatever\" );" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            "void bar() {" + PMD.EOL +
            "    StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "     if ( sb.toString().equals(\"foo\") ){" + PMD.EOL +
            "           sb.append(\",\");" + PMD.EOL +
            "     }" + PMD.EOL +
            "    sb.append( \"whatever\" );" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            "void bar() {" + PMD.EOL +
            "    StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "     if ( sb.toString().length() == 0 ){" + PMD.EOL +
            "           sb.append(\",\");" + PMD.EOL +
            "     }" + PMD.EOL +
            "    sb.append( \"whatever\" );" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " boolean bar(Object foo) {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  return sb.toString().equals(foo);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
