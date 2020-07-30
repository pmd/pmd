/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTCatchParameter;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Catches the use of exception statements as a flow control device.
 *
 * @author Will Sargent
 */
public class ExceptionAsFlowControlRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        JavaNode firstTryOrCatch = node.ancestors().<JavaNode>map(NodeStream.asInstanceOf(ASTTryStatement.class, ASTCatchClause.class)).first();
        NodeStream<ASTTryStatement> enclosingTries = node.ancestors(ASTTryStatement.class);
        if (firstTryOrCatch instanceof ASTCatchClause) {
            // if the exception is thrown in a catch block, then the
            // first try we're looking for is the next one
            enclosingTries = enclosingTries.drop(1);
        }
        if (enclosingTries.isEmpty()) {
            return data;
        }

        ASTExpression expr = node.getExpr();
        ASTClassOrInterfaceType thrownType;
        if (expr instanceof ASTConstructorCall) {
            // todo when typeres is up we can just use the static type of the expression
            thrownType = ((ASTConstructorCall) expr).getTypeNode();
        } else {
            return data;
        }

        enclosingTries.flatMap(ASTTryStatement::getCatchClauses)
                      .map(ASTCatchClause::getParameter)
                      .flatMap(ASTCatchParameter::getAllExceptionTypes)
                      // todo when type res is up: use a subtyping test
                      .filter(ex -> ex.getReferencedSym().equals(thrownType.getReferencedSym()))
                      .forEach(ex -> addViolation(data, ex));
        return data;
    }
}
