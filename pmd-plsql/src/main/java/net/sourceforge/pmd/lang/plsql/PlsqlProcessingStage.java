/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
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
        public void processAST(RootNode rootNode, AstAnalysisConfiguration configuration) {
            new SymbolFacade().initializeWith((ASTInput) rootNode);
        }


        @Override
        public boolean dependsOnImpl(Rule rule) {
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
        public boolean dependsOnImpl(Rule rule) {
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


    /**
     * Returns true if the given Java rule depends on this stage.
     *
     * <p>{@link AbstractPLSQLRule#dependsOn(AstProcessingStage)}
     * delegates to this implementation after a validity check.
     * Dispatching on the rule language & forwarding to the
     * processing stage implementation allows specializing XPath
     * rules differently from rules written in Java, while keeping
     * dependency specification inside the processing stage declaration,
     * for readability. By design, PLSQL XPath rules ignore this
     * method, it is only relevant to {@link AbstractPLSQLRule}.
     *
     * @param rule Rule to check
     *
     * @return True if the given rule depends on this stage
     *
     * @throws IllegalArgumentException if this rule is not a PL-SQL rule.
     */
    @Experimental
    public final boolean ruleDependsOnThisStage(Rule rule) {
        if (!rule.getLanguage().equals(LanguageRegistry.findLanguageByTerseName("plsql"))) {
            throw new IllegalArgumentException();
        }
        return dependsOnImpl(rule); // this is a template method
    }


    protected abstract boolean dependsOnImpl(Rule rule);
}

