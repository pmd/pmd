/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.EscapeAwareReader;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaInputReader;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.jsp.ast.JspTokenKinds;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;

public class JSPTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return JspTokenKinds.newTokenManager(sourceCode);
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
