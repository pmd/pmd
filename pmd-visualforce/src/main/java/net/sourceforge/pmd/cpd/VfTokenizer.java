/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.JavaEscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.MalformedSourceException;
import net.sourceforge.pmd.lang.vf.ast.VfTokenKinds;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * @author sergey.gorbaty
 */
public class VfTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return VfTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected JavaccTokenDocument newTokenDoc(TextDocument textDoc) {
        return new JavaccTokenDocument(textDoc) {

            @Override
            protected TextDocument translate(TextDocument text) throws MalformedSourceException {
                try (JavaEscapeTranslator translator = new JavaEscapeTranslator(text)) {
                    return translator.translateDocument();
                }
            }
        };
    }

}
