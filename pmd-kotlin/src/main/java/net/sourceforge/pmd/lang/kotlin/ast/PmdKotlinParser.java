/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFile;

/**
 * Adapter for the KotlinParser.
 */
public final class PmdKotlinParser extends AntlrBaseParser<KotlinNode, KtFile> {

    @Override
    protected KtFile parse(final Lexer lexer, ParserTask task) {
        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));
        return parser.file().makeAstInfo(task);
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new KotlinLexer(source);
    }
}
