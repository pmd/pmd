/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.cs.ast.CSharpLexer;

/**
 * The C# tokenizer.
 */
public class CsTokenizer extends AntlrTokenizer {

    private boolean ignoreUsings = false;
    private boolean ignoreLiteralSequences = false;

    /**
     * Sets the possible options for the C# tokenizer.
     *
     * @param properties the properties
     * @see #IGNORE_USINGS
     * @see #OPTION_IGNORE_LITERAL_SEQUENCES
     */
    public void setProperties(Properties properties) {
        ignoreUsings = Boolean.parseBoolean(properties.getProperty(IGNORE_USINGS, Boolean.FALSE.toString()));
        ignoreLiteralSequences = Boolean.parseBoolean(properties.getProperty(OPTION_IGNORE_LITERAL_SEQUENCES,
            Boolean.FALSE.toString()));
    }

    public void setIgnoreUsings(boolean ignoreUsings) {
        this.ignoreUsings = ignoreUsings;
    }

    public void setIgnoreLiteralSequences(boolean ignoreLiteralSequences) {
        this.ignoreLiteralSequences = ignoreLiteralSequences;
    }

    @Override
    protected AntlrTokenManager getLexerForSource(final SourceCode sourceCode) {
        final CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        return new AntlrTokenManager(new CSharpLexer(charStream), sourceCode.getFileName());
    }

    @Override
    protected AntlrTokenFilter getTokenFilter(final AntlrTokenManager tokenManager) {
        return new CsTokenFilter(tokenManager, ignoreUsings, ignoreLiteralSequences);
    }

    /**
     * The {@link CsTokenFilter} extends the {@link AntlrTokenFilter} to discard
     * C#-specific tokens.
     * <p>
     * By default, it enables annotation-based CPD suppression.
     * If the --ignoreUsings flag is provided, using directives are filtered out.
     * </p>
     */
    private static class CsTokenFilter extends AntlrTokenFilter {
        private enum UsingState {
            KEYWORD, // just encountered the using keyword
            IDENTIFIER, // just encountered an identifier or var keyword
        }

        private final boolean ignoreUsings;
        private final boolean ignoreLiteralSequences;
        private boolean discardingUsings = false;
        private boolean discardingNL = false;
        private AntlrToken discardingLiteralsUntil = null;
        private boolean discardCurrent = false;

        CsTokenFilter(final AntlrTokenManager tokenManager, boolean ignoreUsings, boolean ignoreLiteralSequences) {
            super(tokenManager);
            this.ignoreUsings = ignoreUsings;
            this.ignoreLiteralSequences = ignoreLiteralSequences;
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
            return discardingUsings || discardingNL || isDiscardingLiterals() || discardCurrent;
        }
    }
}
