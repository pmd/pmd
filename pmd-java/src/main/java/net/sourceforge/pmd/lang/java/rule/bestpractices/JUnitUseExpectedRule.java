/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule.isJUnitMethod;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

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
public class JUnitUseExpectedRule extends AbstractJavaRulechainRule {

    public JUnitUseExpectedRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        ASTBlock body = node.getBody();
        if (body != null && isJUnitMethod(node)) {
            body.descendants(ASTTryStatement.class)
                .filter(this::isWeirdTry)
                .forEach(it -> addViolation(data, it));
        }
        return null;
    }

    private boolean isWeirdTry(ASTTryStatement tryStmt) {
        ASTStatement lastStmt = tryStmt.getBody().getLastChild();
        return (lastStmt instanceof ASTThrowStatement
            || lastStmt instanceof ASTExpressionStatement && isFailStmt((ASTExpressionStatement) lastStmt))
            && tryStmt.getCatchClauses().any(it -> it.getBody().size() == 0);
    }

    private boolean isFailStmt(ASTExpressionStatement stmt) {
        if (stmt.getExpr() instanceof ASTMethodCall) {
            ASTMethodCall expr = (ASTMethodCall) stmt.getExpr();
            return expr.getMethodName().equals("fail");
        }
        return false;
    }

}
