/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.regex.Pattern;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.python.ast.PythonTokenKinds;
import net.sourceforge.pmd.lang.python.ast.PythonTokenManager;
import net.sourceforge.pmd.util.IOUtil;

/**
 * The Python tokenizer.
 */
public class PythonTokenizer extends JavaCCTokenizer {

    private static final Pattern STRING_NL_ESCAPE = Pattern.compile("\\\\\\r?\\n");

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        StringBuilder buffer = sourceCode.getCodeBuffer();
        return new PythonTokenManager(IOUtil.skipBOM(new StringReader(buffer.toString())));
    }

    @Override
    protected String getImage(JavaccToken token) {
        switch (token.kind) {
        case PythonTokenKinds.SINGLE_STRING:
        case PythonTokenKinds.SINGLE_STRING2:
        case PythonTokenKinds.SINGLE_BSTRING:
        case PythonTokenKinds.SINGLE_BSTRING2:
        case PythonTokenKinds.SINGLE_USTRING:
        case PythonTokenKinds.SINGLE_USTRING2:
            // linebreak escapes, only for single-quoted strings
            // todo other escapes?
            return STRING_NL_ESCAPE.matcher(token.getImage()).replaceAll("");
        default:
            return token.getImage();
        }
    }

}
