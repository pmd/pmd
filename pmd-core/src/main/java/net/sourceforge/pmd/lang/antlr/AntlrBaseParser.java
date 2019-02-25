/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.antlr;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public abstract class AntlrBaseParser implements Parser {

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
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Node parse(final String fileName, final Reader source) throws ParseException {
        AntlrBaseNode rootNode = null;
        try {
            rootNode = getRootNode(getParser(getLexer(source)));
        } catch (final IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return rootNode;
    }

    private AntlrBaseNode getRootNode(final org.antlr.v4.runtime.Parser parser)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method rootMethod = parser.getClass().getMethod(parser.getRuleNames()[0]);
        return (AntlrBaseNode) rootMethod.invoke(parser);
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>();
    }

    protected abstract Lexer getLexer(Reader source) throws IOException;

    protected abstract org.antlr.v4.runtime.Parser getParser(Lexer lexer);
}
