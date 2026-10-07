/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy.cpd;

import java.io.IOException;

import org.apache.groovy.parser.antlr4.GroovyLangLexer;

import net.sourceforge.pmd.cpd.impl.CpdLexerBase;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.groovy.ast.impl.antlr4.GroovyToken;
import net.sourceforge.pmd.lang.groovy.ast.impl.antlr4.GroovyTokenManager;

import groovyjarjarantlr4.v4.runtime.CharStream;
import groovyjarjarantlr4.v4.runtime.CharStreams;

/**
 * The Groovy Tokenizer
 *
 * <p>Note: This class has been called GroovyTokenizer in PMD 6</p>.
 */
public class GroovyCpdLexer extends CpdLexerBase<GroovyToken> {

    @Override
    protected final TokenManager<GroovyToken> makeLexerImpl(TextDocument doc) throws IOException {
        CharStream charStream = CharStreams.fromReader(doc.newReader(), doc.getFileId().getAbsolutePath());
        // GroovyLangLexer is the lexer the Groovy compiler itself uses. The plain
        // GroovyLexer relies on it to roll back one char at the end of a GString
        // value (e.g. "$i"), without that it fails on valid code (#7100).
        return new GroovyTokenManager(new GroovyLangLexer(charStream), doc);
    }
}
