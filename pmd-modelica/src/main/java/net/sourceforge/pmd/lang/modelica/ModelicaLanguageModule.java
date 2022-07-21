/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

public class ModelicaLanguageModule extends SimpleLanguageModuleBase {
    public static final String NAME = "Modelica";
    public static final String TERSE_NAME = "modelica";

    public ModelicaLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("mo"),
              new ModelicaHandler());
    }

}
