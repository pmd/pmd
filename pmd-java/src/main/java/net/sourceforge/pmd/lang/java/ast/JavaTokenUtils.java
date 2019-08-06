/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.JavaCharStream;
import net.sourceforge.pmd.lang.ast.impl.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.TokenDocument;

final class JavaTokenUtils {

    private JavaTokenUtils() {

    }

    static JavaccToken newToken(int kind, CharStream charStream) {
        JavaCharStream jcs = (JavaCharStream) charStream;

        String image = JavaParserTokenManager.jjstrLiteralImages[kind];

        switch (kind) {
        case JavaParserConstants.RUNSIGNEDSHIFT:
        case JavaParserConstants.RSIGNEDSHIFT:
        case JavaParserConstants.GT:
            return new GTToken(
                JavaParserConstants.GT,
                kind,
                ">",
                jcs.getStartOffset(),
                jcs.getEndOffset(),
                jcs.getTokenDocument()
            );
        case JavaParserConstants.WHITESPACE:
            // unlikely that anybody cares about that, and since
            // they're still 30% of all tokens best make this assumption

            // btw 40% of all tokens have a compile-time string constant
            // as image (jjstrLiteralImages) so they're shared.
            return new LazyImageToken(
                kind,
                jcs.getStartOffset(),
                jcs.getEndOffset(),
                jcs.getTokenDocument()
            );
        default:
            return new JavaccToken(
                kind,
                image == null ? charStream.GetImage() : image,
                jcs.getStartOffset(),
                jcs.getEndOffset(),
                jcs.getTokenDocument()
            );
        }
    }

    static int getRealKind(JavaccToken token) {
        return token instanceof GTToken ? ((GTToken) token).realKind : token.kind;
    }

    private static final class LazyImageToken extends JavaccToken {

        public LazyImageToken(int kind, int startInclusive, int endExclusive, TokenDocument document) {
            super(kind, null, startInclusive, endExclusive, document);
        }

        @Override
        public String getImage() {
            return document.getFullText().substring(getStartInDocument(), getEndInDocument());
        }
    }

    private static final class GTToken extends JavaccToken {

        final int realKind;

        /**
         * Constructs a new token for the specified Image and Kind.
         */
        GTToken(int kind, int realKind, CharSequence image, int startOffset, int endOffset, TokenDocument doc) {
            super(kind, image, startOffset, endOffset, doc);
            this.realKind = realKind;
        }

    }


}
