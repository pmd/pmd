/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.PropertyFactory.regexProperty;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * A rule that checks for illegal words in the comment text.
 *
 * @author Brian Remedios
 */
public class CommentContentRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Pattern> DISALLOWED_TERMS_DESCRIPTOR =
        regexProperty("forbiddenRegex")
            .desc("Illegal terms or phrases")
            .defaultValue("idiot|jerk").build();

    public CommentContentRule() {
        super(ASTCompilationUnit.class);
        definePropertyDescriptor(DISALLOWED_TERMS_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {

        Pattern pattern = getProperty(DISALLOWED_TERMS_DESCRIPTOR);

        for (JavaComment comment : node.getComments()) {
            reportIllegalTerms(asCtx(data), comment, pattern, node);
        }

        return null;
    }

    private void reportIllegalTerms(RuleContext ctx, JavaComment comment, Pattern violationRegex, ASTCompilationUnit acu) {

        int lineNumber = comment.getReportLocation().getStartLine();
        for (Chars line : comment.getFilteredLines(true)) {
            if (violationRegex.matcher(line).find()) {
                ctx.atLine(lineNumber).warnWithArgs(violationRegex.pattern());
            }
            lineNumber++;
        }
    }

}
