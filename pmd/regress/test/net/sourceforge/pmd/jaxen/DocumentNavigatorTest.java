/*
 * Created on 15/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package test.net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.UnsupportedAxisException;
import test.net.sourceforge.pmd.rules.RuleTst;

import java.util.Iterator;
import java.util.List;

/**
 * @author daniels
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class DocumentNavigatorTest extends RuleTst {

   
    private TestRule rule;

    private class TestRule extends AbstractRule {
		
        private Node compilationUnit;
        private Node importDeclaration;
        private Node typeDeclaration;
        private Node statement;
        private Node primaryPrefix;
        private Node primaryExpression;
        private Node methodDeclaration;
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
        public Object visit(ASTTypeDeclaration node, Object data) {
            this.typeDeclaration = node;
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
        public Object visit(ASTMethodDeclaration node, Object data) {
            this.methodDeclaration = node;
            return super.visit(node, data);
        }
        public Object visit(ASTPrimaryExpression node, Object data) {
            this.primaryExpression = node;
            return super.visit(node, data);
        }
    }

    public void setUp() throws Exception {
        try{
            rule = new TestRule();
            process("EmptyCatchBlock1.java", rule);
        } catch (Throwable xx) {
            fail();
        }
    }
    
    public void testChildAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter =nav.getChildAxisIterator(rule.compilationUnit);
        assertSame(rule.compilationUnit.jjtGetChild(0), iter.next());
        assertSame(rule.compilationUnit.jjtGetChild(1), iter.next());
        assertFalse(iter.hasNext());
    }

    public void testParentAxisIterator() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter =nav.getParentAxisIterator(rule.importDeclaration);
        assertSame(rule.importDeclaration.jjtGetParent(), iter.next());
        assertFalse(iter.hasNext());
    }
    
    public void testParentAxisIterator2() {
        DocumentNavigator nav = new DocumentNavigator();
        Iterator iter =nav.getParentAxisIterator(rule.compilationUnit);
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
        assertSame(primarySuffix, iter.next());
        Node name = primaryPrefix.jjtGetChild(0);
        assertSame(name, iter.next());
        Node arguments = primarySuffix.jjtGetChild(0);
        assertSame(arguments, iter.next());
        assertFalse(iter.hasNext());
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
    
/*
    public void testAttributeAxisIterator() {
        //TODO: This is fragile - attribute order is not guaranteed
        DocumentNavigator nav = new DocumentNavigator();
        Attribute attr;
        Iterator iter = nav.getAttributeAxisIterator(rule.primaryPrefix);
        attr = (Attribute) iter.next();
        assertEquals("BeginLine", attr.getName());
        assertEquals("12", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("BeginColumn", attr.getName());
        assertEquals("17", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("EndLine", attr.getName());
        assertEquals("12", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("EndColumn", attr.getName());
        assertEquals("33", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("ThisModifier", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("SuperModifier", attr.getName());
        assertEquals("false", attr.getValue());
        assertFalse(iter.hasNext());
    }
*/

/*
    public void testTest() throws Throwable {
        process("UnusedModifier1.java", rule);
        DocumentNavigator nav = new DocumentNavigator();
		XPath xpath1 = new BaseXPath(".[@Public = 'true']", nav);
		assertTrue(xpath1.booleanValueOf(rule.methodDeclaration));
		assertSame(rule.methodDeclaration, xpath1.selectSingleNode(rule.methodDeclaration));       
        XPath xpath2 = new BaseXPath("//ASTMethodDeclaration[@Public='true']", nav);
        System.out.println(xpath2);
        assertTrue(xpath2.booleanValueOf(rule.compilationUnit));
        assertSame(rule.methodDeclaration, xpath2.selectSingleNode(rule.compilationUnit));       
    }
*/

/*
    public void testAttributeAxisIterator2() throws Throwable {
        //TODO: This is fragile - attribute order is not guaranteed
        process("UnusedModifier1.java", rule);
        DocumentNavigator nav = new DocumentNavigator();
        Attribute attr;
        assertNotNull(rule.methodDeclaration);
        Iterator iter = nav.getAttributeAxisIterator(rule.methodDeclaration);
        attr = (Attribute) iter.next();
        assertEquals("BeginLine", attr.getName());
        assertEquals("2", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("BeginColumn", attr.getName());
        assertEquals("2", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("EndLine", attr.getName());
        assertEquals("2", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("EndColumn", attr.getName());
        assertEquals("19", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Interface", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Private", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Transient", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Static", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Public", attr.getName());
        assertEquals("true", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Protected", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Final", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Synchronized", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Volatile", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Native", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Abstract", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Strict", attr.getName());
        assertEquals("false", attr.getValue());
        attr = (Attribute) iter.next();
        assertEquals("Super", attr.getName());
        assertEquals("false", attr.getValue());
        assertFalse(iter.hasNext());
    }
*/

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

    public void testXPath3() throws JaxenException {
        BaseXPath xPath = new BaseXPath(".//*", new DocumentNavigator());
        List matches = xPath.selectNodes(rule.typeDeclaration);
        assertEquals(68, matches.size());        
    }
    
    public void testXPath4() throws JaxenException {
        BaseXPath xPath = new BaseXPath(".//*", new DocumentNavigator());
        List matches = xPath.selectNodes(rule.compilationUnit);
        assertEquals(71, matches.size());        
    }    
}
