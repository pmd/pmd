/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.perl;

import net.sourceforge.pmd.cpd.AnyTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

public class PerlLanguageModule extends CpdOnlyLanguageModuleBase {

    public PerlLanguageModule() {
        super(LanguageMetadata.withId("perl").name("Perl").extensions("pm", "pl", "t"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new AnyTokenizer("#");
    }
}
