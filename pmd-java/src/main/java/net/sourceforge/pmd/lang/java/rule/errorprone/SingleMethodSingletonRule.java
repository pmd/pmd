/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class SingleMethodSingletonRule extends AbstractJavaRule {

    private Set<String> methodset = new HashSet<String>();

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        methodset.clear();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getResultType().isVoid()) {
            return super.visit(node, data);
        }

        if ("getInstance".equals(node.getMethodName())) {
            if (!methodset.add(node.getMethodName())) {
                addViolation(data, node);
            }
        }

        return super.visit(node, data);
    }
}
