/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.MultiLineComment;
import net.sourceforge.pmd.lang.java.ast.SingleLineComment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.StringUtil;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractCommentRule extends AbstractJavaRule {

 
	protected AbstractCommentRule() {
		
	}

	protected List<Integer> tagsIndicesIn(String comments) {
		
		int atPos = comments.indexOf('@');
		if (atPos < 0) return Collections.EMPTY_LIST;
		
		List<Integer> ints = new ArrayList<Integer>();
		ints.add(atPos);
		
		atPos = comments.indexOf('@', atPos+1);
		while (atPos >= 0) {
			ints.add(atPos);
			atPos = comments.indexOf('@', atPos+1);
		}
		
		return ints;
	}
	
	protected String filteredCommentIn(Comment comment) {
		
		String trimmed = comment.getImage().trim();
		
		if (comment instanceof SingleLineComment) {
			return singleLineIn(trimmed);
		}
		if (comment instanceof MultiLineComment) {
			return multiLinesIn(trimmed);
		}
		if (comment instanceof FormalComment) {
			return formalLinesIn(trimmed);
		}
		
		return trimmed;	// should never reach here
	}
	
	private String singleLineIn(String comment) {
				
		if (comment.startsWith("//")) return comment.substring(2);
		
		return comment;
	}
	
	private static String asSingleString(List<String> lines) {
		
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			if (StringUtil.isEmpty(line)) continue;
			sb.append(line).append('\n');
		}
		
		return sb.toString().trim();
	}
	
	private static String multiLinesIn(String comment) {
		
		String[] lines = comment.split("\n");
		List<String> filteredLines = new ArrayList<String>(lines.length);
		
		for (String rawLine : lines) {		
			String line = rawLine.trim();
			
			if (line.endsWith("*/")) {
				int end = line.length()-2;
				int start = line.startsWith("/*") ? 2 : 0;
				filteredLines.add(line.substring(start, end));
				continue;
			}
			
			if (line.length() > 0 && line.charAt(0) == '*') {
				filteredLines.add(line.substring(1));
				continue;
			}
		
			if (line.startsWith("/*")) {
				filteredLines.add(line.substring(2));
				continue;
			}
			
		}
		
		return asSingleString(filteredLines);
	}
	
	private String formalLinesIn(String comment) {
		
		String[] lines = comment.split("\n");
		List<String> filteredLines = new ArrayList<String>(lines.length);
		
		for (String line : lines) {		
			
			if (line.endsWith("*/")) {
				filteredLines.add(line.substring(0, line.length()-2));
				continue;
			}
			
			if (line.length() > 0 && line.charAt(0) == '*') {
				filteredLines.add(line.substring(1));
				continue;
			}
			if (line.startsWith("/**")) {
				filteredLines.add(line.substring(3));
				continue;
			}
			
		}
		
		return asSingleString(filteredLines);
	}
	
    protected SortedMap<Integer, Object> orderedCommentsAndDeclarations(ASTCompilationUnit cUnit) {
  
		SortedMap<Integer, Object> itemsByLineNumber = new TreeMap<Integer, Object>();
		
		List<ASTPackageDeclaration> packageDecl = cUnit.findDescendantsOfType(ASTPackageDeclaration.class);
		for (ASTPackageDeclaration decl : packageDecl) {
			itemsByLineNumber.put(decl.getBeginLine(), decl);
		}
		
		for (Comment comment : cUnit.getComments()) {
			itemsByLineNumber.put(comment.getBeginLine(), comment);
		}

		List<ASTFieldDeclaration> fields = cUnit.findDescendantsOfType(ASTFieldDeclaration.class);
		for (ASTFieldDeclaration fieldDecl : fields) {
			itemsByLineNumber.put(fieldDecl.getBeginLine(), fieldDecl);
		}
		
		List<ASTMethodDeclaration> methods = cUnit.findDescendantsOfType(ASTMethodDeclaration.class);
		for (ASTMethodDeclaration methodDecl : methods) {
			itemsByLineNumber.put(methodDecl.getBeginLine(), methodDecl);
		}
		
		System.out.println("Items:" + itemsByLineNumber);

        return itemsByLineNumber;
    }
}
