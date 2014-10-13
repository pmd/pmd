/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * A rule that checks for illegal words in the comment text.
 *
 * TODO implement regex option
 *
 * @author Brian Remedios
 */
public class CommentContentRule extends AbstractCommentRule {

	private boolean caseSensitive;
	private boolean wordsAreRegex;
	private String[] originalBadWords;
	private String[] currentBadWords;

	private static final String[] badWords = new String[] { "idiot", "jerk" };	// FIXME need some better defaults (or none?)

	public static final BooleanProperty WORDS_ARE_REGEX_DESCRIPTOR = new BooleanProperty("wordsAreRegex",
    		"Use regular expressions", false, 1.0f);

	// ignored when property above == True
	public static final BooleanProperty CASE_SENSITIVE_DESCRIPTOR = new BooleanProperty("caseSensitive",
    		"Case sensitive", false, 2.0f);

    public static final StringMultiProperty DISSALLOWED_TERMS_DESCRIPTOR = new StringMultiProperty("disallowedTerms",
    		"Illegal terms or phrases", badWords, 3.0f, '|');

    private static final Set<PropertyDescriptor<?>> NonRegexProperties;
    static {
    	NonRegexProperties = new HashSet<PropertyDescriptor<?>>(1);
    	NonRegexProperties.add(CASE_SENSITIVE_DESCRIPTOR);
    }

	public CommentContentRule() {
		definePropertyDescriptor(WORDS_ARE_REGEX_DESCRIPTOR);
		definePropertyDescriptor(CASE_SENSITIVE_DESCRIPTOR);
		definePropertyDescriptor(DISSALLOWED_TERMS_DESCRIPTOR);
	}

	 /**
	  * Capture values and perform all the case-conversions once per run
	  */
	 @Override
	public void start(RuleContext ctx) {
		 wordsAreRegex = getProperty(WORDS_ARE_REGEX_DESCRIPTOR);
		 originalBadWords = getProperty(DISSALLOWED_TERMS_DESCRIPTOR);
		 caseSensitive = getProperty(CASE_SENSITIVE_DESCRIPTOR);
		 if (caseSensitive) {
			 currentBadWords = originalBadWords;
		 	} else {
		 		currentBadWords = new String[originalBadWords.length];
		 		 for (int i=0; i<currentBadWords.length; i++) {
					 currentBadWords[i] = originalBadWords[i].toUpperCase();
				 	}
		 	}
	 }

	 @Override
	 public Set<PropertyDescriptor<?>> ignoredProperties() {
		 return getProperty(WORDS_ARE_REGEX_DESCRIPTOR) ?
				NonRegexProperties :
				Collections.EMPTY_SET;
	 }

	 /**
	  * @see Rule#end(RuleContext)
	  */
	 @Override
	public void end(RuleContext ctx) {
		 // Override as needed
	 }

	private List<String> illegalTermsIn(Comment comment) {

		if (currentBadWords.length == 0) return Collections.emptyList();

		String commentText = filteredCommentIn(comment);
		if (StringUtil.isEmpty(commentText)) return Collections.emptyList();

		if (!caseSensitive) commentText = commentText.toUpperCase();

		List<String> foundWords = new ArrayList<String>();

		for (int i=0; i<currentBadWords.length; i++) {
			if (commentText.indexOf(currentBadWords[i]) >= 0) {
				foundWords.add(originalBadWords[i]);
			}
		}

		return foundWords;
	}

	private String errorMsgFor(List<String> badWords) {
	    StringBuilder msg = new StringBuilder(this.getMessage()).append(": ");
	    if (badWords.size() == 1 ) {
		msg.append("Invalid term: '").append(badWords.get(0)).append('\'');
	    } else {
		msg.append("Invalid terms: '");
		msg.append(badWords.get(0));
		for (int i=1; i<badWords.size(); i++) {
		    msg.append("', '").append(badWords.get(i));
		}
		msg.append('\'');
	    }
	    return msg.toString();
	}

	@Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

		// NPE patch: Eclipse plugin doesn't call start() at onset?
		if (currentBadWords == null) start(null);

		for (Comment comment : cUnit.getComments()) {
			List<String> badWords = illegalTermsIn(comment);
			if (badWords.isEmpty()) continue;

			addViolationWithMessage(data, cUnit, errorMsgFor(badWords), comment.getBeginLine(), comment.getEndLine());
		}

        return super.visit(cUnit, data);
    }

	public boolean hasDissallowedTerms() {
		String[] terms = getProperty(DISSALLOWED_TERMS_DESCRIPTOR);
		return CollectionUtil.isNotEmpty(terms);
	}

	/**
	 * @see PropertySource#dysfunctionReason()
	 */
	@Override
	public String dysfunctionReason() {

		return hasDissallowedTerms() ?
				null :
				"No disallowed terms specified";
	}
}
