/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.Properties;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.plsql.PLSQLTokenManager;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserConstants;
import net.sourceforge.pmd.lang.plsql.ast.Token;
import net.sourceforge.pmd.util.IOUtil;

public class PLSQLTokenizer extends JavaCCTokenizer {
    // This is actually useless, the comments are special tokens, never taken into account by CPD
    @Deprecated
    public static final String IGNORE_COMMENTS = "ignore_comments";
    public static final String IGNORE_IDENTIFIERS = "ignore_identifiers";
    public static final String IGNORE_LITERALS = "ignore_literals";

    private boolean ignoreIdentifiers;
    private boolean ignoreLiterals;

    public void setProperties(Properties properties) {
        /*
         * The Tokenizer is derived from PLDoc, in which comments are very
         * important When looking for duplication, we are probably not
         * interested in comment variation, so we shall default ignoreComments
         * to true
         */
        ignoreIdentifiers = Boolean.parseBoolean(properties.getProperty(IGNORE_IDENTIFIERS, "false"));
        ignoreLiterals = Boolean.parseBoolean(properties.getProperty(IGNORE_LITERALS, "false"));
    }

    @Deprecated
    public void setIgnoreComments(boolean ignore) {
        // This is actually useless, the comments are special tokens, never taken into account by CPD
    }

    public void setIgnoreLiterals(boolean ignore) {
        this.ignoreLiterals = ignore;
    }

    public void setIgnoreIdentifiers(boolean ignore) {
        this.ignoreIdentifiers = ignore;
    }

    @Override
    protected TokenEntry processToken(Tokens tokenEntries, GenericToken currentToken, String fileName) {
        String image = currentToken.getImage();

        Token plsqlToken = (Token) currentToken;

        if (ignoreIdentifiers && plsqlToken.kind == PLSQLParserConstants.IDENTIFIER) {
            image = String.valueOf(plsqlToken.kind);
        }

        if (ignoreLiterals && (plsqlToken.kind == PLSQLParserConstants.UNSIGNED_NUMERIC_LITERAL
                || plsqlToken.kind == PLSQLParserConstants.FLOAT_LITERAL
                || plsqlToken.kind == PLSQLParserConstants.INTEGER_LITERAL
                || plsqlToken.kind == PLSQLParserConstants.CHARACTER_LITERAL
                || plsqlToken.kind == PLSQLParserConstants.STRING_LITERAL
                || plsqlToken.kind == PLSQLParserConstants.QUOTED_LITERAL)) {
            image = String.valueOf(plsqlToken.kind);
        }

        return new TokenEntry(image, fileName, currentToken.getBeginLine(),
                currentToken.getBeginColumn(), currentToken.getEndColumn());
    }

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        StringBuilder stringBuilder = sourceCode.getCodeBuffer();
        return new PLSQLTokenManager(IOUtil.skipBOM(new StringReader(stringBuilder.toString())));
    }
}
