/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.typescript.cpd;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.typescript.ast.TypeScriptLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

public class TypeScriptCpdLexer extends AntlrCpdLexer {
    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new TypeScriptLexer(charStream);
    }
}
