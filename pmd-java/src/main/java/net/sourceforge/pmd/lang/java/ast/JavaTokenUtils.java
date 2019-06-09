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

    public static JavaccToken newToken(int kind, CharStream charStream) {
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

    public static final class GTToken extends JavaccToken {

        public int realKind;

        /**
         * Constructs a new token for the specified Image and Kind.
         */
        public GTToken(int kind, int realKind, CharSequence image, int startOffset, int endOffset, TokenDocument doc) {
            super(kind, image, startOffset, endOffset, doc);
            this.realKind = realKind;
        }

    }


}
