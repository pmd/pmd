/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLTokenKinds;

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
    protected TokenEntry processToken(Tokens tokenEntries, JavaccToken plsqlToken, String fileName) {
        String image = plsqlToken.getImage();

        if (ignoreIdentifiers && plsqlToken.kind == PLSQLTokenKinds.IDENTIFIER) {
            image = String.valueOf(plsqlToken.kind);
        }

        if (ignoreLiterals && (plsqlToken.kind == PLSQLTokenKinds.UNSIGNED_NUMERIC_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.FLOAT_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.INTEGER_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.CHARACTER_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.STRING_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.QUOTED_LITERAL)) {
            image = String.valueOf(plsqlToken.kind);
        }

        return new TokenEntry(image, fileName, plsqlToken.getBeginLine(),
                              plsqlToken.getBeginColumn(), plsqlToken.getEndColumn());
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return PLSQLTokenKinds.newTokenManager(sourceCode);
    }
}
