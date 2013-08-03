/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;

import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

public class EcmascriptParserTest {

    private EcmascriptNode<AstRoot> parse(String code) {
        EcmascriptParser parser = new EcmascriptParser(new EcmascriptParserOptions());
        Reader sourceCode = new StringReader(code);
        return parser.parse(sourceCode);
    }

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

    private static final String SOURCE_CODE =
	    "function a() {" + PMD.EOL
	  + "  alert('hello');" + PMD.EOL
	  + "}" + PMD.EOL;
}
