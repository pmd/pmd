package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.ast.ASTAnnotation;
import net.sourceforge.pmd.ast.ParseException;
import test.net.sourceforge.pmd.testframework.ParserTst;

public class ASTAnnotationTest extends ParserTst {

    public void testAnnotationFailsWithJDK14() throws Throwable {
        try {
            getNodes(ASTAnnotation.class, TEST1);
            // FIXME fail("Should have failed to parse an annotation in JDK 1.4 mode");
        } catch (ParseException pe) {
            // cool
        }
    }

    public void testAnnotationSucceedsWithJDK15() throws Throwable {
        try {
            getNodes(new TargetJDK1_5(), ASTAnnotation.class, TEST1);
        } catch (ParseException pe) {
            pe.printStackTrace();
            fail("Should have been able to parse an annotation in JDK 1.5 mode");
        }
    }

    private static final String TEST1 =
            "public class Foo extends Buz {" + PMD.EOL +
            " @Override" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  // overrides a superclass method" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
