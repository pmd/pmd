/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

/**
 *  Parsing helper for Kotlin tests.
 */
public class KotlinParsingHelper extends BaseParsingHelper<KotlinParsingHelper, KotlinParser.KtKotlinFile> {

    public static final KotlinParsingHelper DEFAULT = new KotlinParsingHelper(Params.getDefault());

    public KotlinParsingHelper(@NotNull Params params) {
        super(KotlinLanguageModule.getInstance(), KotlinParser.KtKotlinFile.class, params);
    }

    @NotNull
    @Override
    protected KotlinParsingHelper clone(@NotNull Params params) {
        return new KotlinParsingHelper(params);
    }

    @NotNull
    @Override
    protected RootNode parseImpl(@NotNull Params params, @NotNull LanguageProcessor processor, @NotNull ParserTask task) {
        // Override for tests: use a custom parser setup (error listeners, etc)
        KotlinLexer lexer = new KotlinLexer(CharStreams.fromString(task.getSourceText()));
        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));

        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                throw new ParseException(msg)
                    .withLocation(FileLocation.caret(task.getFileId(), line, charPositionInLine));
            }
        });

        return parser.kotlinFile().makeAstInfo(task);
    }
}
