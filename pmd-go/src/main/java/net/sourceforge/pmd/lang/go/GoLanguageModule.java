/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.go;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.go.cpd.GoTokenizer;

public class GoLanguageModule extends CpdOnlyLanguageModuleBase {

    public GoLanguageModule() {
        super(LanguageMetadata.withId("go").name("Go").extensions("go"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new GoTokenizer();
    }
}
