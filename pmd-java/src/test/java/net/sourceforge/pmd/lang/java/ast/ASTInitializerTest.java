package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;


public class ASTInitializerTest extends ParserTst {

    @Test
    public void testDontCrashOnBlockStatement() throws Throwable {
        getNodes(ASTInitializer.class, TEST1);
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " {" + PMD.EOL +
            "   x = 5;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTInitializerTest.class);
    }
}
