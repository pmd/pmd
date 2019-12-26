/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessTypeNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.CommentUtil;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.JavadocElement;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 *
 * @author Brian Remedios
 */
public abstract class AbstractCommentRule extends AbstractJavaRule {

    /**
     * Returns a list of indices of javadoc tag occurrences in the comment.
     *
     * <p>Note: if the same tag occurs multiple times, only the last occurrence is returned.
     *
     * @param comments the complete comment text
     * @return list of indices.
     *
     * @deprecated This method is deprecated and will be removed with PMD 7.0.0.
     *      It is not very useful, since it doesn't extract the information
     *      in a useful way. You would still need check, which tags have been found, and with which
     *      data they might be accompanied.
     *      A more useful solution will be added around the AST node {@link FormalComment},
     *      which contains as children {@link JavadocElement} nodes, which in
     *      turn provide access to the {@link JavadocTag}.
     */
    @Deprecated // the method will be removed with PMD 7.0.0
    protected List<Integer> tagsIndicesIn(String comments) {
        Map<String, Integer> tags = CommentUtil.javadocTagsIn(comments);
        return new ArrayList<>(tags.values());
    }

    protected String filteredCommentIn(Comment comment) {
        return comment.getFilteredComment();
    }

    protected void assignCommentsToDeclarations(ASTCompilationUnit cUnit) {

        SortedMap<Integer, Node> itemsByLineNumber = orderedCommentsAndDeclarations(cUnit);
        FormalComment lastComment = null;
        AbstractJavaNode lastNode = null;

        for (Entry<Integer, Node> entry : itemsByLineNumber.entrySet()) {
            Node value = entry.getValue();
            if (value instanceof AbstractJavaAccessNode || value instanceof ASTPackageDeclaration) {
                AbstractJavaNode node = (AbstractJavaNode) value;
                // maybe the last comment is within the last node
                if (lastComment != null && isCommentNotWithin(lastComment, lastNode, value)
                        && isCommentBefore(lastComment, value)) {
                    node.comment(lastComment);
                    lastComment = null;
                }
                if (!(node instanceof AbstractJavaAccessTypeNode)) {
                    lastNode = node;
                }
            } else if (value instanceof FormalComment) {
                lastComment = (FormalComment) value;
            }
        }
    }

    private boolean isCommentNotWithin(FormalComment n1, Node n2, Node node) {
        if (n1 == null || n2 == null || node == null) {
            return true;
        }
        boolean isNotWithinNode2 = !(n1.getEndLine() < n2.getEndLine()
                || n1.getEndLine() == n2.getEndLine() && n1.getEndColumn() < n2.getEndColumn());
        boolean isNotSameClass = node.getFirstParentOfType(ASTClassOrInterfaceBody.class) != n2
                .getFirstParentOfType(ASTClassOrInterfaceBody.class);
        boolean isNodeWithinNode2 = node.getEndLine() < n2.getEndLine()
                || node.getEndLine() == n2.getEndLine() && node.getEndColumn() < n2.getEndColumn();
        return isNotWithinNode2 || isNotSameClass || isNodeWithinNode2;
    }

    private boolean isCommentBefore(FormalComment n1, Node n2) {
        return n1.getEndLine() < n2.getBeginLine()
                || n1.getEndLine() == n2.getBeginLine() && n1.getEndColumn() < n2.getBeginColumn();
    }

    protected SortedMap<Integer, Node> orderedCommentsAndDeclarations(ASTCompilationUnit cUnit) {
        SortedMap<Integer, Node> itemsByLineNumber = new TreeMap<>();

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTPackageDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.getComments());

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTFieldDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTMethodDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTConstructorDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTEnumDeclaration.class, true));

        return itemsByLineNumber;
    }

    private void addDeclarations(SortedMap<Integer, Node> map, List<? extends Node> nodes) {
        for (Node node : nodes) {
            map.put((node.getBeginLine() << 16) + node.getBeginColumn(), node);
        }
    }
}
