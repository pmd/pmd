package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;

public class ASTBooleanLiteralTest extends TestCase {
    public void testTrue() {
        ASTBooleanLiteral b = new ASTBooleanLiteral(0);
        assertFalse(b.isTrue());
        b.setTrue();
        assertTrue(b.isTrue());
    }
}
