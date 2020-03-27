/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ast.AstSymFactory;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ClasspathSymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectionSymFactory;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SymbolTableResolver;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Java processing stages.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
public enum JavaProcessingStage implements AstProcessingStage<JavaProcessingStage> {

    /**
     * This acts as a merged stage, non-optional. Ideally this would be encapsulated
     * in the {@link JavaParser}, like the {@link LanguageLevelChecker}.
     */
    JAVA_PROCESSING("Java processing") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            /*
                PASSES:

                - qname resolution, setting symbols on declarations
                  - now AST symbols are only partially initialized, their type-related methods will fail
                - symbol table resolution
                  - AST symbols are now functional
                - TODO AST disambiguation here
                - TODO type resolution initialization
             */

            ASTCompilationUnit acu = (ASTCompilationUnit) rootNode;

            AstSymFactory astSymFactory = new AstSymFactory();

            // Qualified name resolver now resolves also symbols for type declarations
            bench("Qualified name resolution",
                () -> astSymFactory.createSymbolsOn(acu));

            ClassLoader classLoader = PMDASMClassLoader.getInstance(configuration.getTypeResolutionClassLoader());
            SymbolResolver symResolver = new ClasspathSymbolResolver(classLoader, new ReflectionSymFactory());

            SemanticChecksLogger logger = new SemanticChecksLogger() {
                @Override
                public void warning(JavaNode location, String message, Object... args) {
                    Logger.getLogger(getClass().getName()).fine(() -> MessageFormat.format(message, args));
                }
            };

            // Resolve symbol tables
            bench("Symbol table resolution",
                () -> new SymbolTableResolver(symResolver, logger, acu).traverse());

        }
    },

    /**
     * Symbol table analysis.
     */
    SYMBOL_RESOLUTION("Symbol table") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            // kept for compatibility with existing tests
            new SymbolFacade().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Type resolution, depends on QName resolution.
     */
    TYPE_RESOLUTION("Type resolution", JAVA_PROCESSING) {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            // removed because of incompatibilities with current AST
            //            new TypeResolutionFacade().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Data flow analysis.
     */
    DFA("Data flow analysis") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            // removed because of incompatibilities with current AST
            // new DataFlowFacade().initializeWith(new JavaDataFlowHandler(), (ASTCompilationUnit) rootNode);
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
