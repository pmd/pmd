/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.matlab.ast.MatlabTokenKinds;

/**
 * The Matlab Tokenizer.
 */
public class MatlabTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return MatlabTokenKinds.newTokenManager(doc);
    }
}
