/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.Reader;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.python.ast.PythonTokenKinds;

/**
 * The Python tokenizer.
 */
public class PythonTokenizer extends JavaCCTokenizer {

    private static final Pattern STRING_NL_ESCAPE = Pattern.compile("\\\\\\r?\\n");

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return PythonTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected CharStream makeCharStream(Reader sourceCode) {
        return CharStreamFactory.simpleCharStream(sourceCode, PythonTokenDocument::new);
    }

    private static class PythonTokenDocument extends JavaccTokenDocument {

        PythonTokenDocument(String fullText) {
            super(fullText);
        }

        @Override
        protected @Nullable String describeKindImpl(int kind) {
            return PythonTokenKinds.describe(kind);
        }

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
