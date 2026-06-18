/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParserWithErrorHandling;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrErrorListener;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwTopLevel;

/**
 * Adapter for the SwiftParser.
 */
public final class PmdSwiftParser extends AntlrBaseParserWithErrorHandling<SwiftNode, SwTopLevel, SwiftParser, SwiftLexer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmdSwiftParser.class);

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

    // Note: Overriding parse(ParserTask), so that we can avoid
    // throwing a parse exception automatically for now
    @Override
    public SwTopLevel parse(ParserTask task) throws ParseException {
        CharStream cs = CharStreams.fromString(task.getSourceText(), task.getTextDocument().getFileId().getAbsolutePath());
        AntlrErrorListener errorListener = new AntlrErrorListener(task);
        SwiftLexer lexer = getLexer(cs);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener.lexerErrorListener());

        SwiftParser parser = getParser(lexer);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener.parserErrorListener());

        SwTopLevel parsed = parse(parser, task);
        if (errorListener.hasErrors()) {
            LOGGER.warn("Errors while parsing have been ignored", errorListener.getException());
            // TODO: eventually we should throw a exception
            //throw errorListener.getException();
        }
        return parsed;
    }
}
