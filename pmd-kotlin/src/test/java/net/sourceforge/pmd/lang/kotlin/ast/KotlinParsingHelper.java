/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

/**
 *
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
}
