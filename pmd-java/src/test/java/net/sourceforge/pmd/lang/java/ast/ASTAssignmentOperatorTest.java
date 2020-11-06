/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTAssignmentOperatorTest extends BaseParserTest {

    @Test
    public void testSimpleAssignmentRecognized() {
        List<ASTAssignmentOperator> ops = java.getNodes(ASTAssignmentOperator.class, TEST1);
        assertFalse(ops.get(0).isCompound());
    }

    @Test
    public void testCompoundAssignmentPlusRecognized() {
        List<ASTAssignmentOperator> ops = java.getNodes(ASTAssignmentOperator.class, TEST2);
        assertTrue(ops.get(0).isCompound());
    }

    @Test
    public void testCompoundAssignmentMultRecognized() {
        List<ASTAssignmentOperator> ops = java.getNodes(ASTAssignmentOperator.class, TEST3);
        assertTrue(ops.get(0).isCompound());
    }

    private static final String TEST1 = "public class Foo {\n void bar() {\n  int x;\n  x=2;\n }\n}";

    private static final String TEST2 = "public class Foo {\n void bar() {\n  int x;\n  x += 2;\n }\n}";

    private static final String TEST3 = "public class Foo {\n void bar() {\n  int x;\n  x *= 2;\n }\n}";
}
