/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTreeBuilderState;

/**
 * Adapter for the SwiftParser.
 */
public final class SwiftParserAdapter extends AntlrBaseParser<SwiftNode<?>, SwiftNodeImpl<?>, SwiftRootNode> {

    public SwiftParserAdapter(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected SwiftRootNode parse(final Lexer lexer) {
        SwiftTreeParser parser = new SwiftTreeParser(new CommonTokenStream(lexer));
        AntlrTreeBuilderState<?> listener = new AntlrTreeBuilderState<>(SwiftNodeFactory.INSTANCE);
        parser.addParseListener(listener);
        parser.topLevel();
        return (SwiftRootNode) listener.top();
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new SwiftTreeLexer(source);
    }
}
