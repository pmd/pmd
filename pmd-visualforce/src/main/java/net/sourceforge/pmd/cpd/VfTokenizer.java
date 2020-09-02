/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.EscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.JavaEscapeTranslator;
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
    protected TokenDocumentBehavior newTokenDoc() {
        return new JavaccTokenDocument.TokenDocumentBehavior(VfTokenKinds.TOKEN_NAMES,
                                                             EscapeTranslator.translatorFor(JavaEscapeTranslator::new));
    }

}
