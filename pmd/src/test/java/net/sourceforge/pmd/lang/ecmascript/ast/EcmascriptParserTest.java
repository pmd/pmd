/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.Ecmascript3Parser;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;

import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

public class EcmascriptParserTest extends EcmascriptParserTestBase {

    /**
     * https://sourceforge.net/p/pmd/bugs/1043/
     */
    @Test
    public void testLineNumbers() {
        String SOURCE_CODE =
                "function a() {" + PMD.EOL
              + "  alert('hello');" + PMD.EOL
              + "}" + PMD.EOL;
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
     * https://sourceforge.net/p/pmd/bugs/1149/
     */
    @Test
    public void testLineNumbersWithinEcmascriptRules() {
        String source =
                "function f(x){\n" + 
                "   if (x) {\n" + 
                "       return 1;\n" + 
                "   } else {\n" + 
                "       return 0;\n" + 
                "   }\n" + 
                "}";
        final List<String> output = new ArrayList<String>();

        class MyEcmascriptRule extends AbstractEcmascriptRule {
            public Object visit(ASTScope node, Object data) {
                output.add("Scope from " + node.getBeginLine() + " to " + node.getEndLine());
                return super.visit(node, data);
            }
        }

        MyEcmascriptRule rule = new MyEcmascriptRule();
        RuleContext ctx = new RuleContext();
        rule.apply(Arrays.asList(parse(source)), ctx);

        assertEquals("Scope from 2 to 4", output.get(0));
        assertEquals("Scope from 4 to 6", output.get(1));
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

    /**
     * https://sourceforge.net/p/pmd/bugs/1150/
     * #1150 "EmptyExpression" for valid statements!
     */
    @Test
    public void testCaseAsIdentifier() {
        ASTAstRoot rootNode = parse("function f(a){\n" + 
                "    a.case.flag = 1;\n" + 
                "    return;\n" + 
                "}");
        ASTBlock block = rootNode.getFirstDescendantOfType(ASTBlock.class);
        assertFalse(block.jjtGetChild(0) instanceof ASTEmptyExpression);
        assertTrue(block.jjtGetChild(0) instanceof ASTExpressionStatement);
        assertTrue(block.jjtGetChild(0).jjtGetChild(0) instanceof ASTAssignment);
    }

    /**
     * https://sourceforge.net/p/pmd/bugs/1045/
     * #1045 //NOPMD not working (or not implemented) with ECMAscript 
     */
    @Test
    public void testSuppresionComment() {
        Ecmascript3Parser parser = new Ecmascript3Parser(new EcmascriptParserOptions());
        Reader sourceCode = new StringReader("function(x) {\n"
                + "x = x; //NOPMD I know what I'm doing\n"
                + "}\n");
        parser.parse("foo", sourceCode);
        assertEquals(" I know what I'm doing", parser.getSuppressMap().get(2));
        assertEquals(1, parser.getSuppressMap().size());

        EcmascriptParserOptions parserOptions = new EcmascriptParserOptions();
        parserOptions.setSuppressMarker("FOOOO");
        parser = new Ecmascript3Parser(parserOptions);
        sourceCode = new StringReader("function(x) {\n"
                + "y = y; //NOPMD xyz\n"
                + "x = x; //FOOOO I know what I'm doing\n"
                + "}\n");
        parser.parse("foo", sourceCode);
        assertEquals(" I know what I'm doing", parser.getSuppressMap().get(3));
        assertEquals(1, parser.getSuppressMap().size());
    }
}
