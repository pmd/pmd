/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class NameOccurrenceTest extends TestCase {

    public void testConstructor() {
        SimpleNode node = new ASTPrimaryExpression(1);
        node.testingOnly__setBeginLine(10);
        LocalScope lclScope = new LocalScope();
        node.setScope(lclScope);
        NameOccurrence occ = new NameOccurrence(node, "foo");
        assertEquals("foo", occ.getImage());
        assertTrue(!occ.isThisOrSuper());
        assertEquals(new NameOccurrence(null, "foo"), occ);
        assertEquals(10, occ.getBeginLine());
    }
}
