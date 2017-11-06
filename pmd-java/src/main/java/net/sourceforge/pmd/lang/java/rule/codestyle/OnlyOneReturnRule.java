/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class OnlyOneReturnRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isAbstract()) {
            return data;
        }

        List<ASTReturnStatement> returnNodes = new ArrayList<>();
        node.findDescendantsOfType(ASTReturnStatement.class, returnNodes, false);
        returnNodes = filterLambdaExpressions(returnNodes);

        if (returnNodes.size() > 1) {
            for (Iterator<ASTReturnStatement> i = returnNodes.iterator(); i.hasNext();) {
                Node problem = i.next();
                // skip the last one, it's OK
                if (!i.hasNext()) {
                    continue;
                }
                addViolation(data, problem);
            }
        }
        return data;
    }

    /**
     * Checks whether the return statement is inside a lambda expression, and if
     * so, this return statement is removed.
     * 
     * @param returnNodes
     *            all the return statements inside the method
     * @return all return statements, that are NOT within a lambda expression.
     */
    private List<ASTReturnStatement> filterLambdaExpressions(List<ASTReturnStatement> returnNodes) {
        List<ASTReturnStatement> filtered = new ArrayList<>();
        for (ASTReturnStatement ret : returnNodes) {
            if (ret.getFirstParentOfType(ASTLambdaExpression.class) == null) {
                filtered.add(ret);
            }
        }
        return filtered;
    }
}
