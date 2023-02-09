/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

public class ModelicaLanguageModule extends SimpleLanguageModuleBase {
    public static final String NAME = "Modelica";
    public static final String TERSE_NAME = "modelica";
    @InternalApi
    public static final List<String> EXTENSIONS = listOf("mo");

    public ModelicaLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions(EXTENSIONS.get(0))
                              .addVersion("3.4")
                              .addDefaultVersion("3.5"),
              new ModelicaHandler());
    }

}
