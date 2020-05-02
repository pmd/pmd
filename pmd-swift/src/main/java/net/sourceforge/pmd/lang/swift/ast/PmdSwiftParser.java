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
public final class PmdSwiftParser extends AntlrBaseParser<SwiftNode, SwiftFileNode> {

    public PmdSwiftParser(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected SwiftFileNode parse(final Lexer lexer) {
        SwiftParser parser = new SwiftParser(new CommonTokenStream(lexer));
        return new SwiftFileNode(parser.topLevel());
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new SwiftLexer(source);
    }
}
