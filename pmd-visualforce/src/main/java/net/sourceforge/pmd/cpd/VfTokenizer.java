/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaEscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.vf.ast.VfTokenKinds;

/**
 * @author sergey.gorbaty
 */
public class VfTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return VfTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return new JavaccTokenDocument.TokenDocumentBehavior(VfTokenKinds.TOKEN_NAMES) {
            @Override
            public TextDocument translate(TextDocument text) throws MalformedSourceException {
                return new JavaEscapeTranslator(text).translateDocument();
            }
        };
    }

}
