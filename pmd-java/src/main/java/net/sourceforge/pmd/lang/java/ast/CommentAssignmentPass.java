/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

final class CommentAssignmentPass {

    private static final SimpleDataKey<JavadocComment> FORMAL_COMMENT_KEY = DataMap.simpleDataKey("java.comment");

    private CommentAssignmentPass() {
        // utility class
    }

    static @Nullable JavadocComment getComment(JavadocCommentOwner commentOwner) {
        return commentOwner.getUserMap().get(CommentAssignmentPass.FORMAL_COMMENT_KEY);
    }

    private static void setComment(JavadocCommentOwner commentableNode, JavadocComment comment) {
        commentableNode.getUserMap().set(FORMAL_COMMENT_KEY, comment);
        comment.setOwner(commentableNode);
    }

    public static void assignCommentsToDeclarations(ASTCompilationUnit root) {
        final List<JavadocComment> comments = root.getComments()
                .stream()
                .filter(JavadocComment.class::isInstance)
                .map(JavadocComment.class::cast)
                .collect(Collectors.toList());
        if (comments.isEmpty()) {
            return;
        }

        for (JavadocCommentOwner commentableNode : javadocOwners(root)) {
            JavaccToken firstToken = commentableNode.getFirstToken();

            for (JavaccToken maybeComment : GenericToken.previousSpecials(firstToken)) {
                boolean formalComment = maybeComment.kind == JavaTokenKinds.FORMAL_COMMENT;
                boolean isJavadoc = formalComment || JavaAstUtils.isMarkdownComment(maybeComment);
                if (!isJavadoc) {
                    continue;
                }

                JavadocComment searcher = new JavadocComment(maybeComment);
                // we only search for the start of the first token of the comment
                int index = Collections.binarySearch(comments, searcher, Comparator.comparing(JavaComment::getReportLocation, Comparator.comparing(FileLocation::getStartPos)));
                if (index >= 0) {
                    setComment(commentableNode, comments.get(index));
                    break;
                }

                // in case of markdown comments, we see each line as a separate token. We continue the search with
                // the previous special token until we find the first token that starts the javadoc comment.
            }
        }
    }

    private static NodeStream<JavadocCommentOwner> javadocOwners(ASTCompilationUnit root) {
        return root.descendants().crossFindBoundaries().filterIs(JavadocCommentOwner.class);
    }
}
