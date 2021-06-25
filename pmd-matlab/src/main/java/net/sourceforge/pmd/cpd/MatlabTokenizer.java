/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.matlab.MatlabTokenManager;
import net.sourceforge.pmd.util.IOUtil;

/**
 * The Matlab Tokenizer.
 */
public class MatlabTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        return new MatlabTokenManager(IOUtil.skipBOM(new StringReader(buffer.toString())));
    }
}
