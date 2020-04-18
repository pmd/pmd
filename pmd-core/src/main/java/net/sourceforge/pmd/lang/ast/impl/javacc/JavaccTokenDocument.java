/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.TokenDocument;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Token document for Javacc implementations. This is a helper object
 * for generated token managers.
 */
public class JavaccTokenDocument extends TokenDocument<JavaccToken> {

    private JavaccToken first;

    public JavaccTokenDocument(TextDocument textDocument) {
        super(textDocument);
    }


    public EscapeAwareReader newReader(Chars text) {
        return new EscapeAwareReader(text);
    }


    /**
     * Open the document. This is only meant to be used by a Javacc-generated
     * parser.
     *
     * @return The token for the document start. This token is implicit and
     *     will never end up in the final token chain.
     *
     * @throws IllegalStateException If the document has already been opened
     */
    public JavaccToken open() {
        synchronized (this) {
            if (first != null) {
                throw new RuntimeException("Document is already opened");
            }
            first = JavaccToken.newImplicit(0, this);
        }
        return first;
    }


    @Override
    public JavaccToken getFirstToken() {
        if (first == null || first.next == null) {
            throw new IllegalStateException("Document has not been opened");
        }
        return first.next;
    }

    /**
     * Returns a string that describes the token kind.
     *
     * @param kind Kind of token
     *
     * @return A descriptive string
     */
    public final @NonNull String describeKind(int kind) {
        if (kind == JavaccToken.IMPLICIT_TOKEN) {
            return "<implicit token>";
        }
        String impl = describeKindImpl(kind);
        if (impl != null) {
            return impl;
        }
        return "<token of kind " + kind + ">";
    }

    /**
     * Describe the given kind. If this returns a non-null value, then
     * that's what {@link #describeKind(int)} will use. Otherwise a default
     * implementation is used.
     *
     * <p>An implementation typically uses the JavaCC-generated array
     * named {@code <parser name>Constants.tokenImage}. Remember to
     * check the bounds of the array.
     *
     * @param kind Kind of token
     *
     * @return A descriptive string, or null to use default
     */
    protected @Nullable String describeKindImpl(int kind) {
        return null;
    }


    /**
     * Creates a new token with the given kind. This is called back to
     * by JavaCC-generated token managers (jjFillToken). Note that a
     * created token is not guaranteed to end up in the final token chain.
     *
     * @param kind  Kind of the token
     * @param cs    Char stream of the file. This can be used to get text
     *              coordinates and the image
     * @param image Shared instance of the image token. If this is non-null,
     *              then no call to {@link CharStream#getTokenImage()} should be
     *              issued.
     *
     * @return A new token
     */
    public JavaccToken createToken(int kind, CharStream cs, @Nullable String image) {
        return new JavaccToken(
            kind,
            image == null ? cs.getTokenImage() : image,
            cs.getStartOffset(),
            cs.getEndOffset(),
            this
        );
    }
}
