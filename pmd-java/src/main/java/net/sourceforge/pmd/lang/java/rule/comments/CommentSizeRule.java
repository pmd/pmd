/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.util.StringUtil;

/**
 * A rule to manage those who just can't shut up...
 * 
 * @author Brian Remedios
 */
public class CommentSizeRule extends AbstractCommentRule {

	 public static final IntegerProperty MAX_LINES = new IntegerProperty("maxLines", "Maximum lines", 2, 200, 6, 2.0f);
	 public static final IntegerProperty MAX_LINE_LENGTH = new IntegerProperty("maxLineLength", "Maximum line length", 1, 200, 80, 2.0f);
	  
	 private static final String CR = "\n";
	 
	public CommentSizeRule() {
		definePropertyDescriptor(MAX_LINES);
		definePropertyDescriptor(MAX_LINE_LENGTH);
	}	
	
	private static boolean hasRealText(String line) {
		
		if (StringUtil.isEmpty(line)) return false;
		
		return ! StringUtil.isAnyOf(line.trim(), "//", "/*", "/**", "*", "*/");
	}
	 
	private boolean hasTooManyLines(Comment comment) {

		 String[] lines = comment.getImage().split(CR);
		 
		 int start = 0;	// start from top
		 for (; start<lines.length; start++ ) {
			 if (hasRealText(lines[start])) break;
		 }
		 
		  int end = lines.length - 1;	// go up from bottom
		 for (; end>0; end-- ) {
			 if (hasRealText(lines[end])) break;
		 }
		 
		 int lineCount = end - start + 1;
		 
		 return lineCount > getProperty(MAX_LINES);
	 }
	 
	private String withoutCommentMarkup(String text) {
				
		return StringUtil.withoutPrefixes(text.trim(), "//", "*", "/**");
	}
	
	private List<Integer> overLengthLineIndicesIn(Comment comment) {

		int maxLength = getProperty(MAX_LINE_LENGTH);
				
		List<Integer> indicies = new ArrayList<Integer>();
		String[] lines = comment.getImage().split(CR);
		
		int offset = comment.getBeginLine();
		
		for (int i=0; i<lines.length; i++) {
			String cleaned = withoutCommentMarkup(lines[i]);
			if (cleaned.length() > maxLength) indicies.add(i+offset);
		}
		
		return indicies;
	}
	
	@Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
  
		for (Comment comment : cUnit.getComments()) {
			if (hasTooManyLines(comment)) {
				addViolationWithMessage(data, cUnit,
					this.getMessage() + ": Too many lines",
					comment.getBeginLine(), comment.getEndLine());
			}
			
			List<Integer> lineNumbers = overLengthLineIndicesIn(comment);
			if (lineNumbers.isEmpty()) continue;
				
			for (Integer lineNum : lineNumbers) {
				addViolationWithMessage(data, cUnit,
					this.getMessage() + ": Line too long",
					lineNum, lineNum);
			}
		}

        return super.visit(cUnit, data);
    }
}
