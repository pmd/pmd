/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.cpp.ast.CppCharStream;
import net.sourceforge.pmd.lang.cpp.ast.CppTokenKinds;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.cpp.CppTokenManager;
import net.sourceforge.pmd.lang.cpp.ast.CppParserConstants;
import net.sourceforge.pmd.util.IOUtil;

/**
 * The C++ tokenizer.
 */
public class CPPTokenizer extends JavaCCTokenizer {

    private boolean skipBlocks;
    private String skipBlocksStart;
    private String skipBlocksEnd;
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
            skipBlocksStart = split[0];
            if (split.length == 1) {
                skipBlocksEnd = split[0];
            } else {
                skipBlocksEnd = split[1];
            }
        }
        ignoreLiteralSequences = Boolean.parseBoolean(properties.getProperty(OPTION_IGNORE_LITERAL_SEQUENCES,
                Boolean.FALSE.toString()));
    }

    private String maybeSkipBlocks(String test) throws IOException {
        if (!skipBlocks) {
            return test;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(test))) {
            StringBuilder filtered = new StringBuilder(test.length());
            String line;
            boolean skip = false;
            while ((line = reader.readLine()) != null) {
                if (skipBlocksStart.equalsIgnoreCase(line.trim())) {
                    skip = true;
                } else if (skip && skipBlocksEnd.equalsIgnoreCase(line.trim())) {
                    skip = false;
                }
                if (!skip) {
                    filtered.append(line);
                }
                // always add a new line to keep the line-numbering
                filtered.append(PMD.EOL);
            }
            return filtered.toString();
        }
    }


    @Override
    protected CharStream makeCharStream(Reader sourceCode) {
        return CppCharStream.newCppCharStream(sourceCode);
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return CppTokenKinds.newTokenManager(sourceCode);
    }

    @SuppressWarnings("PMD.CloseResource")
    @Override
    protected TokenManager<JavaccToken> getLexerForSource(SourceCode sourceCode) throws IOException {
        Reader reader = IOUtil.skipBOM(new StringReader(maybeSkipBlocks(sourceCode.getCodeBuffer().toString())));
        CharStream charStream = makeCharStream(reader);
        return makeLexerImpl(charStream);
    }

    @Override
    protected TokenFilter getTokenFilter(final TokenManager tokenManager) {
        return new CppTokenFilter(tokenManager, ignoreLiteralSequences);
    }

    private static class CppTokenFilter extends JavaCCTokenFilter {
        private final boolean ignoreLiteralSequences;
        private GenericToken discardingLiteralsUntil = null;
        private boolean discardCurrent = false;

        CppTokenFilter(final TokenManager tokenManager, final boolean ignoreLiteralSequences) {
            super(tokenManager);
            this.ignoreLiteralSequences = ignoreLiteralSequences;
        }

        @Override
        protected void analyzeTokens(final GenericToken currentToken, final Iterable<GenericToken> remainingTokens) {
            discardCurrent = false;
            skipLiteralSequences(currentToken, remainingTokens);
        }

        private void skipLiteralSequences(final GenericToken currentToken, final Iterable<GenericToken> remainingTokens) {
            if (ignoreLiteralSequences) {
                final int kind = currentToken.getKind();
                if (isDiscardingLiterals()) {
                    if (currentToken == discardingLiteralsUntil) { // NOPMD - intentional check for reference equality
                        discardingLiteralsUntil = null;
                        discardCurrent = true;
                    }
                } else if (kind == CppParserConstants.LCURLYBRACE) {
                    final GenericToken finalToken = findEndOfSequenceOfLiterals(remainingTokens);
                    discardingLiteralsUntil = finalToken;
                }
            }
        }

        private static GenericToken findEndOfSequenceOfLiterals(final Iterable<GenericToken> remainingTokens) {
            boolean seenLiteral = false;
            int braceCount = 0;
            for (final GenericToken token : remainingTokens) {
                switch (token.getKind()) {
                case CppParserConstants.BINARY_INT_LITERAL:
                case CppParserConstants.DECIMAL_INT_LITERAL:
                case CppParserConstants.FLOAT_LITERAL:
                case CppParserConstants.HEXADECIMAL_INT_LITERAL:
                case CppParserConstants.OCTAL_INT_LITERAL:
                case CppParserConstants.ZERO:
                    seenLiteral = true;
                    break; // can be skipped; continue to the next token
                case CppParserConstants.COMMA:
                    break; // can be skipped; continue to the next token
                case CppParserConstants.LCURLYBRACE:
                    braceCount++;
                    break; // curly braces are allowed, as long as they're balanced
                case CppParserConstants.RCURLYBRACE:
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
