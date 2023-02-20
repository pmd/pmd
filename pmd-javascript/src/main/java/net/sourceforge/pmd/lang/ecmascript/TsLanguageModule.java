/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.ecmascript.cpd.EcmascriptTokenizer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * CPD only language to recognize TypeScript files.
 */
public class TsLanguageModule extends CpdOnlyLanguageModuleBase {

    public TsLanguageModule() {
        super(LanguageMetadata.withId("ts").name("TypeScript").extensions("ts"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new EcmascriptTokenizer();
    }
}
