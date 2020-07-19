/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.logging.Logger;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.UnresolvedSymFactory;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ast.AstSymFactory;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ast.SymbolResolutionPass;
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

    private final SemanticChecksLogger logger;
    private final LanguageVersion languageVersion; // NOPMD
    private final AstSymFactory astSymFactory;
    private final ReflectionSymFactory reflectSymFactory; // NOPMD
    private final UnresolvedSymFactory unresolvedSymFactory;
    private SymbolResolver symResolver;


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
        this.unresolvedSymFactory = new UnresolvedSymFactory();
    }

    public AstSymFactory getAstSymFactory() {
        return astSymFactory;
    }

    public JClassSymbol makeUnresolvedReference(String canonicalName) {
        return makeUnresolvedReference(canonicalName, 0);
    }

    public JClassSymbol makeUnresolvedReference(JTypeDeclSymbol outer, String innerSimpleName) {
        if (outer instanceof JClassSymbol) {
            return makeUnresolvedReference(((JClassSymbol) outer).getCanonicalName() + '.' + innerSimpleName);
        }
        return makeUnresolvedReference(innerSimpleName); // child of a type variable does not exist
    }

    public JClassSymbol makeUnresolvedReference(String canonicalName, int typeArity) {
        return unresolvedSymFactory.makeUnresolvedReference(canonicalName, typeArity);
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
            - AST disambiguation here
            - TODO type resolution initialization
         */

        SymbolResolver knownSyms = bench("Symbol resolution", () -> SymbolResolutionPass.traverse(this, acu));

        // Now symbols are on the relevant nodes
        this.symResolver = SymbolResolver.layer(knownSyms, this.symResolver);

        bench("Symbol table resolution", () -> SymbolTableResolver.traverse(this, acu));
        bench("AST disambiguation", () -> InternalApiBridge.disambig(this, acu));

    }


    public static SemanticChecksLogger defaultLogger() {
        return new SemanticChecksLogger() {
            private String locPrefix(JavaNode loc) {
                return "[" + loc.getBeginLine() + "," + loc.getBeginColumn() + "] ";
            }

            @Override
            public void warning(JavaNode location, String message, Object... args) {
                DEFAULT_LOG.fine(() -> locPrefix(location) + MessageFormat.format(message, args));
            }

            @Override
            public void error(JavaNode location, String message, Object... args) {
                DEFAULT_LOG.severe(() -> locPrefix(location) + MessageFormat.format(message, args));
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

    public static void bench(String label, Runnable runnable) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.LANGUAGE_SPECIFIC_PROCESSING, label)) {
            runnable.run();
        }
    }

    public static <T> T bench(String label, Supplier<T> runnable) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.LANGUAGE_SPECIFIC_PROCESSING, label)) {
            return runnable.get();
        }
    }
}
