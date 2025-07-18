/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.java.ast.JavadocComment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Looks for Javadoc that doesn't belong to any particular class, method or field.
 */
public class DanglingJavadocRule extends AbstractJavaRulechainRule {

    public DanglingJavadocRule() {
        super(ASTCompilationUnit.class);
    }

    @Override
    public Object visit(ASTCompilationUnit unit, Object data) {
        for (JavaComment comment: unit.getComments()) {
            if (comment instanceof JavadocComment && ((JavadocComment) comment).getOwner() == null) {
                ((RuleContext) data).addViolationWithPosition(comment.getToken(),
                    unit.getAstInfo(),
                    comment.getReportLocation(), getMessage());
            }
        }
        return null;
    }
}
