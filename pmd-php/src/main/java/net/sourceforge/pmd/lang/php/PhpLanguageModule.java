/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.php;

import net.sourceforge.pmd.cpd.AnyTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

/**
 * Language implementation for PHP
 */
public class PhpLanguageModule extends CpdOnlyLanguageModuleBase {

    public PhpLanguageModule() {
        super(LanguageMetadata.withId("php").name("PHP").extensions("php", "class"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new AnyTokenizer("#");
    }
}
