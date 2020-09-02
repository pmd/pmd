/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.python.ast.PythonTokenKinds;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * The Python tokenizer.
 */
public class PythonTokenizer extends JavaCCTokenizer {

    private static final Pattern STRING_NL_ESCAPE = Pattern.compile("\\\\\\r?\\n");

    private static final TokenDocumentBehavior TOKEN_BEHAVIOR = new TokenDocumentBehavior(PythonTokenKinds.TOKEN_NAMES);

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return PythonTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected JavaccTokenDocument.TokenDocumentBehavior tokenBehavior() {
        return TOKEN_BEHAVIOR;
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
