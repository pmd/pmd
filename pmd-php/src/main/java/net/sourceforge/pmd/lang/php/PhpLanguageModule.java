/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.php;

import net.sourceforge.pmd.cpd.AnyTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Language implementation for PHP
 */
public class PhpLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "php";

    public PhpLanguageModule() {
        super(LanguageMetadata.withId(ID).name("PHP").extensions("php", "class"));
    }

    public static PhpLanguageModule getInstance() {
        return (PhpLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new AnyTokenizer("#|//");
    }
}
