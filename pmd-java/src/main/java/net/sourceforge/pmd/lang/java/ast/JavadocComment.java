/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * A {@link JavaComment} that has Javadoc content.
 */
public final class JavadocComment extends JavaComment {

    private JavadocCommentOwner owner;

    JavadocComment(JavaccToken t) {
        super(t);
        assert t.kind == JavaTokenKinds.FORMAL_COMMENT;
    }

    void setOwner(JavadocCommentOwner owner) {
        this.owner = owner;
    }

    /**
     * Returns the owner of this comment. Null if this comment is 
     * misplaced.
     */
    public @Nullable JavadocCommentOwner getOwner() {
        return owner;
    }

}
