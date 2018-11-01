/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameResolver;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;
import net.sourceforge.pmd.lang.java.typeresolution.TypeResolutionFacade;
import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;


/**
 * Java processing stages.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
public enum JavaProcessingStage implements AstProcessingStage<JavaProcessingStage> {

    /**
     * Qualified name resolution.
     */
    QNAME_RESOLUTION("Qualified name resolution") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisConfiguration configuration) {
            new QualifiedNameResolver().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Symbol table analysis.
     */
    SYMBOL_RESOLUTION("Symbol table") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisConfiguration configuration) {
            new SymbolFacade().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Type resolution, depends on QName resolution.
     */
    TYPE_RESOLUTION("Type resolution", QNAME_RESOLUTION) {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisConfiguration configuration) {
            new TypeResolutionFacade().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Data flow analysis.
     */
    DFA("Data flow analysis") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisConfiguration configuration) {
            new DataFlowFacade().initializeWith(new JavaDataFlowHandler(), (ASTCompilationUnit) rootNode);
        }


        @Override
        public boolean ruleDependsOnThisStage(Rule rule) {
            return rule.isDfa();
        }
    };


    private final String displayName;
    private final List<JavaProcessingStage> dependencies;


    JavaProcessingStage(String displayName, JavaProcessingStage... dependencies) {
        this.displayName = displayName;
        this.dependencies = Collections.unmodifiableList(Arrays.asList(dependencies));
    }


    @Override
    public List<JavaProcessingStage> getDependencies() {
        return dependencies;
    }


    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean ruleDependsOnThisStage(Rule rule) {
        // most stages are enabled by default in java
        return true;
    }
}
