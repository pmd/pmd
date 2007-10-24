package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.ast.ASTAnnotation;
import net.sourceforge.pmd.ast.ParseException;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

public class ASTAnnotationTest extends ParserTst {

    @Test
    public void testAnnotationSucceedsWithDefaultMode() throws Throwable {
        getNodes(ASTAnnotation.class, TEST1);
    }

    @Test(expected = ParseException.class)
    public void testAnnotationFailsWithJDK14() throws Throwable {
        getNodes(new TargetJDK1_4(), ASTAnnotation.class, TEST1);
    }

    @Test
    public void testAnnotationSucceedsWithJDK15() throws Throwable {
        getNodes(new TargetJDK1_5(), ASTAnnotation.class, TEST1);
    }

    private static final String TEST1 =
            "public class Foo extends Buz {" + PMD.EOL +
            " @Override" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  // overrides a superclass method" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTAnnotationTest.class);
    }
}
