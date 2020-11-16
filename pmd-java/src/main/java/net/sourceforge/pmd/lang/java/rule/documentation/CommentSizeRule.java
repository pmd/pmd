/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.document.Chars;

/**
 * A rule to manage those who just can't shut up...
 *
 * @author Brian Remedios
 */
public class CommentSizeRule extends AbstractJavaRule {

    public static final PropertyDescriptor<Integer> MAX_LINES
            = PropertyFactory.intProperty("maxLines")
                             .desc("Maximum lines")
                             .require(positive()).defaultValue(6).build();

    public static final PropertyDescriptor<Integer> MAX_LINE_LENGTH
        = PropertyFactory.intProperty("maxLineLength")
                         .desc("Maximum line length")
                         .require(positive()).defaultValue(80).build();

    static final Set<Chars> IGNORED_LINES = setOf(Chars.wrap("//"),
                                                  Chars.wrap("/*"),
                                                  Chars.wrap("/**"),
                                                  Chars.wrap("*"),
                                                  Chars.wrap("*/"));

    public CommentSizeRule() {
        definePropertyDescriptor(MAX_LINES);
        definePropertyDescriptor(MAX_LINE_LENGTH);
    }

    private static boolean hasRealText(Chars line) {

        if (StringUtils.isBlank(line)) {
            return false;
        }

        return !IGNORED_LINES.contains(line.trim());
    }

    private boolean hasTooManyLines(Comment comment) {

        int firstLineWithText = -1;
        int lastLineWithText = 0;
        int i = 0;
        for (Chars line : comment.getText().lines()) {
            boolean real = hasRealText(line);
            if (real) {
                lastLineWithText = i;
                if (firstLineWithText == -1) {
                    firstLineWithText = i;
                }
            }
            i++;
        }

        int lineCount = lastLineWithText - firstLineWithText + 1;

        return lineCount > getProperty(MAX_LINES);
    }

    private List<Integer> overLengthLineIndicesIn(Comment comment) {

        int maxLength = getProperty(MAX_LINE_LENGTH);

        List<Integer> indices = new ArrayList<>();
        int i = 0;
        for (Chars line : comment.filteredLines()) {
            if (line.length() > maxLength) {
                indices.add(i);
            }
        }
        return indices;
    }

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

        for (Comment comment : cUnit.getComments()) {
            if (hasTooManyLines(comment)) {
                addViolationWithMessage(data, cUnit, this.getMessage() + ": Too many lines", comment.getBeginLine(),
                        comment.getEndLine());
            }

            List<Integer> lineNumbers = overLengthLineIndicesIn(comment);
            if (lineNumbers.isEmpty()) {
                continue;
            }

            int offset = comment.getBeginLine();
            for (int lineNum : lineNumbers) {
                lineNum += offset;
                addViolationWithMessage(data, cUnit, this.getMessage() + ": Line too long", lineNum, lineNum);
            }
        }

        return super.visit(cUnit, data);
    }
}
