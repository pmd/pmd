/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwTopLevel;

/**
 * Adapter for the SwiftParser.
 */
public final class PmdSwiftParser extends AntlrBaseParser<SwiftNode, SwTopLevel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmdSwiftParser.class);

    @Override
    protected SwTopLevel parse(final Lexer lexer, ParserTask task) {
        SwiftParser parser = new SwiftParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                LOGGER.warn("Syntax error at {}:{}:{}: {}", task.getFileId().getOriginalPath(),
                        line, charPositionInLine, msg);
                // TODO: eventually we should throw a parse exception
                // throw new ParseException(msg).withLocation(FileLocation.caret(task.getFileId(), line, charPositionInLine));
            }
        });
        return parser.topLevel().makeAstInfo(task);
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new SwiftLexer(source);
    }
}
