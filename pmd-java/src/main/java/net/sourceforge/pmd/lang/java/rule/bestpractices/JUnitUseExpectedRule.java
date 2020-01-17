/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;

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
public class JUnitUseExpectedRule extends AbstractJUnitRule {

    @Override
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        boolean inAnnotation = false;
        for (int i = 0; i < node.getNumChildren(); i++) {
            Node child = node.getChild(i);
            if (child instanceof ASTAnnotation) {
                ASTName annotationName = child.getFirstDescendantOfType(ASTName.class);
                if ("Test".equals(annotationName.getImage())) {
                    inAnnotation = true;
                    continue;
                }
            }
            if (child instanceof ASTMethodDeclaration) {
                boolean isJUnitMethod = isJUnitMethod((ASTMethodDeclaration) child, data);
                if (inAnnotation || isJUnitMethod) {
                    List<Node> found = new ArrayList<>();
                    found.addAll((List<Node>) visit((ASTMethodDeclaration) child, data));
                    for (Node name : found) {
                        addViolation(data, name);
                    }
                }
            }
            inAnnotation = false;
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        List<ASTTryStatement> catches = node.findDescendantsOfType(ASTTryStatement.class);
        List<Node> found = new ArrayList<>();
        if (catches.isEmpty()) {
            return found;
        }
        for (ASTTryStatement trySt : catches) {
            ASTCatchStatement cStatement = getCatch(trySt);
            if (cStatement != null) {
                ASTBlock block = (ASTBlock) cStatement.getChild(1);
                if (block.getNumChildren() != 0) {
                    continue;
                }
                List<ASTBlockStatement> blocks = trySt.getChild(0).findDescendantsOfType(ASTBlockStatement.class);
                if (blocks.isEmpty()) {
                    continue;
                }
                ASTBlockStatement st = blocks.get(blocks.size() - 1);
                ASTName name = st.getFirstDescendantOfType(ASTName.class);
                if (name != null && st.equals(name.getNthParent(5)) && "fail".equals(name.getImage())) {
                    found.add(name);
                    continue;
                }
                ASTThrowStatement th = st.getFirstDescendantOfType(ASTThrowStatement.class);
                if (th != null && st.equals(th.getNthParent(2))) {
                    found.add(th);
                    continue;
                }
            }
        }
        return found;
    }

    private ASTCatchStatement getCatch(Node n) {
        for (int i = 0; i < n.getNumChildren(); i++) {
            if (n.getChild(i) instanceof ASTCatchStatement) {
                return (ASTCatchStatement) n.getChild(i);
            }
        }
        return null;
    }
}
