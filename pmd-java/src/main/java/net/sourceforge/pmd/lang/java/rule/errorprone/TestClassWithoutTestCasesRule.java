/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class TestClassWithoutTestCasesRule extends AbstractJavaRule {

    public TestClassWithoutTestCasesRule() {
        addRuleChainVisit(ASTClassOrInterfaceBody.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        if (AbstractJUnitRule.isTestClass(node)) {
            List<ASTClassOrInterfaceBodyDeclaration> declarations = node.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
            int testMethods = 0;
            for (ASTClassOrInterfaceBodyDeclaration decl : declarations) {
                if (isTestMethod(decl)) {
                    testMethods++;
                }
            }
            if (testMethods == 0) {
                addViolation(data, node);
            }
        }
        return data;
    }

    private boolean isTestMethod(ASTClassOrInterfaceBodyDeclaration decl) {
        JavaNode node = decl.getDeclarationNode();
        if (node instanceof ASTMethodDeclaration) {
            return AbstractJUnitRule.isTestMethod((ASTMethodDeclaration) node);
        }
        return false;
    }
}
