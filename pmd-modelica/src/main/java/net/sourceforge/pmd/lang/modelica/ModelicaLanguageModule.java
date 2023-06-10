/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.modelica.cpd.ModelicaTokenizer;

public class ModelicaLanguageModule extends SimpleLanguageModuleBase {
    public static final String NAME = "Modelica";
    public static final String TERSE_NAME = "modelica";
    public static final ModelicaLanguageModule INSTANCE = new ModelicaLanguageModule();

    public ModelicaLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions("mo")
                              .addVersion("3.4")
                              .addDefaultVersion("3.5"),
              new ModelicaHandler());
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new ModelicaTokenizer();
    }

    public static ModelicaLanguageModule getInstance() {
        return INSTANCE;
    }
}
