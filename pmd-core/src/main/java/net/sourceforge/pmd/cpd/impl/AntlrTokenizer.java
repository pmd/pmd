/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * Generic implementation of a {@link Tokenizer} useful to any Antlr grammar.
 */
public abstract class AntlrTokenizer extends TokenizerBase<AntlrToken> {
    @Override
    protected final TokenManager<AntlrToken> makeLexerImpl(TextDocument doc) throws IOException {
        CharStream charStream = CharStreams.fromReader(doc.newReader(), doc.getFileId().getAbsolutePath());
        charStream = filterBomChar(charStream);
        return new AntlrTokenManager(getLexerForSource(charStream), doc);
    }

    protected final CharStream filterBomChar(CharStream inputStream) {
        // Check if there is a BOM character after the beginning to the file.
        final String charString = inputStream.toString();
        final int bomIndex = charString.indexOf('\uFEFF');

        if (bomIndex >= 0) {
            String bomFreeString = charString.substring(0, bomIndex) + charString.substring(bomIndex+1);
            return CharStreams.fromString(bomFreeString, inputStream.getSourceName());
        } else {
            return inputStream;
        }
    }

    protected abstract Lexer getLexerForSource(CharStream charStream);

}
