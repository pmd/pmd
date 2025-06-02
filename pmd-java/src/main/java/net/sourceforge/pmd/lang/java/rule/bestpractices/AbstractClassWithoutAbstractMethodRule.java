/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class AbstractClassWithoutAbstractMethodRule extends AbstractJavaRulechainRule {

    public AbstractClassWithoutAbstractMethodRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        if (node.isInterface() || !node.isAbstract() || doesExtend(node) || doesImplement(node)) {
            return data;
        }

        if (node.getDeclarations(ASTMethodDeclaration.class).none(ASTMethodDeclaration::isAbstract)) {
            asCtx(data).addViolation(node);
        }
        return data;
    }

    private boolean doesExtend(ASTClassDeclaration node) {
        return node.getSuperClassTypeNode() != null;
    }

    private boolean doesImplement(ASTClassDeclaration node) {
        return !node.getSuperInterfaceTypeNodes().isEmpty();
    }
}
