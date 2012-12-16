/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;

import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

public class EcmascriptParserTest {

    @Test
    public void testLineNumbers() {
	
	EcmascriptParser parser = new EcmascriptParser(new EcmascriptParserOptions());
	Reader sourceCode = new StringReader(SOURCE_CODE);
	EcmascriptNode<AstRoot> node = parser.parse(sourceCode);

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

    private static final String SOURCE_CODE =
	    "function a() {" + PMD.EOL
	  + "  alert('hello');" + PMD.EOL
	  + "}" + PMD.EOL;
}
