/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import net.sourceforge.pmd.SourceCode;
import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.jsp.JspTokenManager;

public class JSPTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        return new JspTokenManager(new StringReader(sourceCode.getCodeBuffer().toString()));
    }
}
