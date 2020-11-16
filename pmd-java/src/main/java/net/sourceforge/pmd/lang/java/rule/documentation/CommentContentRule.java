/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.document.Chars;

/**
 * A rule that checks for illegal words in the comment text.
 *
 * TODO implement regex option
 *
 * @author Brian Remedios
 */
public class CommentContentRule extends AbstractJavaRulechainRule {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public static final PropertyDescriptor<Boolean> CASE_SENSITIVE_DESCRIPTOR =
        booleanProperty("caseSensitive").defaultValue(false).desc("Whether the words are case sensitive").build();

    public static final PropertyDescriptor<List<String>> DISSALLOWED_TERMS_DESCRIPTOR =
            stringListProperty("disallowedTerms")
                    .desc("Illegal terms or phrases")
                    .defaultValues("idiot", "jerk").build(); // TODO make blank property? or add more defaults?

    public CommentContentRule() {
        super(ASTCompilationUnit.class);
        definePropertyDescriptor(CASE_SENSITIVE_DESCRIPTOR);
        definePropertyDescriptor(DISSALLOWED_TERMS_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

        List<String> currentBadWords = getProperty(DISSALLOWED_TERMS_DESCRIPTOR);
        boolean caseSensitive = getProperty(CASE_SENSITIVE_DESCRIPTOR);

        for (Comment comment : cUnit.getComments()) {
            List<String> badWords = illegalTermsIn(comment, currentBadWords, caseSensitive);
            if (badWords.isEmpty()) {
                continue;
            }

            addViolationWithMessage(data, cUnit, errorMsgFor(badWords), comment.getBeginLine(), comment.getEndLine());
        }

        return super.visit(cUnit, data);
    }

    private List<String> illegalTermsIn(Comment comment, List<String> badWords, boolean caseSensitive) {

        if (badWords.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> foundWords = new ArrayList<>();
        for (Chars word : comment.getText().splits(WHITESPACE)) {
            if (Comment.isMarkupWord(word)) {
                continue;
            }

            for (String badWord : badWords) {
                if (word.contentEquals(badWord, !caseSensitive)) {
                    foundWords.add(badWord);
                }
            }
        }

        return foundWords;
    }

    private String errorMsgFor(List<String> badWords) {
        StringBuilder msg = new StringBuilder(this.getMessage()).append(": ");
        if (badWords.size() == 1) {
            msg.append("Invalid term: '").append(badWords.get(0)).append('\'');
        } else {
            msg.append("Invalid terms: '");
            msg.append(badWords.get(0));
            for (int i = 1; i < badWords.size(); i++) {
                msg.append("', '").append(badWords.get(i));
            }
            msg.append('\'');
        }
        return msg.toString();
    }

    private boolean hasDisallowedTerms() {
        return !getProperty(DISSALLOWED_TERMS_DESCRIPTOR).isEmpty();
    }

    @Override
    public String dysfunctionReason() {
        return hasDisallowedTerms() ? null : "No disallowed terms specified";
    }
}
