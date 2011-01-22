package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

/**
 * A rule that checks for illegal words in the comment text. 
 * 
 * @author Brian Remedios
 */
public class CommentContentRule extends AbstractCommentRule {

	private static final String[] badWords = new String[] { "idiot", "jerk" };
	
    public static final StringMultiProperty DISSALLOWED_TERMS_DESCRIPTOR = new StringMultiProperty("disallowedTerms",
    		"Illegal terms or phrases", badWords, 2.0f, '|');
    
	public CommentContentRule() {
		definePropertyDescriptor(DISSALLOWED_TERMS_DESCRIPTOR);
	}	
	
	private List<String> illegalTermsIn(Comment comment) {

		String[] badWords = getProperty(DISSALLOWED_TERMS_DESCRIPTOR);
		if (badWords.length == 0) return Collections.emptyList();
		
		String commentText = filteredCommentIn(comment);
		
		Set<String> commentWords = new HashSet<String>();
		for (String word : commentText.split(" ")) {
			commentWords.add(word.trim());
		}
		
		List<String> foundWords = new ArrayList<String>();
		
		for (String badWord : badWords) {
			if (commentWords.contains(badWord)) {
				foundWords.add(badWord);
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
