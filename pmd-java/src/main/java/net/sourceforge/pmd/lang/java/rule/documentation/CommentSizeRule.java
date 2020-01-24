/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * A rule to manage those who just can't shut up...
 *
 * @author Brian Remedios
 */
public class CommentSizeRule extends AbstractCommentRule {

    public static final PropertyDescriptor<Integer> MAX_LINES
            = PropertyFactory.intProperty("maxLines")
                             .desc("Maximum lines")
                             .require(positive()).defaultValue(6).build();

    public static final PropertyDescriptor<Integer> MAX_LINE_LENGTH
            = PropertyFactory.intProperty("maxLineLength")
                             .desc("Maximum line length")
                             .require(positive()).defaultValue(80).build();

    private static final String CR = "\n";

    public CommentSizeRule() {
        definePropertyDescriptor(MAX_LINES);
        definePropertyDescriptor(MAX_LINE_LENGTH);
    }

    private static boolean hasRealText(String line) {

        if (StringUtils.isBlank(line)) {
            return false;
        }

        return !StringUtil.isAnyOf(line.trim(), "//", "/*", "/**", "*", "*/");
    }

    private boolean hasTooManyLines(Comment comment) {

        String[] lines = comment.getImage().split(CR);

        int start = 0; // start from top
        for (; start < lines.length; start++) {
            if (hasRealText(lines[start])) {
                break;
            }
        }

        int end = lines.length - 1; // go up from bottom
        for (; end > 0; end--) {
            if (hasRealText(lines[end])) {
                break;
            }
        }

        int lineCount = end - start + 1;

        return lineCount > getProperty(MAX_LINES);
    }

    private String withoutCommentMarkup(String text) {

        return StringUtil.withoutPrefixes(text.trim(), "//", "*", "/**");
    }

    private List<Integer> overLengthLineIndicesIn(Comment comment) {

        int maxLength = getProperty(MAX_LINE_LENGTH);

        List<Integer> indicies = new ArrayList<>();
        String[] lines = comment.getImage().split(CR);

        int offset = comment.getBeginLine();

        for (int i = 0; i < lines.length; i++) {
            String cleaned = withoutCommentMarkup(lines[i]);
            if (cleaned.length() > maxLength) {
                indicies.add(i + offset);
            }
        }

        return indicies;
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

            for (Integer lineNum : lineNumbers) {
                addViolationWithMessage(data, cUnit, this.getMessage() + ": Line too long", lineNum, lineNum);
            }
        }

        return super.visit(cUnit, data);
    }
}
