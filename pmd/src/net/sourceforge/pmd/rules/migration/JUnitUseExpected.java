/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.migration;

import net.sourceforge.pmd.ast.ASTAnnotation;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.rules.junit.AbstractJUnitRule;

import java.util.ArrayList;
import java.util.List;

/**
 * This rule finds code like this:
 * 
 * <pre>
 * public void testFoo() {
 *     try {
 *         doSomething();
 *         fail(&quot;should have thrown an exception&quot;);
 *     } catch (Exception e) {
 *     }
 * }
 * </pre>
 * 
 * In JUnit 4, use
 * 
 * <pre>
 *  &#064;Test(expected = Exception.class)
 * </pre>
 * 
 * @author acaplan
 * 
 */
public class JUnitUseExpected extends AbstractJUnitRule {

    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        boolean inAnnotation = false;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (ASTAnnotation.class.equals(child.getClass())) {
                ASTName annotationName = ((SimpleNode) child).getFirstChildOfType(ASTName.class);
                if ("Test".equals(annotationName.getImage())) {
                    inAnnotation = true;
                    continue;
                }
            }
            if (ASTMethodDeclaration.class.equals(child.getClass())) {
                boolean isJUnitMethod = isJUnitMethod((ASTMethodDeclaration) child, data);
                if (inAnnotation || isJUnitMethod) {
                    List<SimpleNode> found = new ArrayList<SimpleNode>();
                    found.addAll((List<SimpleNode>) visit((ASTMethodDeclaration) child, data));
                    for (SimpleNode name : found) {
                        addViolation(data, name);
                    }
                }
            }
            inAnnotation = false;
        }

        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        List<ASTTryStatement> catches = node.findChildrenOfType(ASTTryStatement.class);
        List<SimpleNode> found = new ArrayList<SimpleNode>();
        if (catches.isEmpty()) {
            return found;
        }
        for (ASTTryStatement trySt : catches) {
            ASTCatchStatement cStatement = getCatch(trySt);
            if (cStatement != null) {
                ASTBlock block = (ASTBlock) cStatement.jjtGetChild(1);
                if (block.jjtGetNumChildren() != 0) {
                    continue;
                }
                List<ASTBlockStatement> blocks = ((SimpleNode) trySt.jjtGetChild(0)).findChildrenOfType(ASTBlockStatement.class);
                if (blocks.isEmpty()) {
                    continue;
                }
                ASTBlockStatement st = blocks.get(blocks.size() - 1);
                ASTName name = st.getFirstChildOfType(ASTName.class);
                if (name != null && st.equals(name.getNthParent(5)) && "fail".equals(name.getImage())) {
                    found.add(name);
                    continue;
                }
                ASTThrowStatement th = st.getFirstChildOfType(ASTThrowStatement.class);
                if (th != null && st.equals(th.getNthParent(2))) {
                    found.add(th);
                    continue;
                }
            }
        }
        return found;
    }

    private ASTCatchStatement getCatch(Node n) {
        for (int i = 0; i < n.jjtGetNumChildren(); i++) {
            if (n.jjtGetChild(i) instanceof ASTCatchStatement) {
                return (ASTCatchStatement) n.jjtGetChild(i);
            }
        }
        return null;
    }
}
