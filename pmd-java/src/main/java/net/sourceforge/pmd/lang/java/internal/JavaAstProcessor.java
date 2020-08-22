/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.text.MessageFormat;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
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
import net.sourceforge.pmd.lang.java.symbols.internal.UnresolvedClassStore;
import net.sourceforge.pmd.lang.java.symbols.internal.ast.SymbolResolutionPass;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SymbolTableResolver;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.SimpleLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.VerboseLogger;

/**
 * Processes the output of the parser before rules get access to the AST.
 * This performs all semantic analyses in layered passes.
 *
 * <p>This is the root context object for file-specific context. Instances
 * do not need to be thread-safe. Global information about eg the classpath
 * is held in a {@link TypeSystem} instance.
 */
public final class JavaAstProcessor {

    private static final Logger DEFAULT_LOG = Logger.getLogger(JavaAstProcessor.class.getName());

    private static final Map<ClassLoader, TypeSystem> TYPE_SYSTEMS = new IdentityHashMap<>();
    private static final Level INFERENCE_LOG_LEVEL;


    static {
        Level level;
        try {
            level = Level.parse(System.getenv("PMD_DEBUG_LEVEL"));
        } catch (IllegalArgumentException | NullPointerException ignored) {
            level = Level.OFF;
        }
        INFERENCE_LOG_LEVEL = level;
    }


    private final TypeInferenceLogger typeInferenceLogger;
    private final SemanticChecksLogger logger;
    private final LanguageVersion languageVersion;
    private final TypeSystem typeSystem;

    private SymbolResolver symResolver;

    private final UnresolvedClassStore unresolvedTypes;


    private JavaAstProcessor(TypeSystem typeSystem,
                             SymbolResolver symResolver,
                             SemanticChecksLogger logger,
                             TypeInferenceLogger typeInfLogger,
                             LanguageVersion languageVersion) {

        this.symResolver = symResolver;
        this.logger = logger;
        this.typeInferenceLogger = typeInfLogger;
        this.languageVersion = languageVersion;

        this.typeSystem = typeSystem;
        unresolvedTypes = new UnresolvedClassStore(typeSystem);
    }

    static TypeInferenceLogger defaultTypeInfLogger() {
        if (INFERENCE_LOG_LEVEL == Level.FINEST) {
            return new VerboseLogger(System.err);
        } else if (INFERENCE_LOG_LEVEL == Level.FINE) {
            return new SimpleLogger(System.err);
        } else {
            return TypeInferenceLogger.noop();
        }
    }

    public JClassSymbol makeUnresolvedReference(String canonicalName, int typeArity) {
        return unresolvedTypes.makeUnresolvedReference(canonicalName, typeArity);
    }

    public JClassSymbol makeUnresolvedReference(JTypeDeclSymbol outer, String simpleName, int typeArity) {
        if (outer instanceof JClassSymbol) {
            return unresolvedTypes.makeUnresolvedReference((JClassSymbol) outer, simpleName, typeArity);
        }
        return makeUnresolvedReference("error." + simpleName, typeArity);
    }

    public SymbolResolver getSymResolver() {
        return symResolver;
    }

    public SemanticChecksLogger getLogger() {
        return logger;
    }

    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    public int getJdkVersion() {
        return ((JavaLanguageHandler) languageVersion.getLanguageVersionHandler()).getJdkVersion();
    }

    /**
     * Performs semantic analysis on the given source file.
     */
    public void process(ASTCompilationUnit acu) {

        SymbolResolver knownSyms = bench("Symbol resolution", () -> SymbolResolutionPass.traverse(this, acu));

        // Now symbols are on the relevant nodes
        this.symResolver = SymbolResolver.layer(knownSyms, this.symResolver);

        InternalApiBridge.initTypeResolver(acu, this, typeInferenceLogger);

        bench("2. Symbol table resolution", () -> SymbolTableResolver.traverse(this, acu));
        bench("3. AST disambiguation", () -> InternalApiBridge.disambig(this, acu));
    }

    public TypeSystem getTypeSystem() {
        return typeSystem;
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
                                          TypeSystem typeSystem,
                                          LanguageVersion languageVersion,
                                          SemanticChecksLogger logger) {

        return new JavaAstProcessor(
            typeSystem,
            symResolver,
            logger,
            defaultTypeInfLogger(),
            languageVersion
        );
    }

    public static JavaAstProcessor create(ClassLoader classLoader,
                                          LanguageVersion languageVersion,
                                          SemanticChecksLogger logger,
                                          TypeInferenceLogger typeInfLogger) {

        TypeSystem typeSystem = TYPE_SYSTEMS.computeIfAbsent(classLoader, TypeSystem::new);
        return new JavaAstProcessor(
            typeSystem,
            typeSystem.bootstrapResolver(),
            logger,
            typeInfLogger,
            languageVersion
        );
    }

    public static JavaAstProcessor create(TypeSystem typeSystem,
                                          LanguageVersion languageVersion,
                                          SemanticChecksLogger semanticLogger,
                                          TypeInferenceLogger typeInfLogger) {
        return new JavaAstProcessor(
            typeSystem,
            typeSystem.bootstrapResolver(),
            semanticLogger,
            typeInfLogger,
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
