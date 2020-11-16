/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.ast.impl.TokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.EscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.MalformedSourceException;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Token document for Javacc implementations. This is a helper object
 * for generated token managers. Note: the extension point is a custom
 * implementation of {@link TokenDocumentBehavior}, see {@link JjtreeParserAdapter#tokenBehavior()},
 * {@link JavaCCTokenizer#tokenBehavior()}
 */
public final class JavaccTokenDocument extends TokenDocument<JavaccToken> {

    private final TokenDocumentBehavior behavior;
    private final StringPool stringPool = new StringPool();

    private JavaccToken first;

    public JavaccTokenDocument(TextDocument textDocument, TokenDocumentBehavior behavior) {
        super(textDocument);
        this.behavior = behavior;
    }

    public static class TokenDocumentBehavior {

        public static final TokenDocumentBehavior DEFAULT = new TokenDocumentBehavior();
        private final List<String> tokenNames;
        private final Function<TextDocument, TextDocument> translator;

        private TokenDocumentBehavior() {
            this(Collections.emptyList());
        }

        public TokenDocumentBehavior(List<String> tokenNames) {
            this(tokenNames, t -> t);
        }

        public TokenDocumentBehavior(List<String> tokenNames,
                                     Function<TextDocument, TextDocument> translator) {
            this.tokenNames = tokenNames;
            this.translator = translator;
        }

        /**
         * Returns true if the lexer should accumulate the image of MORE
         * tokens into the StringBuilder jjimage. This is useless in our
         * current implementations. The default returns false, which makes
         * {@link CharStream#appendSuffix(StringBuilder, int)} a noop.
         */
        public boolean useMarkSuffix() {
            return false;
        }

        /**
         * Translate the escapes of the source document. The default implementation
         * does not perform any escaping.
         *
         * @param text Source doc
         *
         * @see EscapeTranslator
         */
        protected TextDocument translate(TextDocument text) throws MalformedSourceException {
            return translator.apply(text);
        }


        protected boolean isImagePooled(JavaccToken t) {
            return false;
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
            if (kind >= 0 && kind < tokenNames.size()) {
                return tokenNames.get(kind);
            }
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
        public JavaccToken createToken(JavaccTokenDocument self, int kind, CharStream cs, @Nullable String image) {
            return new JavaccToken(
                kind,
                image == null ? cs.getTokenImageCs() : image,
                cs.getStartOffset(),
                cs.getEndOffset(),
                self
            );
        }
    }

    boolean useMarkSuffix() {
        return behavior.useMarkSuffix();
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

    String computeImage(JavaccToken t) {
        CharSequence imageCs = t.getImageInternal();
        if (imageCs instanceof String) {
            return (String) imageCs;
        }
        return stringPool.toString(imageCs, behavior.isImagePooled(t));
    }

    /**
     * @see TokenDocumentBehavior#describeKind(int) 
     */
    public @NonNull String describeKind(int kind) {
        return behavior.describeKind(kind);
    }

    /**
     * @see TokenDocumentBehavior#createToken(JavaccTokenDocument, int, CharStream, String)
     */
    public JavaccToken createToken(int kind, CharStream cs, @Nullable String image) {
        return behavior.createToken(this, kind, cs, image);

    }
}
