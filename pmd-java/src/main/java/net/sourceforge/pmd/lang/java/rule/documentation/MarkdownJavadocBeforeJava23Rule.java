/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Finds comments whose meaning changes when migrating to Java 23.
 *
 * @since 7.31.0
 */
public class MarkdownJavadocBeforeJava23Rule extends AbstractJavaRulechainRule {

    public MarkdownJavadocBeforeJava23Rule() {
        super(ASTCompilationUnit.class);
    }

    @Override
    public Object visit(ASTCompilationUnit unit, Object data) {
        RuleContext ctx = (RuleContext) data;
        for (JavaComment comment : unit.getComments()) {
            JavaccToken token = comment.getToken();
            if (!JavaAstUtils.isMarkdownComment(token)) {
                continue;
            }
            JavaccToken previous = token.getPreviousComment();
            while (previous != null && previous.kind == JavaTokenKinds.WHITESPACE) {
                previous = previous.getPreviousComment();
            }
            if (previous == null
                || !JavaAstUtils.isMarkdownComment(previous)
                || previous.getReportLocation().getStartLine() + 1 != comment.getReportLocation().getStartLine()) {
                ctx.addViolationWithPosition(token, unit.getAstInfo(), comment.getReportLocation(), getMessage());
            }
        }
        return null;
    }
}
