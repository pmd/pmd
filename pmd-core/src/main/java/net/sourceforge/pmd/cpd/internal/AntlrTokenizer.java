/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.internal;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.util.document.CpdCompat;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Generic implementation of a {@link Tokenizer} useful to any Antlr grammar.
 */
public abstract class AntlrTokenizer implements Tokenizer {

    protected abstract Lexer getLexerForSource(CharStream charStream);

    @Override
    public void tokenize(final SourceCode sourceCode, final Tokens tokenEntries) {
        try (TextDocument textDoc = TextDocument.create(CpdCompat.cpdCompat(sourceCode))) {

            CharStream charStream = CharStreams.fromString(textDoc.getText().toString(), textDoc.getDisplayName());

            final AntlrTokenManager tokenManager = new AntlrTokenManager(getLexerForSource(charStream), textDoc);
            final AntlrTokenFilter tokenFilter = getTokenFilter(tokenManager);

            AntlrToken currentToken = tokenFilter.getNextToken();
            while (currentToken != null) {
                processToken(tokenEntries, sourceCode.getFileName(), currentToken);
                currentToken = tokenFilter.getNextToken();
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }

    protected AntlrTokenFilter getTokenFilter(final AntlrTokenManager tokenManager) {
        return new AntlrTokenFilter(tokenManager);
    }

    private void processToken(final Tokens tokenEntries, String fileName, final AntlrToken token) {
        final TokenEntry tokenEntry = new TokenEntry(token.getImage(), fileName, token.getReportLocation());
        tokenEntries.add(tokenEntry);
    }
}
