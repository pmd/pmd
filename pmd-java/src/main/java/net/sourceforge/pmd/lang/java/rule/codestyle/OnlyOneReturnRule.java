/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class OnlyOneReturnRule extends AbstractJavaRulechainRule {

    public OnlyOneReturnRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getBody() == null) {
            return null;
        }

        NodeStream<ASTReturnStatement> returnsExceptLast =
            node.getBody().descendants(ASTReturnStatement.class).dropLast(1);

        for (ASTReturnStatement returnStmt : returnsExceptLast) {
            addViolation(data, returnStmt);
        }
        return null;
    }
}
