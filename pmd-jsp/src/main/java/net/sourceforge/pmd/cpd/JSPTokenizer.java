/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.JavaEscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.MalformedSourceException;
import net.sourceforge.pmd.lang.jsp.ast.JspParser;
import net.sourceforge.pmd.lang.jsp.ast.JspTokenKinds;
import net.sourceforge.pmd.util.document.TextDocument;

public class JSPTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return JspTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected JavaccTokenDocument.TokenDocumentBehavior newTokenDoc() {
        return JspParser.getTokenBehavior();
    }

}
