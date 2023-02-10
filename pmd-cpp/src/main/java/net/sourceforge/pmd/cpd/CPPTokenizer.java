/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.cpd.internal.TokenizerBase;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
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
 */
public class CPPTokenizer extends TokenizerBase<JavaccToken> {

    private boolean skipBlocks;
    private Pattern skipBlocksStart;
    private Pattern skipBlocksEnd;
    private final boolean ignoreLiteralSequences;

    public CPPTokenizer(LanguagePropertyBundle cppProperties) {
        ignoreLiteralSequences = cppProperties.getProperty(Tokenizer.CPD_IGNORE_LITERAL_SEQUENCES);
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
                    discardingLiteralsUntil = findEndOfSequenceOfLiterals(remainingTokens);
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
