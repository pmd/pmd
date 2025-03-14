/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cs.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.cpd.impl.AntlrTokenFilter;
import net.sourceforge.pmd.cpd.impl.BaseTokenFilter;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.cs.ast.CSharpLexer;

/**
 * The C# tokenizer.
 *
 * <p>Note: This class has been called CsTokenizer in PMD 6</p>.
 */
public class CsCpdLexer extends AntlrCpdLexer {

    private final boolean ignoreUsings;
    private final boolean ignoreLiteralSequences;
    private final boolean ignoreAttributes;

    public CsCpdLexer(LanguagePropertyBundle properties) {
        ignoreUsings = properties.getProperty(CpdLanguageProperties.CPD_IGNORE_IMPORTS);
        ignoreLiteralSequences = properties.getProperty(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES);
        ignoreAttributes = properties.getProperty(CpdLanguageProperties.CPD_IGNORE_METADATA);
    }

    @Override
    protected Lexer getLexerForSource(final CharStream charStream) {
        return new CSharpLexer(charStream);
    }

    @Override
    protected TokenManager<AntlrToken> filterTokenStream(TokenManager<AntlrToken> tokenManager) {
        return new CsTokenFilter(tokenManager, ignoreUsings, ignoreLiteralSequences, ignoreAttributes);
    }

    /**
     * The {@link CsTokenFilter} extends the {@link AntlrTokenFilter} to discard
     * C#-specific tokens.
     * <p>
     * By default, it enables annotation-based CPD suppression.
     * If the --ignoreUsings flag is provided, using directives are filtered out.
     * </p>
     */
    private static class CsTokenFilter extends BaseTokenFilter<AntlrToken> {
        private enum UsingState {
            KEYWORD, // just encountered the using keyword
            IDENTIFIER, // just encountered an identifier or var keyword
        }

        private final boolean ignoreUsings;
        private final boolean ignoreLiteralSequences;
        private final boolean ignoreAttributes;
        private boolean discardingUsings = false;
        private boolean discardingNL = false;
        private boolean isDiscardingAttribute = false;
        private AntlrToken discardingLiteralsUntil = null;
        private boolean discardCurrent = false;

        CsTokenFilter(final TokenManager<AntlrToken> tokenManager, boolean ignoreUsings, boolean ignoreLiteralSequences, boolean ignoreAttributes) {
            super(tokenManager);
            this.ignoreUsings = ignoreUsings;
            this.ignoreLiteralSequences = ignoreLiteralSequences;
            this.ignoreAttributes = ignoreAttributes;
        }

        @Override
        protected void analyzeToken(final AntlrToken currentToken) {
            skipNewLines(currentToken);
        }

        @Override
        protected void analyzeTokens(final AntlrToken currentToken, final Iterable<AntlrToken> remainingTokens) {
            discardCurrent = false;
            skipUsingDirectives(currentToken, remainingTokens);
            skipLiteralSequences(currentToken, remainingTokens);
            skipAttributes(currentToken);
        }

        private void skipUsingDirectives(final AntlrToken currentToken, final Iterable<AntlrToken> remainingTokens) {
            if (ignoreUsings) {
                final int type = currentToken.getKind();
                if (type == CSharpLexer.USING && isUsingDirective(remainingTokens)) {
                    discardingUsings = true;
                } else if (type == CSharpLexer.SEMICOLON && discardingUsings) {
                    discardingUsings = false;
                    discardCurrent = true;
                }
            }
        }

        private boolean isUsingDirective(final Iterable<AntlrToken> remainingTokens) {
            UsingState usingState = UsingState.KEYWORD;
            for (final AntlrToken token : remainingTokens) {
                final int type = token.getKind();
                if (usingState == UsingState.KEYWORD) {
                    // The previous token was a using keyword.
                    switch (type) {
                    case CSharpLexer.STATIC:
                        // Definitely a using directive.
                        // Example: using static System.Math;
                        return true;
                    case CSharpLexer.VAR:
                        // Definitely a using statement.
                        // Example: using var font1 = new Font("Arial", 10.0f);
                        return false;
                    case CSharpLexer.OPEN_PARENS:
                        // Definitely a using statement.
                        // Example: using (var font1 = new Font("Arial", 10.0f);
                        return false;
                    case CSharpLexer.IDENTIFIER:
                        // This is either a type for a using statement or an alias for a using directive.
                        // Example (directive): using Project = PC.MyCompany.Project;
                        // Example (statement): using Font font1 = new Font("Arial", 10.0f);
                        usingState = UsingState.IDENTIFIER;
                        break;
                    default:
                        // Some unknown construct?
                        return false;
                    }
                } else if (usingState == UsingState.IDENTIFIER) {
                    // The previous token was an identifier.
                    switch (type) {
                    case CSharpLexer.ASSIGNMENT:
                        // Definitely a using directive.
                        // Example: using Project = PC.MyCompany.Project;
                        return true;
                    case CSharpLexer.IDENTIFIER:
                        // Definitely a using statement.
                        // Example: using Font font1 = new Font("Arial", 10.0f);
                        return false;
                    case CSharpLexer.DOT:
                        // This should be considered part of the same type; revert to previous state.
                        // Example (directive): using System.Text;
                        // Example (statement): using System.Drawing.Font font1 = new Font("Arial", 10.0f);
                        usingState = UsingState.KEYWORD;
                        break;
                    case CSharpLexer.SEMICOLON:
                        // End of using directive.
                        return true;
                    default:
                        // Some unknown construct?
                        return false;
                    }
                }
            }
            return false;
        }

        private void skipNewLines(final AntlrToken currentToken) {
            discardingNL = currentToken.getKind() == CSharpLexer.NL;
        }

        private void skipAttributes(final AntlrToken currentToken) {
            if (ignoreAttributes) {
                switch (currentToken.getKind()) {
                case CSharpLexer.OPEN_BRACKET:
                    // Start of an attribute.
                    isDiscardingAttribute = true;
                    break;
                case CSharpLexer.CLOSE_BRACKET:
                    // End of an attribute.
                    isDiscardingAttribute = false;
                    discardCurrent = true;
                    break;
                default:
                    // Skip any other token.
                    break;
                }
            }
        }

        private void skipLiteralSequences(final AntlrToken currentToken, final Iterable<AntlrToken> remainingTokens) {
            if (ignoreLiteralSequences) {
                final int type = currentToken.getKind();
                if (isDiscardingLiterals()) {
                    if (currentToken == discardingLiteralsUntil) { // NOPMD - intentional check for reference equality
                        discardingLiteralsUntil = null;
                        discardCurrent = true;
                    }
                } else if (type == CSharpLexer.OPEN_BRACE) {
                    final AntlrToken finalToken = findEndOfSequenceOfLiterals(remainingTokens);
                    discardingLiteralsUntil = finalToken;
                }
            }
        }

        private AntlrToken findEndOfSequenceOfLiterals(final Iterable<AntlrToken> remainingTokens) {
            boolean seenLiteral = false;
            int braceCount = 0;
            for (final AntlrToken token : remainingTokens) {
                switch (token.getKind()) {
                case CSharpLexer.BIN_INTEGER_LITERAL:
                case CSharpLexer.CHARACTER_LITERAL:
                case CSharpLexer.HEX_INTEGER_LITERAL:
                case CSharpLexer.INTEGER_LITERAL:
                case CSharpLexer.REAL_LITERAL:
                    seenLiteral = true;
                    break; // can be skipped; continue to the next token
                case CSharpLexer.COMMA:
                    break; // can be skipped; continue to the next token
                case CSharpLexer.OPEN_BRACE:
                    braceCount++;
                    break; // curly braces are allowed, as long as they're balanced
                case CSharpLexer.CLOSE_BRACE:
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

        public boolean isDiscardingLiterals() {
            return discardingLiteralsUntil != null;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingUsings || discardingNL || isDiscardingAttribute || isDiscardingLiterals() || discardCurrent;
        }
    }
}
