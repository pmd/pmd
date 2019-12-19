/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;

public class ModelicaParsingHelper extends BaseParsingHelper<ModelicaParsingHelper, ASTStoredDefinition> {

    /** This runs all processing stages when parsing. */
    public static final ModelicaParsingHelper DEFAULT = new ModelicaParsingHelper(Params.getDefaultProcess());

    private ModelicaParsingHelper(Params params) {
        super(ModelicaLanguageModule.NAME, ASTStoredDefinition.class, params);
    }

    @NotNull
    @Override
    protected ModelicaParsingHelper clone(@NotNull Params params) {
        return new ModelicaParsingHelper(params);
    }

}
