/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.jaxen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.UnsupportedAxisException;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.testframework.RuleTst;

public class DocumentNavigatorTest extends RuleTst {

    private TestRule rule;

    private class TestRule extends AbstractJavaRule {

        private Node compilationUnit;
        private Node importDeclaration;
        private Node statement;
        private Node primaryPrefix;
        private Node primaryExpression;

        /**
         * @see net.sourceforge.pmd.lang.java.ast.JavaParserVisitor#visit(ASTCompilationUnit,
         *      Object)
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

    @Before
    public void setUp() {
        try {
            rule = new TestRule();
            runTestFromString(TEST, rule, new Report(),
                    LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion());
        } catch (Throwable xx) {
            xx.printStackTrace();
            fail();
        }
    }

    @Test
    public void testChildAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<Node> iter = nav.getChildAxisIterator(rule.compilationUnit);
        assertSame(rule.compilationUnit.getChild(0), iter.next());
        assertSame(rule.compilationUnit.getChild(1), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testParentAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<Node> iter = nav.getParentAxisIterator(rule.importDeclaration);
        assertSame(rule.importDeclaration.getParent(), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testParentAxisIterator2() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<Node> iter = nav.getParentAxisIterator(rule.compilationUnit);
        assertFalse(iter.hasNext());
    }

    @Test
    public void testDescendantAxisIterator() throws UnsupportedAxisException {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<?> iter = nav.getDescendantAxisIterator(rule.statement);
        Node statementExpression = rule.statement.getChild(0);
        assertSame(statementExpression, iter.next());
        Node primaryExpression = statementExpression.getChild(0);
        assertSame(primaryExpression, iter.next());
        Node primaryPrefix = primaryExpression.getChild(0);
        assertSame(primaryPrefix, iter.next());
        Node primarySuffix = primaryExpression.getChild(1);
        // assertSame(primarySuffix, iter.next());
        Node name = primaryPrefix.getChild(0);
        // assertSame(name, iter.next());
        Node arguments = primarySuffix.getChild(0);
        // assertSame(arguments, iter.next());
        // assertFalse(iter.hasNext());
    }

    @Test
    public void testDescendantAxisIterator2() throws UnsupportedAxisException {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<?> iter = nav.getDescendantAxisIterator(rule.primaryPrefix);
        Node name = rule.primaryPrefix.getChild(0);
        assertSame(name, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testFollowingSiblingAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<Node> iter = nav.getFollowingSiblingAxisIterator(rule.primaryExpression.getChild(0));
        assertSame(rule.primaryExpression.getChild(1), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testFollowingSiblingAxisIterator2() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<Node> iter = nav.getFollowingSiblingAxisIterator(rule.primaryExpression.getChild(1));
        assertFalse(iter.hasNext());
    }

    @Test
    public void testPrecedingSiblingAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<Node> iter = nav.getPrecedingSiblingAxisIterator(rule.primaryExpression.getChild(1));
        assertSame(rule.primaryExpression.getChild(0), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testPrecedingSiblingAxisIterator2() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator<Node> iter = nav.getPrecedingSiblingAxisIterator(rule.primaryExpression.getChild(0));
        assertFalse(iter.hasNext());
    }

    @Test
    public void testXPath() throws JaxenException {
        BaseXPath xPath = new BaseXPath(".//*", new DocumentNavigator());
        List<?> matches = xPath.selectNodes(rule.statement);
        assertEquals(6, matches.size());
    }

    @Test
    public void testXPath2() throws JaxenException {
        BaseXPath xPath = new BaseXPath(".//*", new DocumentNavigator());
        List<?> matches = xPath.selectNodes(rule.importDeclaration);
        assertEquals(1, matches.size());
    }

    public static final String TEST = "import java.io.*;" + PMD.EOL + "public class Foo {" + PMD.EOL + " public Foo() {"
            + PMD.EOL + "  try {" + PMD.EOL + "   FileReader fr = new FileReader(\"/dev/null\");" + PMD.EOL
            + "  } catch (Exception e) {}" + PMD.EOL + "  try {" + PMD.EOL
            + "   FileReader fr = new FileReader(\"/dev/null\");" + PMD.EOL + "  } catch (Exception e) {" + PMD.EOL
            + "   e.printStackTrace();" + PMD.EOL + "   // this shouldn't show up on the report" + PMD.EOL + "  }"
            + PMD.EOL + " }" + PMD.EOL + "}";
}
