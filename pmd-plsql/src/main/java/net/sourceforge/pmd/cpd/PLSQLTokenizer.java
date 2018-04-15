/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.plsql.PLSQLTokenManager;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserConstants;
import net.sourceforge.pmd.lang.plsql.ast.Token;

public class PLSQLTokenizer implements Tokenizer {
    private static final Logger LOGGER = Logger.getLogger(PLSQLTokenizer.class.getName());

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

    /**
     * Read Reader from SourceCode and output an ordered tree of PLSQL tokens.
     * 
     * @param sourceCode
     *            PLSQL source in file, string or database (any suitable object
     *            that can return a Reader).
     * @param tokenEntries
     *            Derived based on PLSQL Abstract Syntax Tree (derived from
     *            PLDOc parser.)
     */
    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        long encounteredTokens = 0;
        long addedTokens = 0;

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("PLSQLTokenizer: ignoreIdentifiers==" + ignoreIdentifiers);
            LOGGER.fine("PLSQLTokenizer: ignoreLiterals==" + ignoreLiterals);
        }

        String fileName = sourceCode.getFileName();
        StringBuilder sb = sourceCode.getCodeBuffer();

        TokenFilter tokenFilter = new JavaCCTokenFilter(new PLSQLTokenManager(new StringReader(sb.toString())));
        Token currentToken = (Token) tokenFilter.getNextToken();
        while (currentToken != null) {
            String image = currentToken.image;

            encounteredTokens++;

            if (ignoreIdentifiers && currentToken.kind == PLSQLParserConstants.IDENTIFIER) {
                image = String.valueOf(currentToken.kind);
            }

            if (ignoreLiterals && (currentToken.kind == PLSQLParserConstants.UNSIGNED_NUMERIC_LITERAL
                    || currentToken.kind == PLSQLParserConstants.FLOAT_LITERAL
                    || currentToken.kind == PLSQLParserConstants.INTEGER_LITERAL
                    || currentToken.kind == PLSQLParserConstants.CHARACTER_LITERAL
                    || currentToken.kind == PLSQLParserConstants.STRING_LITERAL
                    || currentToken.kind == PLSQLParserConstants.QUOTED_LITERAL)) {
                image = String.valueOf(currentToken.kind);
            }

            tokenEntries.add(new TokenEntry(image, fileName, currentToken.beginLine));
            addedTokens++;
            currentToken = (Token) tokenFilter.getNextToken();
        }
        tokenEntries.add(TokenEntry.getEOF());
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(sourceCode.getFileName() + ": encountered " + encounteredTokens + " tokens;" + " added "
                    + addedTokens + " tokens");
        }
    }

}
