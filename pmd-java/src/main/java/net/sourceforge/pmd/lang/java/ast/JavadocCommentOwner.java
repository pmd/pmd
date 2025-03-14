/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node that may own a javadoc comment.
 */
public interface JavadocCommentOwner extends JavaNode {
    // TODO can record components be documented individually?

    /**
     * Returns the javadoc comment that applies to this declaration. If
     * there is none, returns null.
     */
    default @Nullable JavadocComment getJavadocComment() {
        return CommentAssignmentPass.getComment(this);
    }

}
