/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.internal;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstAnalysisContext;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.modelica.ModelicaLanguageModule;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaSymbolFacade;

/**
 * @author Clément Fournier
 */
public enum ModelicaProcessingStage implements AstProcessingStage<ModelicaProcessingStage> {
    SYMBOL_RESOLUTION("Symbol resolution") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            new ModelicaSymbolFacade().initializeWith((ASTStoredDefinition) rootNode);
        }
    };

    private final String displayName;

    ModelicaProcessingStage(String displayName) {
        this.displayName = displayName;
    }


    @Override
    public Language getLanguage() {
        return LanguageRegistry.getLanguage(ModelicaLanguageModule.NAME);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
