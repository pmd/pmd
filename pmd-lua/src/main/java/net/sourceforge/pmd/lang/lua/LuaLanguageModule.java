/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.lua;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.lua.cpd.LuaTokenizer;

/**
 * @author Clément Fournier
 */
public class LuaLanguageModule extends CpdOnlyLanguageModuleBase {

    public LuaLanguageModule() {
        super(LanguageMetadata.withId("lua").name("Lua").extensions("lua"));
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        LanguagePropertyBundle bundle = super.newPropertyBundle();
        bundle.definePropertyDescriptor(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES);
        return bundle;
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new LuaTokenizer(bundle);
    }
}
