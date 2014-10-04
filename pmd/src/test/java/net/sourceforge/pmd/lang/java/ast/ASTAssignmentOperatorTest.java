package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;

public class ASTAssignmentOperatorTest extends ParserTst {

    @Test
    public void testSimpleAssignmentRecognized() throws Throwable {
        Set ops = super.getNodes(ASTAssignmentOperator.class, TEST1);
        assertFalse(((ASTAssignmentOperator) (ops.iterator().next())).isCompound());
    }

    @Test
    public void testCompoundAssignmentPlusRecognized() throws Throwable {
        Set ops = super.getNodes(ASTAssignmentOperator.class, TEST2);
        assertTrue(((ASTAssignmentOperator) (ops.iterator().next())).isCompound());
    }

    @Test
    public void testCompoundAssignmentMultRecognized() throws Throwable {
        Set ops = super.getNodes(ASTAssignmentOperator.class, TEST3);
        assertTrue(((ASTAssignmentOperator) (ops.iterator().next())).isCompound());
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  int x;" + PMD.EOL +
            "  x=2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  int x;" + PMD.EOL +
            "  x += 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  int x;" + PMD.EOL +
            "  x *= 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTAssignmentOperatorTest.class);
    }
}
