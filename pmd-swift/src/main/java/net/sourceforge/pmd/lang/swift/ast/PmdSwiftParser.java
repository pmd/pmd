/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser2;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrErrorListener;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwTopLevel;

/**
 * Adapter for the SwiftParser.
 */
public final class PmdSwiftParser extends AntlrBaseParser2<SwiftNode, SwTopLevel, SwiftParser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmdSwiftParser.class);

    @Override
    protected SwTopLevel parse(SwiftParser swiftParser, ParserTask task) {
        // Note: replace the default error listener, so that we can avoid
        // throwing a parse exception automatically for now
        AntlrErrorListener errorListener = new AntlrErrorListener(task);
        swiftParser.removeErrorListeners();
        swiftParser.addErrorListener(errorListener.parserErrorListener());

        SwTopLevel swTopLevel = swiftParser.topLevel().makeAstInfo(task);
        if (errorListener.hasErrors()) {
            LOGGER.warn("Errors while parsing have been ignored", errorListener.getException());
            // TODO: eventually we should throw a parse exception
            //throw errorListener.getException();
        }
        return swTopLevel;
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new SwiftLexer(source);
    }

    @Override
    protected SwiftParser getParser(Lexer lexer) {
        return new SwiftParser(new CommonTokenStream(lexer));
    }
}
