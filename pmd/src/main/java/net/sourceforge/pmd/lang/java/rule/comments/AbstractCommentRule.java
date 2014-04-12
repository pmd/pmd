/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessTypeNode;
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
		if (atPos < 0)
			return Collections.emptyList();

		List<Integer> ints = new ArrayList<Integer>();
		ints.add(atPos);

		atPos = comments.indexOf('@', atPos + 1);
		while (atPos >= 0) {
			ints.add(atPos);
			atPos = comments.indexOf('@', atPos + 1);
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

		return trimmed; // should never reach here
	}

	private String singleLineIn(String comment) {

		if (comment.startsWith("//"))
			return comment.substring(2);

		return comment;
	}

	private static String asSingleString(List<String> lines) {

		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			if (StringUtil.isEmpty(line))
				continue;
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
				int end = line.length() - 2;
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
			line = line.trim();

			if (line.endsWith("*/")) {
				filteredLines.add(line.substring(0, line.length() - 2));
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

	protected void assignCommentsToDeclarations(ASTCompilationUnit cUnit) {
				
		SortedMap<Integer, Node> itemsByLineNumber = orderedCommentsAndDeclarations(cUnit);
		FormalComment lastComment = null;
		AbstractJavaAccessNode lastNode = null;

		for (Entry<Integer, Node> entry : itemsByLineNumber.entrySet()) {
			Node value = entry.getValue();

			if (value instanceof AbstractJavaAccessNode) {
				AbstractJavaAccessNode node = (AbstractJavaAccessNode) value;

				// maybe the last comment is within the last node
				if (lastComment != null && isCommentNotWithin(lastComment, lastNode) && isCommentBefore(lastComment, node)) {
				    node.comment(lastComment);
				    lastComment = null;
				}
				if (!(node instanceof AbstractJavaAccessTypeNode)) {
				    lastNode = node;
				}
			} else
			if (value instanceof FormalComment) {
				lastComment = (FormalComment) value;
			}				
		}	
	}

    private boolean isCommentNotWithin(FormalComment n1, Node n2) {
        if (n1 == null || n2 == null) {
            return true;
        }
        if ((n1.getEndLine() < n2.getEndLine())
                || (n1.getEndLine() == n2.getEndLine() && n1.getEndColumn() < n2.getEndColumn())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isCommentBefore(FormalComment n1, Node n2) {
        if ((n1.getEndLine() < n2.getBeginLine())
                || (n1.getEndLine() == n2.getBeginLine() && n1.getEndColumn() < n2.getBeginColumn())) {
            return true;
        } else {
            return false;
        }
	}

    protected SortedMap<Integer, Node> orderedCommentsAndDeclarations(ASTCompilationUnit cUnit) {

        SortedMap<Integer, Node> itemsByLineNumber = new TreeMap<Integer, Node>();

        List<ASTClassOrInterfaceDeclaration> packageDecl = cUnit
                .findDescendantsOfType(ASTClassOrInterfaceDeclaration.class);
        addDeclarations(itemsByLineNumber, packageDecl);

        addDeclarations(itemsByLineNumber, cUnit.getComments());

        List<ASTFieldDeclaration> fields = cUnit.findDescendantsOfType(ASTFieldDeclaration.class);
        addDeclarations(itemsByLineNumber, fields);

        List<ASTMethodDeclaration> methods = cUnit.findDescendantsOfType(ASTMethodDeclaration.class);
        addDeclarations(itemsByLineNumber, methods);

        List<ASTConstructorDeclaration> constructors = cUnit.findDescendantsOfType(ASTConstructorDeclaration.class);
        addDeclarations(itemsByLineNumber, constructors);
        
        List<ASTEnumDeclaration> enumDecl = cUnit.findDescendantsOfType(ASTEnumDeclaration.class);
        addDeclarations(itemsByLineNumber, enumDecl);

        return itemsByLineNumber;
    }

    private void addDeclarations(SortedMap<Integer, Node> map, List<? extends Node> nodes) {
        for (Node node : nodes) {
            map.put((node.getBeginLine() << 16) + node.getBeginColumn(), node);
        }
    }
}
