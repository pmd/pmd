package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class ASTPrimarySuffixTest extends ParserTst {

    public void testArrayDereference() throws Throwable {
        Set ops = getNodes(ASTPrimarySuffix.class, TEST1);
        assertTrue(((ASTPrimarySuffix) (ops.iterator().next())).isArrayDereference());
    }

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


}
