/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * Generic implementation of a {@link CpdLexer} useful to any Antlr grammar.
 */
public abstract class AntlrCpdLexer extends CpdLexerBase<AntlrToken> {
    @Override
    protected final TokenManager<AntlrToken> makeLexerImpl(TextDocument doc) throws IOException {
        CharStream charStream = CharStreams.fromReader(doc.newReader(), doc.getFileId().getAbsolutePath());
        return new AntlrTokenManager(getLexerForSource(charStream), doc);
    }

    protected abstract Lexer getLexerForSource(CharStream charStream);

}
