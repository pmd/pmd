package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.io.StringReader;

public class ParserCornersTest extends ParserTst {

    @Test
    public final void testGetFirstASTNameImageNull() throws Throwable {
        new TargetJDK1_4().createParser(new StringReader(ABSTRACT_METHOD_LEVEL_CLASS_DECL)).CompilationUnit();
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
