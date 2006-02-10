package test.net.sourceforge.pmd;

import net.sourceforge.pmd.ExcludeLines;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.RuleTst;

import java.io.BufferedReader;
import java.io.StringReader;

public class ExcludeLinesTest extends RuleTst {

    public void testExcludeOne() throws Throwable {
        ExcludeLines e = new ExcludeLines(new StringReader(TEST1));
        assertFalse(e.getLinesToExclude().isEmpty());
        Integer i = (Integer) e.getLinesToExclude().iterator().next();
        assertEquals(3, i.intValue());
    }

    public void testExcludeMultiple() throws Throwable {
        ExcludeLines e = new ExcludeLines(new StringReader(TEST2));
        assertEquals(3, e.getLinesToExclude().size());
        assertTrue(e.getLinesToExclude().contains(new Integer(3)));
        assertTrue(e.getLinesToExclude().contains(new Integer(4)));
        assertTrue(e.getLinesToExclude().contains(new Integer(5)));
    }

    public void testCopyMatches() throws Throwable {
        ExcludeLines e = new ExcludeLines(new StringReader(TEST1));
        BufferedReader br = new BufferedReader(e.getCopyReader());
        StringBuffer copyBuffer = new StringBuffer();
        String tmp;
        while ((tmp = br.readLine()) != null) {
            copyBuffer.append(tmp + PMD.EOL);
        }
        copyBuffer.deleteCharAt(copyBuffer.length() - 1);
        if (PMD.EOL.length() == 2) {
            copyBuffer.deleteCharAt(copyBuffer.length() - 1);
        }
        assertEquals(TEST1, copyBuffer.toString());
    }

    public void testAlternateMarker() throws Throwable {
        ExcludeLines e = new ExcludeLines(new StringReader(TEST4), "FOOBAR");
        assertFalse(e.getLinesToExclude().isEmpty());
    }

    public void testAcceptance() throws Throwable {
        try {
            Rule rule = findRule("rulesets/unusedcode.xml", "UnusedLocalVariable");
            runTestFromString(TEST1, 0, rule);
            runTestFromString(TEST3, 1, rule);
        } catch (Exception e) {
            fail("Acceptance tests failed");
        }
    }


    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  int x; //NOPMD " + PMD.EOL +
            " } " + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  int x; //NOPMD " + PMD.EOL +
            "  int y; //NOPMD " + PMD.EOL +
            "  int z; //NOPMD " + PMD.EOL +
            " } " + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  int x;" + PMD.EOL +
            " } " + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  int x; // FOOBAR" + PMD.EOL +
            " } " + PMD.EOL +
            "}";

}
