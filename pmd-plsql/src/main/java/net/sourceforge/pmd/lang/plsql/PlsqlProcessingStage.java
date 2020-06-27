/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstAnalysisContext;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.plsql.symboltable.SymbolFacade;


/**
 * PL-SQL AST processing stages.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
public enum PlsqlProcessingStage implements AstProcessingStage<PlsqlProcessingStage> {

    /**
     * Symbol table analysis.
     */
    SYMBOL_RESOLUTION("Symbol table") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            new SymbolFacade().initializeWith((ASTInput) rootNode);
        }
    },

    /**
     * Data flow analysis.
     */
    DFA("Data flow analysis") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            new DataFlowFacade().initializeWith(new PLSQLDataFlowHandler(), (ASTInput) rootNode);
        }
    };


    private final String displayName;
    private final List<PlsqlProcessingStage> dependencies;


    PlsqlProcessingStage(String displayName, PlsqlProcessingStage... dependencies) {
        this.displayName = displayName;
        this.dependencies = Collections.unmodifiableList(Arrays.asList(dependencies));
    }


    @Override
    public Language getLanguage() {
        return LanguageRegistry.STATIC.findLanguageByTerseName("plsql");
    }


    @Override
    public List<PlsqlProcessingStage> getDependencies() {
        return dependencies;
    }


    @Override
    public String getDisplayName() {
        return displayName;
    }


}

