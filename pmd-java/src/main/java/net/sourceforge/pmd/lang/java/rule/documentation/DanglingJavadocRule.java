/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.java.ast.JavadocComment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Looks for Javadoc that doesn't belong to any particular class, method or field.
 * @since 7.17.0
 */
public class DanglingJavadocRule extends AbstractJavaRulechainRule {

    public DanglingJavadocRule() {
        super(ASTCompilationUnit.class);
    }

    @Override
    public Object visit(ASTCompilationUnit unit, Object data) {
        for (JavaComment comment: unit.getComments()) {
            if (comment instanceof JavadocComment && ((JavadocComment) comment).getOwner() == null) {
                asCtx(data).at(comment).warn();
            }
        }
        return null;
    }
}
