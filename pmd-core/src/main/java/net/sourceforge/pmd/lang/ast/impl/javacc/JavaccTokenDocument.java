/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.TokenDocument;

/**
 * Token document for Javacc implementations.
 */
public class JavaccTokenDocument extends TokenDocument<JavaccToken> {


    public JavaccTokenDocument(String fullText) {
        super(fullText);
    }


    protected String describeKind(int kind) {
        if (kind == JavaccToken.IMPLICIT_TOKEN) {
            return "implicit token";
        }
        return "token of kind " + kind;
    }

    public JavaccToken createToken(int kind, CharStream cs, @Nullable String image) {
        return new JavaccToken(
            kind,
            image == null ? cs.GetImage() : image,
            cs.getStartOffset(),
            cs.getEndOffset(),
            this
        );
    }
}
