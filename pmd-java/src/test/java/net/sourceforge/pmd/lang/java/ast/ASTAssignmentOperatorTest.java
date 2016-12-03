/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

public class ASTAssignmentOperatorTest extends ParserTst {

    @Test
    public void testSimpleAssignmentRecognized() {
        Set<ASTAssignmentOperator> ops = super.getNodes(ASTAssignmentOperator.class, TEST1);
        assertFalse((ops.iterator().next()).isCompound());
    }

    @Test
    public void testCompoundAssignmentPlusRecognized() {
        Set<ASTAssignmentOperator> ops = super.getNodes(ASTAssignmentOperator.class, TEST2);
        assertTrue((ops.iterator().next()).isCompound());
    }

    @Test
    public void testCompoundAssignmentMultRecognized() {
        Set<ASTAssignmentOperator> ops = super.getNodes(ASTAssignmentOperator.class, TEST3);
        assertTrue((ops.iterator().next()).isCompound());
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  int x;"
            + PMD.EOL + "  x=2;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  int x;"
            + PMD.EOL + "  x += 2;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST3 = "public class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  int x;"
            + PMD.EOL + "  x *= 2;" + PMD.EOL + " }" + PMD.EOL + "}";
}
