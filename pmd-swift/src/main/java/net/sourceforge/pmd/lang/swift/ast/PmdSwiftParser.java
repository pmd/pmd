/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwTopLevel;

/**
 * Adapter for the SwiftParser.
 */
public final class PmdSwiftParser extends AntlrBaseParser<SwiftNode, SwTopLevel> {

    @Override
    protected AstInfo<SwTopLevel> parse(final Lexer lexer, ParserTask task) {
        SwiftParser parser = new SwiftParser(new CommonTokenStream(lexer));
        return parser.topLevel().makeAstInfo(task);
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new SwiftLexer(source);
    }
}
