/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageProcessor;
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
    protected KotlinParser.KtKotlinFile doParse(@NotNull LanguageProcessor processor, @NotNull Params params, @NotNull ParserTask task) {
        if (processor instanceof KotlinLanguageProcessor) {
            ((KotlinLanguageProcessor) processor).prepareForSingleDocument(task.getTextDocument());
        }
        return super.doParse(processor, params, task);
    }
}
