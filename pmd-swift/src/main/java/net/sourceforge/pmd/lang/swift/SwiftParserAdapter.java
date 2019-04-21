/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.antlr.AntlrBaseParser;
import net.sourceforge.pmd.lang.ast.AntlrBaseNode;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftLexer;
import net.sourceforge.pmd.lang.swift.antlr4.SwiftParser;

/**
 * Adapter for the SwiftParser.
 */
public class SwiftParserAdapter extends AntlrBaseParser {

    public SwiftParserAdapter(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected AntlrBaseNode getRootNode(final Parser parser) {
        return ((SwiftParser) parser).topLevel();
    }

    @Override
    protected Lexer getLexer(final Reader source) throws IOException {
        return new SwiftLexer(CharStreams.fromReader(source));
    }

    @Override
    protected Parser getParser(final Lexer lexer) {
        return new SwiftParser(new CommonTokenStream(lexer));
    }

    @Override
    public boolean canParse() {
        return true;
    }
}
