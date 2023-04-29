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

    public TsLanguageModule() {
        super(LanguageMetadata.withId("typescript")
                  .name("TypeScript")
                  .extensions("ts"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new TypeScriptTokenizer();
    }

    public static TsLanguageModule getInstance() {
        return (TsLanguageModule) LanguageRegistry.CPD.getLanguageById("typescript");
    }
}
