/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.plsql.symboltable.SymbolFacade;


/**
 * PL-SQL AST processing stages.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
public enum PlsqlProcessingStage implements AstProcessingStage<PlsqlProcessingStage> {

    /**
     * Symbol table analysis.
     */
    SYMBOL_RESOLUTION("Symbol table") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisConfiguration configuration) {
            new SymbolFacade().initializeWith((ASTInput) rootNode);
        }


        @Override
        public boolean ruleDependsOnThisStage(Rule rule) {
            return true;
        }
    },

    /**
     * Data flow analysis.
     */
    DFA("Data flow analysis") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisConfiguration configuration) {
            new DataFlowFacade().initializeWith(new PLSQLDataFlowHandler(), (ASTInput) rootNode);
        }


        @Override
        public boolean ruleDependsOnThisStage(Rule rule) {
            return rule.isDfa();
        }
    };


    private final String displayName;
    private final List<PlsqlProcessingStage> dependencies;


    PlsqlProcessingStage(String displayName, PlsqlProcessingStage... dependencies) {
        this.displayName = displayName;
        this.dependencies = Collections.unmodifiableList(Arrays.asList(dependencies));
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

