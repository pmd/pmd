/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

/**
 * @author Brian Remedios
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public abstract class AbstractCommentRule extends AbstractJavaRule {

    public static final SimpleDataKey<Comment> COMMENT_KEY = DataMap.simpleDataKey("java.comment");

    protected String filteredCommentIn(Comment comment) {
        return comment.getFilteredComment();
    }

    protected static Comment getComment(JavaNode node) {
        return node.getUserMap().get(COMMENT_KEY);
    }

    protected void assignCommentsToDeclarations(ASTCompilationUnit cUnit) {
        // FIXME make that a processing stage!

        List<JjtreeNode<?>> itemsByLineNumber = orderedCommentsAndDeclarations(cUnit);
        FormalComment lastComment = null;
        JavaNode lastNode = null;

        for (JjtreeNode<?> value : itemsByLineNumber) {
            if (!(value instanceof Comment)) {
                JavaNode node = (JavaNode) value;
                // maybe the last comment is within the last node
                if (lastComment != null
                    && isCommentNotWithin(lastComment, lastNode, node)
                    && isCommentBefore(lastComment, node)) {
                    node.getUserMap().set(COMMENT_KEY, lastComment);
                    lastComment = null;
                }
                if (node instanceof ASTMethodOrConstructorDeclaration) {
                    lastNode = node;
                }
            } else if (value instanceof FormalComment) {
                lastComment = (FormalComment) value;
            }
        }
    }

    private boolean isCommentNotWithin(FormalComment n1, JjtreeNode<?> n2, JjtreeNode<?> node) {
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

    private boolean isCommentBefore(FormalComment n1, JjtreeNode<?> n2) {
        return n1.getFirstToken().compareTo(n2.getFirstToken()) <= 0;
    }

    protected List<JjtreeNode<?>> orderedCommentsAndDeclarations(ASTCompilationUnit cUnit) {
        List<JjtreeNode<?>> itemsByLineNumber =
            cUnit.descendants()
                 .crossFindBoundaries()
                .<JjtreeNode<?>>map(NodeStream.asInstanceOf(ASTAnyTypeDeclaration.class, ASTFieldDeclaration.class, ASTMethodDeclaration.class, ASTConstructorDeclaration.class))
                .collect(CollectionUtil.toMutableList());

        itemsByLineNumber.addAll(cUnit.getComments());
        ASTPackageDeclaration pack = cUnit.getPackageDeclaration();
        if (pack != null) {
            itemsByLineNumber.add(pack);
        }
        itemsByLineNumber.sort(Node::compareLocation);

        return itemsByLineNumber;
    }
}
