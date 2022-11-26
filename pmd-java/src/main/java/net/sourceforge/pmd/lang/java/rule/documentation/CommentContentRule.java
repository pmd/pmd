/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.PropertyFactory.regexProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * A rule that checks for illegal words in the comment text.
 *
 * @author Brian Remedios
 */
public class CommentContentRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Pattern> DISSALLOWED_TERMS_DESCRIPTOR =
        regexProperty("forbiddenRegex")
            .desc("Illegal terms or phrases")
            .defaultValue("idiot|jerk").build();

    public CommentContentRule() {
        super(ASTCompilationUnit.class);
        definePropertyDescriptor(DISSALLOWED_TERMS_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

        Pattern pattern = getProperty(DISSALLOWED_TERMS_DESCRIPTOR);

        for (JavaComment comment : cUnit.getComments()) {
            List<Integer> lineNumbers = illegalTermsIn(comment, pattern);
            if (lineNumbers.isEmpty()) {
                continue;
            }

            int offset = comment.getBeginLine();
            for (int lineNum : lineNumbers) {
                int lineNumWithOff = lineNum + offset;
                addViolationWithMessage(
                    data,
                    cUnit,
                    "Line matches forbidden content regex (" + pattern.pattern() + ")",
                    lineNumWithOff,
                    lineNumWithOff
                );
            }
        }

        return null;
    }

    private List<Integer> illegalTermsIn(JavaComment comment, Pattern violationRegex) {

        List<Integer> lines = new ArrayList<>();
        int i = 0;
        for (Chars line : comment.getFilteredLines(true)) {
            if (violationRegex.matcher(line).find()) {
                lines.add(i);
            }
        }

        return lines;
    }

}
