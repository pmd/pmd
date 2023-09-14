/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.python;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.python.cpd.PythonTokenizer;

/**
 * Defines the Language module for Python
 */
public class PythonLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "python";

    public PythonLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Python").extensions("py"));
    }

    public static PythonLanguageModule getInstance() {
        return (PythonLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new PythonTokenizer();
    }
}
