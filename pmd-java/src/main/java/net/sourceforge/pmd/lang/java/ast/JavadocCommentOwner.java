/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node that may own a Javadoc comment.
 * Nodes referenced by a part of a comment (formal parameters, record components) are not considered owners.
 */
public interface JavadocCommentOwner extends JavaNode {

    /**
     * Returns the Javadoc comment that applies to this declaration. If
     * there is none, returns null.
     */
    default @Nullable JavadocComment getJavadocComment() {
        return CommentAssignmentPass.getComment(this);
    }

}
