package net.sourceforge.pmd.ast;

import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.testframework.ParserTst;

import org.junit.Test;


import java.util.Set;

public class ASTPrimarySuffixTest extends ParserTst {

    @Test
    public void testArrayDereference() throws Throwable {
        Set ops = getNodes(ASTPrimarySuffix.class, TEST1);
        assertTrue(((ASTPrimarySuffix) (ops.iterator().next())).isArrayDereference());
    }

    @Test
    public void testArguments() throws Throwable {
        Set ops = getNodes(ASTPrimarySuffix.class, TEST2);
        assertTrue(((ASTPrimarySuffix) (ops.iterator().next())).isArguments());
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "  {x[0] = 2;}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "  {foo(a);}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTPrimarySuffixTest.class);
    }
}
