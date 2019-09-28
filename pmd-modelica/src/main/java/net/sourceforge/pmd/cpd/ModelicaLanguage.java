/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.modelica.ModelicaLanguageModule;

public class ModelicaLanguage extends AbstractLanguage {
    public ModelicaLanguage() {
        super(ModelicaLanguageModule.NAME, ModelicaLanguageModule.TERSE_NAME, new ModelicaTokenizer(), ".mo");
    }
}
