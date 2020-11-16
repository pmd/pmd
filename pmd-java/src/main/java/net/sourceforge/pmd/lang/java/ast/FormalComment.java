/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

public class FormalComment extends Comment {

    private JavadocCommentOwner owner;

    public FormalComment(JavaccToken t) {
        super(t);
        assert t.kind == JavaTokenKinds.FORMAL_COMMENT;
    }

    void setOwner(JavadocCommentOwner owner) {
        this.owner = owner;
    }

    public @Nullable JavadocCommentOwner getOwner() {
        return owner;
    }

}
