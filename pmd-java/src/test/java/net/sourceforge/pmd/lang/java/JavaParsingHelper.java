/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.io.PrintStream;
import java.text.MessageFormat;
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
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.internal.JavaProcessingStage;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.SimpleLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.VerboseLogger;

import kotlin.Pair;

public class JavaParsingHelper extends BaseParsingHelper<JavaParsingHelper, ASTCompilationUnit> {

    /** This just runs the parser and no processing stages. */
    public static final JavaParsingHelper JUST_PARSE = new JavaParsingHelper(Params.getDefaultNoProcess(), NoopSemanticLogger.INSTANCE, TypeInferenceLogger.noop());
    /** This runs all processing stages when parsing. */
    public static final JavaParsingHelper WITH_PROCESSING = new JavaParsingHelper(Params.getDefaultProcess(), NoopSemanticLogger.INSTANCE, TypeInferenceLogger.noop());

    private final SemanticChecksLogger semanticLogger;
    private final TypeInferenceLogger typeInfLogger;

    private JavaParsingHelper(Params params, SemanticChecksLogger logger, TypeInferenceLogger typeInfLogger) {
        super(JavaLanguageModule.NAME, ASTCompilationUnit.class, params);
        this.semanticLogger = logger;
        this.typeInfLogger = typeInfLogger;
    }

    @Override
    protected void postProcessing(@NotNull LanguageVersionHandler handler, @NotNull LanguageVersion lversion, @NotNull ASTCompilationUnit rootNode) {
        JavaAstProcessor.create(JavaProcessingStage.class.getClassLoader(), lversion, semanticLogger, typeInfLogger)
                        .process(rootNode);
    }

    public JavaParsingHelper withLogger(SemanticChecksLogger logger) {
        return new JavaParsingHelper(this.getParams(), logger, typeInfLogger);
    }

    public JavaParsingHelper logTypeInference(boolean verbose, PrintStream out) {
        TypeInferenceLogger typeInfLogger = verbose ? new VerboseLogger(out) : new SimpleLogger(out);
        return new JavaParsingHelper(this.getParams(), this.semanticLogger, typeInfLogger);
    }

    @NonNull
    @Override
    protected JavaParsingHelper clone(Params params) {
        return new JavaParsingHelper(params, new TestCheckLogger(), typeInfLogger);
    }

    public static <T> List<T> convertList(List<Node> nodes, Class<T> target) {
        List<T> converted = new ArrayList<>();
        for (Node n : nodes) {
            converted.add(target.cast(n));
        }
        return converted;
    }

    static class NoopSemanticLogger implements SemanticChecksLogger {

        public static final SemanticChecksLogger INSTANCE = new NoopSemanticLogger();

        @Override
        public void warning(JavaNode location, String message, Object... args) {

        }

        @Override
        public void error(JavaNode location, String message, Object... args) {

        }
    }

    public static class TestCheckLogger implements SemanticChecksLogger {

        private static final Logger LOG = Logger.getLogger(TestCheckLogger.class.getName());
        public final Map<String, List<kotlin.Pair<JavaNode, Object[]>>> warnings = new HashMap<>();
        public final Map<String, List<kotlin.Pair<JavaNode, Object[]>>> errors = new HashMap<>();

        private final boolean doLogOnConsole;

        public TestCheckLogger() {
            this(false);
        }

        public TestCheckLogger(boolean doLogOnConsole) {
            this.doLogOnConsole = doLogOnConsole;
        }

        @Override
        public void warning(JavaNode location, String message, Object... args) {
            log(location, message, args, Level.WARNING);
            warnings.computeIfAbsent(message, k -> new ArrayList<>())
                    .add(new Pair<>(location, args));
        }

        @Override
        public void error(JavaNode location, String message, Object... args) {
            log(location, message, args, Level.SEVERE);
            errors.computeIfAbsent(message, k -> new ArrayList<>())
                  .add(new Pair<>(location, args));
        }


        public void log(JavaNode location, String message, Object[] args, Level level) {
            if (doLogOnConsole) {
                LOG.log(level, formatLoc(location) + new MessageFormat(message).format(args));
            }
        }

        @NonNull
        private String formatLoc(JavaNode location) {
            return "[" + location.getBeginLine() + "," + location.getBeginColumn() + "] ";
        }

    }
}
