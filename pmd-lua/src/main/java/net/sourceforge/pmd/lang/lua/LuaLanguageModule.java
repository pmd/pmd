/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.lua;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.lua.cpd.LuaCpdLexer;

/**
 * @author Clément Fournier
 */
public class LuaLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "lua";

    public LuaLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Lua").extensions("lua"));
    }

    public static LuaLanguageModule getInstance() {
        return (LuaLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        LanguagePropertyBundle bundle = super.newPropertyBundle();
        bundle.definePropertyDescriptor(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES);
        return bundle;
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new LuaCpdLexer(bundle);
    }
}
