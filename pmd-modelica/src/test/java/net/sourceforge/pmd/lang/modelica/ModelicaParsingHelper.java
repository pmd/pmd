/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;


import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

public class ModelicaParsingHelper extends BaseParsingHelper<ModelicaParsingHelper, ASTStoredDefinition> {

    /** This runs all processing stages when parsing. */
    public static final ModelicaParsingHelper DEFAULT = new ModelicaParsingHelper(Params.getDefault());

    private ModelicaParsingHelper(Params params) {
        super(ModelicaLanguageModule.getInstance(), ASTStoredDefinition.class, params);
    }

    @Override
    protected ModelicaParsingHelper clone(Params params) {
        return new ModelicaParsingHelper(params);
    }

}
