/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.antlr;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AntlrBaseNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Generic Antlr parser adapter for all Antlr parsers.
 */
public abstract class AntlrBaseParser<T extends org.antlr.v4.runtime.Parser> implements Parser {

    protected final ParserOptions parserOptions;

    public AntlrBaseParser(final ParserOptions parserOptions) {
        this.parserOptions = parserOptions;
    }

    @Override
    public ParserOptions getParserOptions() {
        return parserOptions;
    }

    @Override
    public TokenManager getTokenManager(final String fileName, final Reader source) {
        try {
            return new AntlrTokenManager(getLexer(source), fileName);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Node parse(final String fileName, final Reader source) throws ParseException {
        try {
            return getRootNode(getParser(getLexer(source)));
        } catch (final IOException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>();
    }

    protected abstract AntlrBaseNode getRootNode(T parser);

    protected abstract Lexer getLexer(Reader source) throws IOException;

    protected abstract T getParser(Lexer lexer);
}
