/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.MalformedSourceException;
import net.sourceforge.pmd.lang.cpp.ast.CppTokenKinds;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * The C++ tokenizer.
 */
public class CPPTokenizer extends JavaCCTokenizer {

    private boolean skipBlocks;
    private Pattern skipBlocksStart;
    private Pattern skipBlocksEnd;
    private boolean ignoreLiteralSequences = false;

    public CPPTokenizer() {
        setProperties(new Properties()); // set the defaults
    }

    // override to make it visible in tests
    @Override
    protected TokenManager<JavaccToken> getLexerForSource(SourceCode sourceCode) throws IOException {
        return super.getLexerForSource(sourceCode);
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
        ignoreLiteralSequences = Boolean.parseBoolean(properties.getProperty(OPTION_IGNORE_LITERAL_SEQUENCES,
                Boolean.FALSE.toString()));
    }


    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return new TokenDocumentBehavior(CppTokenKinds.TOKEN_NAMES) {

            @Override
            protected TextDocument translate(TextDocument text) throws MalformedSourceException {
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
        return new CppTokenFilter(tokenManager, ignoreLiteralSequences);
    }

    private static class CppTokenFilter extends JavaCCTokenFilter {
        private final boolean ignoreLiteralSequences;
        private JavaccToken discardingLiteralsUntil = null;
        private boolean discardCurrent = false;

        CppTokenFilter(final TokenManager<JavaccToken> tokenManager, final boolean ignoreLiteralSequences) {
            super(tokenManager);
            this.ignoreLiteralSequences = ignoreLiteralSequences;
        }

        @Override
        protected void analyzeTokens(final JavaccToken currentToken, final Iterable<JavaccToken> remainingTokens) {
            discardCurrent = false;
            skipLiteralSequences(currentToken, remainingTokens);
        }

        private void skipLiteralSequences(final JavaccToken currentToken, final Iterable<JavaccToken> remainingTokens) {
            if (ignoreLiteralSequences) {
                final int kind = currentToken.getKind();
                if (isDiscardingLiterals()) {
                    if (currentToken == discardingLiteralsUntil) { // NOPMD - intentional check for reference equality
                        discardingLiteralsUntil = null;
                        discardCurrent = true;
                    }
                } else if (kind == CppTokenKinds.LCURLYBRACE) {
                    final JavaccToken finalToken = findEndOfSequenceOfLiterals(remainingTokens);
                    discardingLiteralsUntil = finalToken;
                }
            }
        }

        private static JavaccToken findEndOfSequenceOfLiterals(final Iterable<JavaccToken> remainingTokens) {
            boolean seenLiteral = false;
            int braceCount = 0;
            for (final JavaccToken token : remainingTokens) {
                switch (token.getKind()) {
                case CppTokenKinds.BINARY_INT_LITERAL:
                case CppTokenKinds.DECIMAL_INT_LITERAL:
                case CppTokenKinds.FLOAT_LITERAL:
                case CppTokenKinds.HEXADECIMAL_INT_LITERAL:
                case CppTokenKinds.OCTAL_INT_LITERAL:
                case CppTokenKinds.ZERO:
                    seenLiteral = true;
                    break; // can be skipped; continue to the next token
                case CppTokenKinds.COMMA:
                    break; // can be skipped; continue to the next token
                case CppTokenKinds.LCURLYBRACE:
                    braceCount++;
                    break; // curly braces are allowed, as long as they're balanced
                case CppTokenKinds.RCURLYBRACE:
                    braceCount--;
                    if (braceCount < 0) {
                        // end of the list; skip all contents
                        return seenLiteral ? token : null;
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

        private boolean isDiscardingLiterals() {
            return discardingLiteralsUntil != null;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return isDiscardingLiterals() || discardCurrent;
        }
    }
}
