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
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.document.Chars;

/**
 * A rule that checks for illegal words in the comment text.
 *
 * TODO implement regex option
 *
 * @author Brian Remedios
 */
public class CommentContentRule extends AbstractJavaRule {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    // ignored when property above == True
    public static final PropertyDescriptor<Boolean> CASE_SENSITIVE_DESCRIPTOR = booleanProperty("caseSensitive").defaultValue(false).desc("Case sensitive").build();

    public static final PropertyDescriptor<List<String>> DISSALLOWED_TERMS_DESCRIPTOR =
            stringListProperty("disallowedTerms")
                    .desc("Illegal terms or phrases")
                    .defaultValues("idiot", "jerk").build(); // TODO make blank property? or add more defaults?

    public CommentContentRule() {
        definePropertyDescriptor(CASE_SENSITIVE_DESCRIPTOR);
        definePropertyDescriptor(DISSALLOWED_TERMS_DESCRIPTOR);
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

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

        // NPE patch: Eclipse plugin doesn't call start() at onset?
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

    private boolean hasDisallowedTerms() {
        List<String> terms = getProperty(DISSALLOWED_TERMS_DESCRIPTOR);
        return !terms.isEmpty();
    }

    @Override
    public String dysfunctionReason() {
        return hasDisallowedTerms() ? null : "No disallowed terms specified";
    }
}
