/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.python.cpd;

import java.util.regex.Pattern;

import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.python.ast.PythonTokenKinds;

/**
 * The Python tokenizer.
 *
 * <p>Note: This class has been called PythonTokenizer in PMD 6</p>.
 */
public class PythonCpdLexer extends JavaccCpdLexer {

    private static final Pattern STRING_NL_ESCAPE = Pattern.compile("\\\\\\r?\\n");

    private static final TokenDocumentBehavior TOKEN_BEHAVIOR = new TokenDocumentBehavior(PythonTokenKinds.TOKEN_NAMES);

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return PythonTokenKinds.newTokenManager(CharStream.create(doc, TOKEN_BEHAVIOR));
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
