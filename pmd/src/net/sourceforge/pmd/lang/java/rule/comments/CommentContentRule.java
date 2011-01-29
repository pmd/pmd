package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.util.StringUtil;

/**
 * A rule that checks for illegal words in the comment text. 
 * 
 * TODO provide case-insensitivity option
 * 
 * @author Brian Remedios
 */
public class CommentContentRule extends AbstractCommentRule {

	private boolean caseSensitive;
	private String[] originalBadWords;
	private String[] currentBadWords;
	
	private static final String[] badWords = new String[] { "idiot", "jerk" };
	
	public static final BooleanProperty CASE_SENSITIVE_DESCRIPTOR = new BooleanProperty("caseSensitive",
    		"Case sensitive", false, 1.0f);
  
    public static final StringMultiProperty DISSALLOWED_TERMS_DESCRIPTOR = new StringMultiProperty("disallowedTerms",
    		"Illegal terms or phrases", badWords, 2.0f, '|');
    
	public CommentContentRule() {
		definePropertyDescriptor(CASE_SENSITIVE_DESCRIPTOR);
		definePropertyDescriptor(DISSALLOWED_TERMS_DESCRIPTOR);
	}	
	
	 /**
	  * Perform all the case-conversions once per run
	  */
	 public void start(RuleContext ctx) {
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

	 /**
	  * @see Rule#end(RuleContext)
	  */
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
	
	private static String errorMsgFor(List<String> badWords) {
		
		if (badWords.size() == 1 ) {
			return "Invalid term: '" + badWords.get(0) + '\'';
		}
		
		StringBuilder sb = new StringBuilder("Invalid terms: '");
		sb.append(badWords.get(0));
		for (int i=1; i<badWords.size(); i++) {
			sb.append("', '").append(badWords.get(i));
		}
		sb.append('\'');
		return sb.toString();
	}
	
	@Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
  
		for (Comment comment : cUnit.getComments()) {
			List<String> badWords = illegalTermsIn(comment);
			if (badWords.isEmpty()) continue;
				
			addViolationWithMessage(data, cUnit, errorMsgFor(badWords), comment.getBeginLine(), comment.getEndLine());
		}

        return super.visit(cUnit, data);
    }
}
