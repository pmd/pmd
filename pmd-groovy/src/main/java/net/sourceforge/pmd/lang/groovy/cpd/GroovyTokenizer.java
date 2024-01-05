/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.cpd;

import java.io.IOException;

import org.apache.groovy.parser.antlr4.GroovyLexer;

import net.sourceforge.pmd.cpd.impl.TokenizerBase;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.groovy.ast.impl.antlr4.GroovyToken;
import net.sourceforge.pmd.lang.groovy.ast.impl.antlr4.GroovyTokenManager;

import groovyjarjarantlr4.v4.runtime.CharStream;
import groovyjarjarantlr4.v4.runtime.CharStreams;

/**
 * The Groovy Tokenizer
 */
public class GroovyTokenizer extends TokenizerBase<GroovyToken> {

    @Override
    protected final TokenManager<GroovyToken> makeLexerImpl(TextDocument doc) throws IOException {
        CharStream charStream = CharStreams.fromReader(doc.newReader(), doc.getFileId().getAbsolutePath());
        return new GroovyTokenManager(new GroovyLexer(charStream), doc);
    }
}
