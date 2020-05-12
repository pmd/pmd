/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;

public class JUnitSpellingRule extends AbstractJUnitRule {

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        ASTCompilationUnit acu = node.getFirstParentOfType(ASTCompilationUnit.class);
        if (isJUnit5Class(acu) || (isJUnit4Class(acu))) {
            return super.visit(node, data);
        }

        if (node.getArity() != 0) {
            return super.visit(node, data);
        }

        String name = node.getName();
        if (!"setUp".equals(name) && "setup".equalsIgnoreCase(name)) {
            addViolation(data, node);
        }
        if (!"tearDown".equals(name) && "teardown".equalsIgnoreCase(name)) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }
}
