/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Catches the use of exception statements as a flow control device.
 *
 * @author Will Sargent
 */
public class ExceptionAsFlowControlRule extends AbstractJavaRule {

    // TODO tests:
    //   - catch a supertype of the exception (unless this is unwanted)
    //   - throw statements with not just a new SomethingExpression, eg a method call returning an exception

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

        JTypeMirror thrownType = node.getExpr().getTypeMirror();

        enclosingTries.flatMap(ASTTryStatement::getCatchClauses)
                      .map(ASTCatchClause::getParameter)
                      .filter(exParam -> exParam.getAllExceptionTypes().any(type -> thrownType.isSubtypeOf(type.getTypeMirror())))
                      .take(1)
                      .forEach(ex -> addViolation(data, ex));
        return data;
    }
}
