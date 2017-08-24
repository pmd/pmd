/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ASTBlockStatementTest {

    @Test
    public void testIsAllocation() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAllocationExpression(1), 0);
        assertTrue(bs.isAllocation());
    }

    @Test
    public void testIsAllocation2() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAssertStatement(1), 0);
        assertFalse(bs.isAllocation());
    }
}
