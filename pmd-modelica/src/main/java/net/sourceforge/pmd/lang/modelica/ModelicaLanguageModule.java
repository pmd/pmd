/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.modelica.rule.ModelicaRuleChainVisitor;

public class ModelicaLanguageModule extends BaseLanguageModule {
    public static final String NAME = "Modelica";
    public static final String TERSE_NAME = "modelica";

    public ModelicaLanguageModule() {
        super(NAME, null, TERSE_NAME, ModelicaRuleChainVisitor.class, "mo");
        addVersion("", new ModelicaHandler(), true);
    }
}
