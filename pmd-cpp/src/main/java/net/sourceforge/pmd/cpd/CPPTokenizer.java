/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;
import java.util.regex.Pattern;

import net.sourceforge.pmd.cpd.impl.JavaCCTokenizer;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException;
import net.sourceforge.pmd.lang.cpp.ast.CppTokenKinds;
import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * The C++ tokenizer.
 */
public class CPPTokenizer extends JavaCCTokenizer {

    private boolean skipBlocks;
    private Pattern skipBlocksStart;
    private Pattern skipBlocksEnd;
    private boolean ignoreIdentifierAndLiteralSeqences = false;
    private boolean ignoreLiteralSequences = false;

    public CPPTokenizer() {
        setProperties(new Properties()); // set the defaults
    }

    /**
     * Sets the possible options for the C++ tokenizer.
     *
     * @param properties the properties
     * @see #OPTION_SKIP_BLOCKS
     * @see #OPTION_SKIP_BLOCKS_PATTERN
     * @see #OPTION_IGNORE_LITERAL_SEQUENCES
     */
    public void setProperties(Properties properties) {
        skipBlocks = Boolean.parseBoolean(properties.getProperty(OPTION_SKIP_BLOCKS, Boolean.TRUE.toString()));
        if (skipBlocks) {
            String skipBlocksPattern = properties.getProperty(OPTION_SKIP_BLOCKS_PATTERN, DEFAULT_SKIP_BLOCKS_PATTERN);
            String[] split = skipBlocksPattern.split("\\|", 2);
            skipBlocksStart = CppBlockSkipper.compileSkipMarker(split[0]);
            if (split.length == 1) {
                skipBlocksEnd = skipBlocksStart;
            } else {
                skipBlocksEnd = CppBlockSkipper.compileSkipMarker(split[1]);
            }
        }
        ignoreLiteralSequences = Boolean.parseBoolean(properties.getProperty(OPTION_IGNORE_LITERAL_SEQUENCES, Boolean.FALSE.toString()));
        ignoreIdentifierAndLiteralSeqences = 
            Boolean.parseBoolean(properties.getProperty(OPTION_IGNORE_IDENTIFIER_AND_LITERAL_SEQUENCES, Boolean.FALSE.toString()));
    }


    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return new TokenDocumentBehavior(CppTokenKinds.TOKEN_NAMES) {

            @Override
            public TextDocument translate(TextDocument text) throws MalformedSourceException {
                if (skipBlocks) {
                    text = new CppBlockSkipper(text, skipBlocksStart, skipBlocksEnd).translateDocument();
                }
                return new CppEscapeTranslator(text).translateDocument();
            }
        };
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return CppTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected TokenFilter<JavaccToken> getTokenFilter(final TokenManager<JavaccToken> tokenManager) {
        return new CppTokenFilter(tokenManager, ignoreLiteralSequences, ignoreIdentifierAndLiteralSeqences);
    }

    private static class CppTokenFilter extends JavaCCTokenFilter {
        private final boolean ignoreLiteralSequences;
        private final boolean ignoreIdentifierAndLiteralSeqences;
        private JavaccToken discardingTokensUntil = null;
        private boolean discardCurrent = false;

        CppTokenFilter(final TokenManager<JavaccToken> tokenManager, final boolean ignoreLiteralSequences, final boolean ignoreIdentifierAndLiteralSeqences) {
            super(tokenManager);
            this.ignoreIdentifierAndLiteralSeqences = ignoreIdentifierAndLiteralSeqences;
            this.ignoreLiteralSequences = ignoreLiteralSequences;
        }

        @Override
        protected void analyzeTokens(final JavaccToken currentToken, final Iterable<JavaccToken> remainingTokens) {
            discardCurrent = false;
            skipSequences(currentToken, remainingTokens);
        }

        private void skipSequences(final JavaccToken currentToken, final Iterable<JavaccToken> remainingTokens) {
            if (ignoreLiteralSequences || ignoreIdentifierAndLiteralSeqences) {
                final int kind = currentToken.getKind();
                if (isDiscardingToken()) {
                    if (currentToken == discardingTokensUntil) { // NOPMD - intentional check for reference equality
                        discardingTokensUntil = null;
                        discardCurrent = true;
                    }
                } else if (kind == CppTokenKinds.LCURLYBRACE) {
                    final JavaccToken finalToken = findEndOfSequenceToDiscard(remainingTokens, ignoreIdentifierAndLiteralSeqences);
                    discardingTokensUntil = finalToken;
                }
            }
        }

        private static JavaccToken findEndOfSequenceToDiscard(final Iterable<JavaccToken> remainingTokens, boolean ignoreIdentifierAndLiteralSeqences) {
            boolean seenAllowedToken = false;
            int braceCount = 0;
            for (final JavaccToken token : remainingTokens) {
                switch (token.getKind()) {
                case CppTokenKinds.BINARY_INT_LITERAL:
                case CppTokenKinds.DECIMAL_INT_LITERAL:
                case CppTokenKinds.FLOAT_LITERAL:
                case CppTokenKinds.HEXADECIMAL_INT_LITERAL:
                case CppTokenKinds.OCTAL_INT_LITERAL:
                case CppTokenKinds.ZERO:
                case CppTokenKinds.STRING:
                    seenAllowedToken = true;
                    break; // can be skipped; continue to the next token
                case CppTokenKinds.ID:
                    // Ignore identifiers if instructed
                    if (ignoreIdentifierAndLiteralSeqences) {
                        seenAllowedToken = true;
                        break; // can be skipped; continue to the next token
                    } else {
                        // token not expected, other than identifier
                        return null;
                    }
                case CppTokenKinds.COMMA:
                    break; // can be skipped; continue to the next token
                case CppTokenKinds.LCURLYBRACE:
                    braceCount++;
                    break; // curly braces are allowed, as long as they're balanced
                case CppTokenKinds.RCURLYBRACE:
                    braceCount--;
                    if (braceCount < 0) {
                        // end of the list; skip all contents
                        return seenAllowedToken ? token : null;
                    } else {
                        // curly braces are not yet balanced; continue to the next token
                        break;
                    }
                default:
                    // some other token than the expected ones; this is not a sequence of literals
                    return null;
                }
            }
            return null;
        }

        private boolean isDiscardingToken() {
            return discardingTokensUntil != null;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return isDiscardingToken() || discardCurrent;
        }
    }
}
