/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.cpd;

import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaEscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.visualforce.ast.VfTokenKinds;

/**
 * <p>Note: This class has been called VfTokenizer in PMD 6</p>.
 * @author sergey.gorbaty
 */
public class VfCpdLexer extends JavaccCpdLexer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return VfTokenKinds.newTokenManager(CharStream.create(doc, tokenBehavior()));
    }

    private TokenDocumentBehavior tokenBehavior() {
        return new JavaccTokenDocument.TokenDocumentBehavior(VfTokenKinds.TOKEN_NAMES) {
            @Override
            public TextDocument translate(TextDocument text) throws MalformedSourceException {
                return new JavaEscapeTranslator(text).translateDocument();
            }
        };
    }

}
