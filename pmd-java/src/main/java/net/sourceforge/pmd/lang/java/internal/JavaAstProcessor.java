/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.text.MessageFormat;
import java.util.logging.Logger;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.AstDisambiguationPass;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ast.SymbolResolutionPass;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ast.AstSymFactory;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ClasspathSymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectionSymFactory;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SymbolTableResolver;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;

/**
 * Processes the output of the parser before rules get access to the AST.
 * This performs all semantic analyses in layered passes.
 */
public final class JavaAstProcessor {

    private static final Logger DEFAULT_LOG = Logger.getLogger(JavaAstProcessor.class.getName());

    final SymbolResolver symResolver;
    final SemanticChecksLogger logger;
    final LanguageVersion languageVersion;

    final AstSymFactory astSymFactory;
    final ReflectionSymFactory reflectSymFactory;


    private JavaAstProcessor(ReflectionSymFactory reflectionSymFactory,
                             AstSymFactory astSymFactory,
                             SymbolResolver symResolver,
                             SemanticChecksLogger logger,
                             LanguageVersion languageVersion) {

        this.symResolver = symResolver;
        this.logger = logger;
        this.languageVersion = languageVersion;

        this.astSymFactory = astSymFactory;
        this.reflectSymFactory = reflectionSymFactory;
    }

    public AstSymFactory getAstSymFactory() {
        return astSymFactory;
    }

    public ReflectionSymFactory getReflectSymFactory() {
        return reflectSymFactory;
    }

    public SymbolResolver getSymResolver() {
        return symResolver;
    }

    public SemanticChecksLogger getLogger() {
        return logger;
    }


    /**
     * Performs semantic analysis on the given source file.
     */
    public void process(ASTCompilationUnit acu) {
        /*
            PASSES:

            - qname resolution, setting symbols on declarations
              - now AST symbols are only partially initialized, their type-related methods will fail
            - symbol table resolution
              - AST symbols are now functional
            - TODO AST disambiguation here
            - type resolution initialization
         */


        // Qualified name resolver now resolves also symbols for type declarations
        bench("Symbol resolution", () -> SymbolResolutionPass.traverse(this, acu));
        bench("Symbol table resolution", () -> SymbolTableResolver.traverse(this, acu));
        bench("AST disambiguation", () -> AstDisambiguationPass.traverse(this, acu));

    }


    public static SemanticChecksLogger defaultLogger() {
        return new SemanticChecksLogger() {
            @Override
            public void warning(JavaNode location, String message, Object... args) {
                DEFAULT_LOG.fine(() -> MessageFormat.format(message, args));
            }

            @Override
            public void error(JavaNode location, String message, Object... args) {
                DEFAULT_LOG.severe(() -> MessageFormat.format(message, args));
            }
        };
    }

    public static JavaAstProcessor create(SymbolResolver symResolver,
                                          ReflectionSymFactory reflectionSymFactory,
                                          LanguageVersion languageVersion,
                                          SemanticChecksLogger logger) {

        return new JavaAstProcessor(
            reflectionSymFactory,
            new AstSymFactory(),
            symResolver,
            logger,
            languageVersion
        );
    }

    public static JavaAstProcessor create(ClassLoader classLoader,
                                          LanguageVersion languageVersion,
                                          SemanticChecksLogger logger) {


        ReflectionSymFactory reflectionSymFactory = new ReflectionSymFactory();
        AstSymFactory astSymFactory = new AstSymFactory();

        ClassLoader cloaderImpl = PMDASMClassLoader.getInstance(classLoader);
        SymbolResolver symResolver = new ClasspathSymbolResolver(cloaderImpl, reflectionSymFactory);

        return new JavaAstProcessor(
            reflectionSymFactory,
            astSymFactory,
            symResolver,
            logger,
            languageVersion
        );
    }

    private static void bench(String label, Runnable runnable) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.LANGUAGE_SPECIFIC_PROCESSING, label)) {
            runnable.run();
        }
    }
}
