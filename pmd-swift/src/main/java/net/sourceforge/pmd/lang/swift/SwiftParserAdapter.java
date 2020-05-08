/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.swift.ast.SwiftLexer;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser;

/**
 * Adapter for the SwiftParser.
 */
public class SwiftParserAdapter extends AntlrBaseParser<SwiftParser> {

    public SwiftParserAdapter(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected RootNode getRootNode(final SwiftParser parser) {
        return parser.topLevel();
    }

    @Override
    protected Lexer getLexer(final Reader source) throws IOException {
        return new SwiftLexer(CharStreams.fromReader(source));
    }

    @Override
    protected SwiftParser getParser(final Lexer lexer) {
        return new SwiftParser(new CommonTokenStream(lexer));
    }

}
