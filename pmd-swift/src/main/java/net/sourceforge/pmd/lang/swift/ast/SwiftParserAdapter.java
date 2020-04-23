/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;

/**
 * Adapter for the SwiftParser.
 */
public class SwiftParserAdapter extends AntlrBaseParser<SwiftParser, SwiftFileNode> {

    public SwiftParserAdapter(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected SwiftFileNode parse(final SwiftParser parser) {
        return new SwiftFileNode(parser.topLevel());
    }

    @Override
    protected Lexer getLexer(final Reader source) throws IOException {
        return new SwiftLexer(CharStreams.fromReader(source));
    }

    @Override
    protected SwiftParser getParser(final Lexer lexer) {
        return new SwiftParser(new CommonTokenStream(lexer));
    }

}
