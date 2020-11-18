/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.FORMAL_COMMENT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.GT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.IDENTIFIER;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.MULTI_LINE_COMMENT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.RSIGNEDSHIFT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.RUNSIGNEDSHIFT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.SINGLE_LINE_COMMENT;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.JavaEscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.MalformedSourceException;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * {@link JavaccTokenDocument} for Java.
 */
final class JavaTokenDocument extends JavaccTokenDocument.TokenDocumentBehavior {

    static final JavaTokenDocument INSTANCE = new JavaTokenDocument();

    private JavaTokenDocument() {
        super(JavaTokenKinds.TOKEN_NAMES);
    }


    /**
     * Returns true if the given token is a Java comment.
     */
    public static boolean isComment(JavaccToken t) {
        switch (t.kind) {
        case FORMAL_COMMENT:
        case MULTI_LINE_COMMENT:
        case SINGLE_LINE_COMMENT:
            return true;
        default:
            return false;
        }
    }


    @Override
    protected TextDocument translate(TextDocument text) throws MalformedSourceException {
        return new JavaEscapeTranslator(text).translateDocument();
    }


    @Override
    protected boolean isImagePooled(JavaccToken t) {
        return t.kind == IDENTIFIER;
    }

    @Override
    public JavaccToken createToken(JavaccTokenDocument self, int kind, CharStream jcs, @Nullable String image) {
        switch (kind) {
        case RUNSIGNEDSHIFT:
        case RSIGNEDSHIFT:
        case GT:
            return new GTToken(
                GT,
                kind,
                ">",
                jcs.getStartOffset(),
                jcs.getEndOffset(),
                jcs.getTokenDocument()
            );
        default:
            return super.createToken(self, kind, jcs, image);
        }
    }

    static int getRealKind(JavaccToken token) {
        return token instanceof GTToken ? ((GTToken) token).realKind : token.kind;
    }

    private static final class GTToken extends JavaccToken {

        final int realKind;

        GTToken(int kind, int realKind, String image, int startOffset, int endOffset, JavaccTokenDocument doc) {
            super(kind, image, startOffset, endOffset, doc);
            this.realKind = realKind;
        }

    }
}
