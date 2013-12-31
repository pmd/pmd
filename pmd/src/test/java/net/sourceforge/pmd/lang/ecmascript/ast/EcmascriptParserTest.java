/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Node;

import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

public class EcmascriptParserTest extends EcmascriptParserTestBase {

    @Test
    public void testLineNumbers() {
    EcmascriptNode<AstRoot> node = parse(SOURCE_CODE);
	assertEquals(1, node.getBeginLine());
	assertEquals(1, node.getBeginColumn());
	assertEquals(3, node.getEndLine());
	assertEquals(1, node.getEndColumn());

	Node child = node.getFirstChildOfType(ASTFunctionNode.class);
	assertEquals(1, child.getBeginLine());
	assertEquals(1, child.getBeginColumn());
	assertEquals(3, child.getEndLine());
	assertEquals(1, child.getEndColumn());

	child = node.getFirstDescendantOfType(ASTFunctionCall.class);
	assertEquals(2, child.getBeginLine());
	assertEquals(3, child.getBeginColumn());
	assertEquals(2, child.getEndLine());
	assertEquals(16, child.getEndColumn());
    }

    /**
     * Test bug https://sourceforge.net/p/pmd/bugs/1118/
     */
    @Test
    public void testArrayAccess() {
        EcmascriptNode<AstRoot> node = parse("function a() { b['a'] = 1; c[1] = 2; }");
        List<ASTElementGet> arrays = node.findDescendantsOfType(ASTElementGet.class);
        assertEquals("b", arrays.get(0).getTarget().getImage());
        assertEquals("a", arrays.get(0).getElement().getImage());
        assertEquals("c", arrays.get(1).getTarget().getImage());
        assertEquals("1", arrays.get(1).getElement().getImage());
    }

    /**
     * Test for bug #1136 ECAMScript: NullPointerException in getLeft() and getRight()
     */
    @Test
    public void testArrayMethod() {
        EcmascriptNode<AstRoot> rootNode = parse("function test(){\n" + 
                "  a();      // OK\n" + 
                "  b.c();    // OK\n" + 
                "  d[0]();   // OK\n" + 
                "  e[0].f(); // OK\n" + 
                "  y.z[0](); // FAIL ==> java.lang.NullPointerException\n" + 
                "}");

        List<ASTFunctionCall> calls = rootNode.findDescendantsOfType(ASTFunctionCall.class);
        List<String> results = new ArrayList<String>();
        for (ASTFunctionCall f : calls) {
            Node node = f.getTarget();
            results.add(getName(node));
        }
        assertEquals("[a, b.c, d[], e[].f, y.z[]]", results.toString());
    }

    private String getName(Node node) {
        if( node instanceof ASTName ){
            return ((ASTName)node).getIdentifier();
        }
        if( node instanceof ASTPropertyGet ){
            final ASTPropertyGet pgNode = (ASTPropertyGet)node;
            final String leftName  = getName(pgNode.getLeft());
            final String rightName = getName(pgNode.getRight());
            return leftName + "." + rightName;
        }
        if( node instanceof ASTElementGet ){
            return getName(((ASTElementGet)node).getTarget()) + "[]";
        }
        return "????";
    }

    private static final String SOURCE_CODE =
	    "function a() {" + PMD.EOL
	  + "  alert('hello');" + PMD.EOL
	  + "}" + PMD.EOL;
}
