/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.io.EscapeAwareReader;
import net.sourceforge.pmd.lang.ast.impl.io.JavaInputReader;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.vf.ast.VfTokenKinds;
import net.sourceforge.pmd.util.document.Chars;
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
            public EscapeAwareReader newReader(Chars text) {
                return new JavaInputReader(text);
            }
        };
    }

}
