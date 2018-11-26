/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameResolver;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;
import net.sourceforge.pmd.lang.java.typeresolution.TypeResolutionFacade;


/**
 * Java processing stages.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
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
        public boolean dependsOnImpl(Rule rule) {
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


    /**
     * Returns true if the given Java rule depends on this stage.
     *
     * <p>{@link AbstractJavaRule#dependsOn(AstProcessingStage)}
     * delegates to this implementation after a validity check.
     * Dispatching on the rule language & forwarding to the
     * processing stage implementation allows specializing XPath
     * rules differently from rules written in Java, while keeping
     * dependency specification inside the processing stage declaration,
     * for readability. By design, Java XPath rules ignore this
     * method, it is only relevant to {@link AbstractJavaRule}.
     *
     * @param rule Rule to check
     *
     * @return True if the given rule depends on this stage
     *
     * @throws IllegalArgumentException if this rule is not a rule for Java.
     */
    @Experimental
    public final boolean ruleDependsOnThisStage(Rule rule) {
        if (!rule.getLanguage().equals(LanguageRegistry.findLanguageByTerseName("java"))) {
            throw new IllegalArgumentException();
        }
        return dependsOnImpl(rule); // this is a template method
    }


    protected boolean dependsOnImpl(Rule rule) {
        // most Java processing stages are run by default
        return true;
    }
}
