/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab.cpd;

import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.matlab.ast.MatlabTokenKinds;

/**
 * The Matlab Tokenizer.
 *
 * <p>Note: This class has been called MatlabTokenizer in PMD 6</p>.
 */
public class MatlabCpdLexer extends JavaccCpdLexer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return MatlabTokenKinds.newTokenManager(CharStream.create(doc));
    }
}
