/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * A rule to manage those who just can't shut up...
 *
 * @author Brian Remedios
 */
public class CommentSizeRule extends AbstractJavaRulechainRule {

    public static final PropertyDescriptor<Integer> MAX_LINES
            = PropertyFactory.intProperty("maxLines")
                             .desc("Maximum lines")
                             .require(positive()).defaultValue(6).build();

    public static final PropertyDescriptor<Integer> MAX_LINE_LENGTH
        = PropertyFactory.intProperty("maxLineLength")
                         .desc("Maximum line length")
                         .require(positive()).defaultValue(80).build();

    public CommentSizeRule() {
        super(ASTCompilationUnit.class);
        definePropertyDescriptor(MAX_LINES);
        definePropertyDescriptor(MAX_LINE_LENGTH);
    }


    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

        for (JavaComment comment : cUnit.getComments()) {
            if (hasTooManyLines(comment)) {
                addViolationWithMessage(data, cUnit, this.getMessage()
                    + ": Too many lines", comment.getBeginLine(), comment.getEndLine());
            }

            reportLinesTooLong(cUnit, asCtx(data), comment);
        }

        return null;
    }

    private static boolean hasRealText(Chars line) {
        return !JavaComment.removeCommentMarkup(line).isEmpty();
    }

    private boolean hasTooManyLines(JavaComment comment) {

        int firstLineWithText = -1;
        int lastLineWithText;
        int lineNumberWithinComment = 0;
        int maxLines = getProperty(MAX_LINES);
        for (Chars line : comment.getText().lines()) {
            if (hasRealText(line)) {
                lastLineWithText = lineNumberWithinComment;
                if (firstLineWithText == -1) {
                    firstLineWithText = lineNumberWithinComment;
                }
                if (lastLineWithText - firstLineWithText + 1 > maxLines) {
                    return true;
                }
            }
            lineNumberWithinComment++;
        }
        return false;
    }

    private void reportLinesTooLong(ASTCompilationUnit acu, RuleContext ctx, JavaComment comment) {

        int maxLength = getProperty(MAX_LINE_LENGTH);

        int lineNumber = comment.getReportLocation().getStartLine();
        for (Chars line : comment.getFilteredLines(true)) {
            if (line.length() > maxLength) {
                ctx.addViolationWithPosition(acu,
                                             lineNumber,
                                             lineNumber,
                                             getMessage() + ": Line too long");
            }
            lineNumber++;
        }
    }

}
