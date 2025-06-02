/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.swift.ast.SwiftLexer;

/**
 * SwiftTokenizer
 *
 * <p>Note: This class has been called SwiftTokenizer in PMD 6</p>.
 */
public class SwiftCpdLexer extends AntlrCpdLexer {

    @Override
    protected Lexer getLexerForSource(final CharStream charStream) {
        return new SwiftLexer(charStream);
    }
}
