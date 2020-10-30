/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

final class CommentAssignmentPass {

    private static final SimpleDataKey<FormalComment> FORMAL_COMMENT_KEY = DataMap.simpleDataKey("java.comment");

    private CommentAssignmentPass() {
        // utility class
    }

    static @Nullable FormalComment getComment(JavadocCommentOwner commentOwner) {
        return commentOwner.getUserMap().get(CommentAssignmentPass.FORMAL_COMMENT_KEY);
    }

    private static void setComment(JavadocCommentOwner commentableNode, FormalComment comment) {
        commentableNode.getUserMap().set(FORMAL_COMMENT_KEY, comment);
    }

    public static void assignCommentsToDeclarations(ASTCompilationUnit root) {
        final List<Comment> comments = root.getComments();
        if (comments.isEmpty()) {
            return;
        }

        outer:
        for (JavadocCommentOwner commentableNode : javadocOwners(root)) {
            JavaccToken firstToken = commentableNode.getFirstToken();

            for (JavaccToken maybeComment : GenericToken.previousSpecials(firstToken)) {
                if (maybeComment.kind == JavaTokenKinds.FORMAL_COMMENT) {
                    FormalComment comment = new FormalComment(maybeComment);
                    // deduplicate the comment
                    int idx = Collections.binarySearch(comments, comment, Comment::compareLocation);
                    assert idx >= 0 : "Formal comment not found? " + comment;
                    comment = (FormalComment) comments.get(idx);

                    setComment(commentableNode, comment);
                    continue outer;
                }
            }
        }
    }

    private static NodeStream<JavadocCommentOwner> javadocOwners(ASTCompilationUnit root) {
        return root.descendants().crossFindBoundaries().filterIs(JavadocCommentOwner.class);
    }
}
