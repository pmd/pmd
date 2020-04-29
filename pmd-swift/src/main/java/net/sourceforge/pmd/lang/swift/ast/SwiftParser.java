/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTreeBuilderListener;

/**
 * Adapter for the SwiftParser.
 */
public final class SwiftParser extends AntlrBaseParser<SwiftNode<?>, SwRootNode> {

    public SwiftParser(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected SwRootNode parse(final Lexer lexer) {
        SwiftTreeParser parser = new SwiftTreeParser(new CommonTokenStream(lexer));
        AntlrTreeBuilderListener<?, ?> listener = new AntlrTreeBuilderListener<>(SwiftNodeFactory.INSTANCE);
        parser.addParseListener(listener);
        parser.topLevel();
        return (SwRootNode) listener.top();
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new SwiftTreeLexer(source);
    }
}
