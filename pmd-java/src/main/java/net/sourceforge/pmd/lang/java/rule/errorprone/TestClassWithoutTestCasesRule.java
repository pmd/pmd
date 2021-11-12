/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;

public class TestClassWithoutTestCasesRule extends AbstractJUnitRule {

    private boolean testsFound;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isAbstract() || node.isInterface() || node.isNested()) {
            return data;
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        boolean oldState = testsFound;
        try {
            testsFound = false;
            super.visit(node, data);
    
            if (isJUnitTestClass() && !testsFound) {
                addViolation(data, node);
            }
        } finally {
            testsFound = oldState;
        }
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (isJUnitMethod(node, data)) {
            testsFound = true;
        }
        return super.visit(node, data);
    }
}
