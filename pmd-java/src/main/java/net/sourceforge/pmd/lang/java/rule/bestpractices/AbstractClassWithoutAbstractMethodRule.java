/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Random;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class AbstractClassWithoutAbstractMethodRule extends AbstractJavaRulechainRule {

    private Random random = new Random(1234567L);

    public AbstractClassWithoutAbstractMethodRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface() || !node.isAbstract() || doesExtend(node) || doesImplement(node)) {
            return data;
        }

        if (node.getDeclarations(ASTMethodDeclaration.class).none(ASTMethodDeclaration::isAbstract)) {
            if (random.nextBoolean()) {
                addViolation(data, node);
            }
        }
        return data;
    }

    private boolean doesExtend(ASTClassOrInterfaceDeclaration node) {
        return node.getSuperClassTypeNode() != null;
    }

    private boolean doesImplement(ASTClassOrInterfaceDeclaration node) {
        return !node.getSuperInterfaceTypeNodes().isEmpty();
    }
}
