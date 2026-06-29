/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParserWithErrorHandling;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwTopLevel;

/**
 * Adapter for the SwiftParser.
 */
public final class PmdSwiftParser extends AntlrBaseParserWithErrorHandling<SwiftNode, SwTopLevel, SwiftParser, SwiftLexer> {
    @Override
    protected SwTopLevel parse(SwiftParser swiftParser, ParserTask task) {
        return swiftParser.topLevel().makeAstInfo(task);
    }

    @Override
    protected SwiftLexer getLexer(final CharStream source) {
        return new SwiftLexer(source);
    }

    @Override
    protected SwiftParser getParser(SwiftLexer lexer) {
        return new SwiftParser(new CommonTokenStream(lexer));
    }
}
