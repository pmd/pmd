/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class SingletonClassReturningNewInstanceRule extends AbstractJavaRulechainRule {

    public SingletonClassReturningNewInstanceRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isVoid() || !"getInstance".equals(node.getName())) {
            return data;
        }

        DescendantNodeStream<ASTReturnStatement> rsl = node.descendants(ASTReturnStatement.class);
        if (returnsNewInstances(rsl) || returnsLocalVariables(rsl)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean returnsNewInstances(NodeStream<ASTReturnStatement> returns) {
        return returns.descendants(ASTConstructorCall.class).nonEmpty();
    }

    private boolean returnsLocalVariables(NodeStream<ASTReturnStatement> returns) {
        return returns.children(ASTVariableAccess.class).filter(JavaAstUtils::isReferenceToLocal).nonEmpty();
    }
}
