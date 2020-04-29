/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;

/**
 * Adapter for the SwiftParser.
 */
public final class SwiftParserAdapter extends AntlrBaseParser<SwiftNode, SwiftNodeImpl<?>, SwiftRootNode> {

    public SwiftParserAdapter(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected SwiftRootNode parse(final Lexer lexer) {
        SwiftParser parser = new SwiftParser(new CommonTokenStream(lexer));
        SwiftTreeBuilder listener = new SwiftTreeBuilder();
        parser.addParseListener(listener);
        return (SwiftRootNode) listener.state.top();
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new SwiftLexer(source);
    }
}
