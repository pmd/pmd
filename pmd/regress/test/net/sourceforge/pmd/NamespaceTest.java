/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 2:21:44 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.Namespace;
import net.sourceforge.pmd.SymbolTable;

public class NamespaceTest extends TestCase{
    public NamespaceTest(String name) {
        super(name);
    }

    public void testBasic() {
        Namespace nameSpace = new Namespace();
        nameSpace.addTable();
        assertEquals(1, nameSpace.size());
        nameSpace.removeTable();
        assertEquals(0, nameSpace.size());
    }

    public void testParent() {
        Namespace nameSpace = new Namespace();
        nameSpace.addTable();
        SymbolTable parent = nameSpace.peek();
        nameSpace.addTable();
        assertEquals(parent, nameSpace.peek().getParent());
    }
}
