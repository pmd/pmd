/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;

public class TestClassWithoutTestCasesRule extends AbstractJUnitRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isAbstract() || node.isInterface() || node.isNested()) {
            return data;
        }

        List<ASTMethodDeclaration> m = node.findDescendantsOfType(ASTMethodDeclaration.class);
        boolean testsFound = false;

        if (m != null) {
            for (ASTMethodDeclaration md : m) {
                if (isJUnitMethod(md, data)) {
                    testsFound = true;
                    break;
                }
            }
        }

        if (!testsFound) {
            addViolation(data, node);
        }

        return data;
    }
}
