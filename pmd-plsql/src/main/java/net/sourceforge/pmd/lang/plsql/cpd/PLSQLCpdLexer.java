/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.cpd;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLTokenKinds;

/**
 * <p>Note: This class has been called PLSQLTokenizer in PMD 6</p>.
 */
public class PLSQLCpdLexer extends JavaccCpdLexer {

    private final boolean ignoreIdentifiers;
    private final boolean ignoreLiterals;

    public PLSQLCpdLexer(LanguagePropertyBundle properties) {
        /*
         * The Tokenizer is derived from PLDoc, in which comments are very
         * important When looking for duplication, we are probably not
         * interested in comment variation, so we shall default ignoreComments
         * to true
         */
        ignoreIdentifiers = properties.getProperty(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS);
        ignoreLiterals = properties.getProperty(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS);
    }

    @Override
    protected String getImage(JavaccToken plsqlToken) {
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
        return image;
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return PLSQLTokenKinds.newTokenManager(CharStream.create(doc));
    }
}
