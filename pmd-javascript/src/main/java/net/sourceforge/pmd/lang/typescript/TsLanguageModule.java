/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.typescript.cpd.TypeScriptTokenizer;

/**
 * @author pguyot@kallisys.net
 */
public class TsLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "typescript";

    public TsLanguageModule() {
        super(LanguageMetadata.withId(ID)
                  .name("TypeScript")
                  .extensions("ts"));
    }

    public static TsLanguageModule getInstance() {
        return (TsLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new TypeScriptTokenizer();
    }
}
