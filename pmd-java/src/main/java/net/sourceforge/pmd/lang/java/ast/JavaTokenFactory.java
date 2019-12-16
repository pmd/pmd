/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.TokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaCharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

final class JavaTokenFactory {

    private JavaTokenFactory() {

    }

    static JavaccToken newToken(int kind, CharStream charStream) {
        JavaCharStream jcs = (JavaCharStream) charStream;

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
            // We don't create a new string for the image of whitespace tokens eagerly

            // It's unlikely that anybody cares about that, and since
            // they're still 30% of all tokens this is advantageous
            return new LazyImageToken(
                kind,
                jcs.getStartOffset(),
                jcs.getEndOffset(),
                jcs.getTokenDocument()
            );

        default:
            // Most tokens have an entry in there, it's used to share the
            // image string for keywords & punctuation. Those represent ~40%
            // of token instances
            String image = JavaParserTokenManager.jjstrLiteralImages[kind];

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

        LazyImageToken(int kind, int startInclusive, int endExclusive, TokenDocument document) {
            super(kind, null, startInclusive, endExclusive, document);
        }

        @Override
        public String getImage() {
            return document.getFullText().substring(getStartInDocument(), getEndInDocument());
        }
    }

    private static final class GTToken extends JavaccToken {

        final int realKind;

        GTToken(int kind, int realKind, CharSequence image, int startOffset, int endOffset, TokenDocument doc) {
            super(kind, image, startOffset, endOffset, doc);
            this.realKind = realKind;
        }

    }


}
