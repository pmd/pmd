/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.cpd;

import java.util.Locale;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.Chars;
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
        String image;

        if (ignoreIdentifiers && plsqlToken.kind == PLSQLTokenKinds.IDENTIFIER) {
            image = "<identifier>";
        } else if (ignoreLiterals && plsqlToken.kind == PLSQLTokenKinds.STRING_LITERAL) {
            // Javacc gives it the wrong name in PLSQLTokenKinds.describe
            return "<STRING_LITERAL>";
        } else if (ignoreLiterals && (plsqlToken.kind == PLSQLTokenKinds.UNSIGNED_NUMERIC_LITERAL
            || plsqlToken.kind == PLSQLTokenKinds.FLOAT_LITERAL
            || plsqlToken.kind == PLSQLTokenKinds.INTEGER_LITERAL
            || plsqlToken.kind == PLSQLTokenKinds.CHARACTER_LITERAL
            || plsqlToken.kind == PLSQLTokenKinds.QUOTED_LITERAL)) {
            // the token kind is preserved
            image = PLSQLTokenKinds.describe(plsqlToken.kind);
        } else if (plsqlToken.kind != PLSQLTokenKinds.CHARACTER_LITERAL
            && plsqlToken.kind != PLSQLTokenKinds.STRING_LITERAL
            && plsqlToken.kind != PLSQLTokenKinds.QUOTED_LITERAL) {

            Chars imageCs = plsqlToken.getImageCs();
            if (plsqlToken.kind == PLSQLTokenKinds.IDENTIFIER && imageCs.charAt(0) == '"') {
                // remove quotes to make identical to bare ID
                image = imageCs.substring(1, imageCs.length() - 1);
            } else {
                image = plsqlToken.getImage();
            }

            // PLSQL is case-insensitive, but the contents of
            // string literals and the like are case-sensitive.
            // Note: tokens are normalized to uppercase make CPD case-insensitive.
            // We use uppercase and not lowercase because that way, PLSQL keywords
            // will be returned unchanged (they are already uppercase, see PLSQLParser),
            // therefore creating fewer strings in memory.
            image = image.toUpperCase(Locale.ROOT);

        } else {
            image = plsqlToken.getImage();
        }
        return image;
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return PLSQLTokenKinds.newTokenManager(CharStream.create(doc));
    }
}
