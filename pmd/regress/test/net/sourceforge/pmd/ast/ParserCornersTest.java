package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

public class ParserCornersTest extends ParserTst {

    @Test
    public final void testGetFirstASTNameImageNull() throws Throwable {
        parseJava14(ABSTRACT_METHOD_LEVEL_CLASS_DECL);
    }

    private static final String ABSTRACT_METHOD_LEVEL_CLASS_DECL =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   abstract class X { public abstract void f(); }" + PMD.EOL +
            "   class Y extends X { public void f() {" + PMD.EOL +
            "    new Y().f();" + PMD.EOL +
            "   }}" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ParserCornersTest.class);
    }
}
