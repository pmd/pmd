/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;

/**
 *
 */
public class KotlinParsingHelper extends BaseParsingHelper<KotlinParsingHelper, KotlinParser.KtFile> {

    public static final KotlinParsingHelper DEFAULT = new KotlinParsingHelper(Params.getDefaultNoProcess());


    public KotlinParsingHelper(@NotNull Params params) {
        super(KotlinLanguageModule.NAME, KotlinParser.KtFile.class, params);
    }

    @NotNull
    @Override
    protected KotlinParsingHelper clone(@NotNull Params params) {
        return new KotlinParsingHelper(params);
    }
}
