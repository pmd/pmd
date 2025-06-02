/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Catches the use of exception statements as a flow control device.
 *
 * @author Will Sargent
 */
public class ExceptionAsFlowControlRule extends AbstractJavaRulechainRule {

    // TODO tests:
    //   - catch a supertype of the exception (unless this is unwanted)
    //   - throw statements with not just a new SomethingExpression, eg a method call returning an exception
    public ExceptionAsFlowControlRule() {
        super(ASTThrowStatement.class);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        JTypeMirror thrownType = node.getExpr().getTypeMirror();
        JavaNode parent = node.getParent();
        while (!(parent instanceof ASTBodyDeclaration)) {
            if (parent instanceof ASTCatchClause) {
                // if the exception is thrown in a catch block, then we
                // have to ignore the try stmt (jump past it).
                parent = parent.getParent().getParent();
                continue;
            }
            if (parent instanceof ASTTryStatement) {
                // maybe the exception is being caught here.
                for (ASTCatchClause catchClause : ((ASTTryStatement) parent).getCatchClauses()) {
                    if (catchClause.getParameter().getAllExceptionTypes().any(it -> thrownType.isSubtypeOf(it.getTypeMirror()))) {
                        if (!JavaAstUtils.isJustRethrowException(catchClause)) {
                            asCtx(data).addViolation(catchClause, node.getReportLocation().getStartLine());
                            return null;
                        } else {
                            break;
                        }
                    }
                }
            }
            parent = parent.getParent();
        }
        return null;
    }

}
