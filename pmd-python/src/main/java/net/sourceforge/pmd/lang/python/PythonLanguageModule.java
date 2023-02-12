/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.python;

import net.sourceforge.pmd.lang.python.cpd.PythonTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

/**
 * Defines the Language module for Python
 */
public class PythonLanguageModule extends CpdOnlyLanguageModuleBase {

    public PythonLanguageModule() {
        super(LanguageMetadata.withId("python").name("Python").extensions("py"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new PythonTokenizer();
    }
}
