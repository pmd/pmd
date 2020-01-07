/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstAnalysisContext;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.java.multifile.MultifileVisitorFacade;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameResolver;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ast.AstSymFactory;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ClasspathSymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectionSymFactory;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SymbolTableResolver;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;
import net.sourceforge.pmd.lang.java.typeresolution.TypeResolutionFacade;
import net.sourceforge.pmd.lang.java.types.internal.ast.LazyTypeResolver;


/**
 * Java processing stages.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
public enum JavaProcessingStage implements AstProcessingStage<JavaProcessingStage> {

    JAVA_PROCESSING("Java processing") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            /*
                - qname resolution, setting symbols on declarations
                  - AST symbols are only partially initialized, their type-related methods will fail
                - symbol table resolution
                  - AST symbols are now functional
                - TODO AST disambiguation here
                - type resolution initialization

             */
            ASTCompilationUnit acu = (ASTCompilationUnit) rootNode;

            AstSymFactory astSymFactory = new AstSymFactory();


            // Qualified name resolver now resolves also symbols for type declarations
            bench("Qualified name resolution",
                  () -> new QualifiedNameResolver(astSymFactory, acu).traverse());

            ClassLoader classLoader = PMDASMClassLoader.getInstance(configuration.getTypeResolutionClassLoader());


            SymbolResolver symResolver = new ClasspathSymbolResolver(classLoader, new ReflectionSymFactory());

            int jdkVersion = ((JavaLanguageHandler) configuration.getLanguageVersion().getLanguageVersionHandler()).getJdkVersion();

            // Resolve symbol tables
            bench("Symbol table resolution",
                  () -> new SymbolTableResolver(symResolver, jdkVersion, acu).traverse());

            // At this point at least explicit types should be handled,
            // so AstTypeFactory::fromAst should properly work

            InternalApiBridge.setTypeResolver(acu, new LazyTypeResolver(astSymFactory));
        }
    },

    /**
     * Symbol table analysis.
     */
    SYMBOL_RESOLUTION("Symbol table") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            new SymbolFacade().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Type resolution, depends on QName resolution.
     */
    TYPE_RESOLUTION("Type resolution", JAVA_PROCESSING) {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            new TypeResolutionFacade().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Data flow analysis.
     */
    DFA("Data flow analysis") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            new DataFlowFacade().initializeWith(new JavaDataFlowHandler(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Multi file analysis.
     */
    MULTIFILE("Multifile analysis") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            new MultifileVisitorFacade().initializeWith((ASTCompilationUnit) rootNode);
        }
    };

    private final String displayName;
    private final List<JavaProcessingStage> dependencies;

    JavaProcessingStage(String displayName, JavaProcessingStage... dependencies) {
        this.displayName = displayName;
        this.dependencies = Collections.unmodifiableList(Arrays.asList(dependencies));
    }

    protected void bench(String label, Runnable runnable) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.LANGUAGE_SPECIFIC_PROCESSING, label)) {
            runnable.run();
        }
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
    public final Language getLanguage() {
        return LanguageRegistry.findLanguageByTerseName("java");
    }


}
