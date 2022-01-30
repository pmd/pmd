/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.ast.SemanticException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.SimpleLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.VerboseLogger;

import kotlin.Pair;

public class JavaParsingHelper extends BaseParsingHelper<JavaParsingHelper, ASTCompilationUnit> {

    /**
     * Note: this is the default type system for everything parsed with
     * default options of JavaParsingHelper. This allows constants like
     * the null type to be compared.
     */
    public static final TypeSystem TEST_TYPE_SYSTEM = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());

    /** This just runs the parser and no processing stages. */
    public static final JavaParsingHelper JUST_PARSE = new JavaParsingHelper(Params.getDefaultNoProcess(), SemanticErrorReporter.noop(), TEST_TYPE_SYSTEM, TypeInferenceLogger.noop());

    /** This runs all processing stages when parsing. */
    public static final JavaParsingHelper WITH_PROCESSING = new JavaParsingHelper(Params.getDefaultProcess(), SemanticErrorReporter.noop(), TEST_TYPE_SYSTEM, TypeInferenceLogger.noop());

    private final SemanticErrorReporter semanticLogger;
    private final TypeSystem ts;
    private final TypeInferenceLogger typeInfLogger;

    private JavaParsingHelper(Params params, SemanticErrorReporter logger, TypeSystem ts, TypeInferenceLogger typeInfLogger) {
        super(JavaLanguageModule.NAME, ASTCompilationUnit.class, params);
        this.semanticLogger = logger;
        this.ts = ts;
        this.typeInfLogger = typeInfLogger;
    }

    @Override
    protected void postProcessing(@NotNull LanguageVersionHandler handler, @NotNull LanguageVersion lversion, @NotNull ASTCompilationUnit rootNode) {
        JavaAstProcessor.create(ts, lversion, semanticLogger, typeInfLogger)
                        .process(rootNode);
    }

    public TypeInferenceLogger getTypeInfLogger() {
        return typeInfLogger;
    }

    public JavaParsingHelper withLogger(SemanticErrorReporter logger) {
        return new JavaParsingHelper(this.getParams(), logger, this.ts, this.typeInfLogger);
    }

    public JavaParsingHelper withTypeSystem(TypeSystem ts) {
        return new JavaParsingHelper(this.getParams(), this.semanticLogger, ts, this.typeInfLogger);
    }

    public JavaParsingHelper logTypeInference(boolean verbose, PrintStream out) {
        return logTypeInference(verbose ? new VerboseLogger(out) : new SimpleLogger(out));
    }

    public JavaParsingHelper logTypeInference(TypeInferenceLogger logger) {
        return new JavaParsingHelper(this.getParams(), this.semanticLogger, this.ts, logger);
    }

    @Override
    protected @NonNull JavaParsingHelper clone(@NonNull Params params) {
        return new JavaParsingHelper(params, semanticLogger, ts, typeInfLogger);
    }

    public static class TestCheckLogger implements SemanticErrorReporter {

        public final Map<String, List<kotlin.Pair<Node, Object[]>>> warnings = new HashMap<>();
        public final Map<String, List<kotlin.Pair<Node, Object[]>>> errors = new HashMap<>();

        private final SemanticErrorReporter baseLogger;

        public TestCheckLogger() {
            this(false);
        }

        public TestCheckLogger(boolean doLogOnConsole) {
            Logger consoleLogger = Logger.getAnonymousLogger();
            if (!doLogOnConsole) {
                consoleLogger.setLevel(Level.OFF);
            }
            baseLogger = SemanticErrorReporter.reportToLogger(consoleLogger);
        }

        @Override
        public void warning(Node location, String message, Object... args) {
            warnings.computeIfAbsent(message, k -> new ArrayList<>())
                    .add(new Pair<>(location, args));

            baseLogger.warning(location, message, args);
        }

        @Override
        public SemanticException error(Node location, String message, Object... args) {
            errors.computeIfAbsent(message, k -> new ArrayList<>())
                  .add(new Pair<>(location, args));
            return baseLogger.error(location, message, args);
        }

        @Override
        public boolean hasError() {
            return baseLogger.hasError();
        }
    }
}
