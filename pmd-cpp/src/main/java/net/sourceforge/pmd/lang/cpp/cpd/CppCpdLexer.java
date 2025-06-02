/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.cpd;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.cpd.impl.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException;
import net.sourceforge.pmd.lang.cpp.CppLanguageModule;
import net.sourceforge.pmd.lang.cpp.ast.CppTokenKinds;
import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * The C++ tokenizer.
 *
 * <p>Note: This class has been called CPPTokenizer in PMD 6</p>.
 */
public class CppCpdLexer extends JavaccCpdLexer {

    private boolean skipBlocks;
    private Pattern skipBlocksStart;
    private Pattern skipBlocksEnd;
    private final boolean ignoreIdentifierAndLiteralSeqences;
    private final boolean ignoreLiteralSequences;
    private final boolean ignoreLiterals;
    private final boolean ignoreIdentifiers;

    public CppCpdLexer(LanguagePropertyBundle cppProperties) {
        ignoreLiteralSequences = cppProperties.getProperty(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES);
        ignoreIdentifierAndLiteralSeqences = cppProperties.getProperty(CpdLanguageProperties.CPD_IGNORE_LITERAL_AND_IDENTIFIER_SEQUENCES);
        ignoreLiterals = cppProperties.getProperty(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS);
        ignoreIdentifiers = cppProperties.getProperty(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS);
        String skipBlocksPattern = cppProperties.getProperty(CppLanguageModule.CPD_SKIP_BLOCKS);
        if (StringUtils.isNotBlank(skipBlocksPattern)) {
            skipBlocks = true;
            String[] split = skipBlocksPattern.split("\\|", 2);
            skipBlocksStart = CppBlockSkipper.compileSkipMarker(split[0]);
            if (split.length == 1) {
                skipBlocksEnd = skipBlocksStart;
            } else {
                skipBlocksEnd = CppBlockSkipper.compileSkipMarker(split[1]);
            }
        }
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return CppTokenKinds.newTokenManager(newCharStream(doc));
    }

    CharStream newCharStream(TextDocument doc) {
        return CharStream.create(doc, new TokenDocumentBehavior(CppTokenKinds.TOKEN_NAMES) {

            @Override
            public TextDocument translate(TextDocument text) throws MalformedSourceException {
                if (skipBlocks) {
                    text = new CppBlockSkipper(text, skipBlocksStart, skipBlocksEnd).translateDocument();
                }
                return new CppEscapeTranslator(text).translateDocument();
            }
        });
    }

    @Override
    protected TokenManager<JavaccToken> filterTokenStream(final TokenManager<JavaccToken> tokenManager) {
        return new CppTokenFilter(tokenManager, ignoreLiteralSequences, ignoreIdentifierAndLiteralSeqences);
    }

    @Override
    protected void processToken(TokenFactory tokenEntries, JavaccToken currentToken) {
        int kind = currentToken.getKind();
        String image = currentToken.getImage();

        boolean isLiteral = kind == CppTokenKinds.STRING || kind == CppTokenKinds.RSTRING || kind == CppTokenKinds.CHARACTER || kind == CppTokenKinds.DECIMAL_INT_LITERAL || kind == CppTokenKinds.HEXADECIMAL_INT_LITERAL || kind == CppTokenKinds.OCTAL_INT_LITERAL || kind == CppTokenKinds.FLOAT_LITERAL || kind == CppTokenKinds.BINARY_INT_LITERAL || kind == CppTokenKinds.ZERO;
        if (ignoreLiterals && isLiteral) {
            image = CppTokenKinds.describe(kind);
        }

        if (ignoreIdentifiers && (kind == CppTokenKinds.ID)) {
            image = CppTokenKinds.describe(kind);
        }

        tokenEntries.recordToken(image, currentToken.getReportLocation());
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
                    discardingTokensUntil = findEndOfSequenceToDiscard(remainingTokens, ignoreIdentifierAndLiteralSeqences);
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
