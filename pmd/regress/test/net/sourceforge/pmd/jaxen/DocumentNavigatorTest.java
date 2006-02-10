/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.UnsupportedAxisException;
import test.net.sourceforge.pmd.testframework.RuleTst;

import java.util.Iterator;
import java.util.List;

public class DocumentNavigatorTest extends RuleTst {


    private TestRule rule;

    private class TestRule extends AbstractRule {

        private Node compilationUnit;
        private Node importDeclaration;
        private Node statement;
        private Node primaryPrefix;
        private Node primaryExpression;

        /**
         * @see net.sourceforge.pmd.ast.JavaParserVisitor#visit(ASTCompilationUnit, Object)
         */
        public Object visit(ASTCompilationUnit node, Object data) {
            this.compilationUnit = node;
            return super.visit(node, data);
        }

        public Object visit(ASTImportDeclaration node, Object data) {
            this.importDeclaration = node;
            return super.visit(node, data);
        }

        public Object visit(ASTStatement node, Object data) {
            this.statement = node;
            return super.visit(node, data);
        }

        public Object visit(ASTPrimaryPrefix node, Object data) {
            this.primaryPrefix = node;
            return super.visit(node, data);
        }

        public Object visit(ASTPrimaryExpression node, Object data) {
            this.primaryExpression = node;
            return super.visit(node, data);
        }
    }

    public void setUp() throws Exception {
        try {
            rule = new TestRule();
            runTestFromString(TEST, rule, new Report());
        } catch (Throwable xx) {
            xx.printStackTrace();
            fail();
        }
    }

    public void testChildAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getChildAxisIterator(rule.compilationUnit);
        assertSame(rule.compilationUnit.jjtGetChild(0), iter.next());
        assertSame(rule.compilationUnit.jjtGetChild(1), iter.next());
        assertFalse(iter.hasNext());
    }

    public void testParentAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getParentAxisIterator(rule.importDeclaration);
        assertSame(rule.importDeclaration.jjtGetParent(), iter.next());
        assertFalse(iter.hasNext());
    }

    public void testParentAxisIterator2() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getParentAxisIterator(rule.compilationUnit);
        assertFalse(iter.hasNext());
    }

    public void testDescendantAxisIterator() throws UnsupportedAxisException {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getDescendantAxisIterator(rule.statement);
        Node statementExpression = rule.statement.jjtGetChild(0);
        assertSame(statementExpression, iter.next());
        Node primaryExpression = statementExpression.jjtGetChild(0);
        assertSame(primaryExpression, iter.next());
        Node primaryPrefix = primaryExpression.jjtGetChild(0);
        assertSame(primaryPrefix, iter.next());
        Node primarySuffix = primaryExpression.jjtGetChild(1);
//        assertSame(primarySuffix, iter.next());
        Node name = primaryPrefix.jjtGetChild(0);
//        assertSame(name, iter.next());
        Node arguments = primarySuffix.jjtGetChild(0);
//        assertSame(arguments, iter.next());
//        assertFalse(iter.hasNext());
    }

    public void testDescendantAxisIterator2() throws UnsupportedAxisException {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getDescendantAxisIterator(rule.primaryPrefix);
        Node name = rule.primaryPrefix.jjtGetChild(0);
        assertSame(name, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testFollowingSiblingAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getFollowingSiblingAxisIterator(rule.primaryExpression.jjtGetChild(0));
        assertSame(rule.primaryExpression.jjtGetChild(1), iter.next());
        assertFalse(iter.hasNext());
    }

    public void testFollowingSiblingAxisIterator2() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getFollowingSiblingAxisIterator(rule.primaryExpression.jjtGetChild(1));
        assertFalse(iter.hasNext());
    }

    public void testPrecedingSiblingAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getPrecedingSiblingAxisIterator(rule.primaryExpression.jjtGetChild(1));
        assertSame(rule.primaryExpression.jjtGetChild(0), iter.next());
        assertFalse(iter.hasNext());
    }

    public void testPrecedingSiblingAxisIterator2() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter = nav.getPrecedingSiblingAxisIterator(rule.primaryExpression.jjtGetChild(0));
        assertFalse(iter.hasNext());
    }

    public void testXPath() throws JaxenException {
        BaseXPath xPath = new BaseXPath(".//*", new DocumentNavigator());
        List matches = xPath.selectNodes(rule.statement);
        assertEquals(6, matches.size());
    }

    public void testXPath2() throws JaxenException {
        BaseXPath xPath = new BaseXPath(".//*", new DocumentNavigator());
        List matches = xPath.selectNodes(rule.importDeclaration);
        assertEquals(1, matches.size());
    }


    public static final String TEST =
            "import java.io.*;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "  try {" + PMD.EOL +
            "   FileReader fr = new FileReader(\"/dev/null\");" + PMD.EOL +
            "  } catch (Exception e) {}" + PMD.EOL +
            "  try {" + PMD.EOL +
            "   FileReader fr = new FileReader(\"/dev/null\");" + PMD.EOL +
            "  } catch (Exception e) {" + PMD.EOL +
            "   e.printStackTrace();" + PMD.EOL +
            "   // this shouldn't show up on the report" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
