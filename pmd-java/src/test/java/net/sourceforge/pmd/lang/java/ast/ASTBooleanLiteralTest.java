/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class ASTBooleanLiteralTest {

    @Test
    public void testTrue() {
        Set<ASTBooleanLiteral> ops = getNodes(ASTBooleanLiteral.class, TEST1);
        ASTBooleanLiteral b = ops.iterator().next();
        assertTrue(b.isTrue());
    }

    @Test
    public void testFalse() {
        Set<ASTBooleanLiteral> ops = getNodes(ASTBooleanLiteral.class, TEST2);
        ASTBooleanLiteral b = ops.iterator().next();
        assertFalse(b.isTrue());
    }

    private static final String TEST1 = "class Foo { " + PMD.EOL + " boolean bar = true; " + PMD.EOL + "} ";

    private static final String TEST2 = "class Foo { " + PMD.EOL + " boolean bar = false; " + PMD.EOL + "} ";
}
