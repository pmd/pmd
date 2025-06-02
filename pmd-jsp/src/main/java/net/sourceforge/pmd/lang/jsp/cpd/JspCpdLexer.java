/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.cpd;

import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.jsp.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.jsp.ast.JspTokenKinds;

/**
 * <p>Note: This class has been called JSPTokenizer in PMD 6</p>.
 */
public class JspCpdLexer extends JavaccCpdLexer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return JspTokenKinds.newTokenManager(CharStream.create(doc, InternalApiBridge.getJspTokenBehavior()));
    }

}
