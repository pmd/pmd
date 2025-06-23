/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rust.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.rust.ast.RustLexer;

public class RustCpdLexer extends AntlrCpdLexer {
  
    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new RustLexer(charStream);
    }
}
