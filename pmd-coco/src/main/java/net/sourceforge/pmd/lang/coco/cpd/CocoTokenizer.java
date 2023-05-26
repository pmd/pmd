/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.coco.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrTokenizer;
import net.sourceforge.pmd.lang.coco.ast.CocoLexer;

/**
 * The Coco Tokenizer.
 */
public class CocoTokenizer extends AntlrTokenizer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new CocoLexer(charStream);
    }
}
