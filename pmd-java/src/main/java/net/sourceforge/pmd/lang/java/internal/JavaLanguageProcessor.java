/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties.InferenceLoggingVerbosity;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.BaseContextNodeTestFun;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.GetCommentOnFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.GetModifiersFun;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.MatchesSignatureFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.MetricFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.NodeIsFunction;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.SimpleLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.VerboseLogger;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.reporting.ViolationDecorator;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

/**
 * @author Clément Fournier
 */
public class JavaLanguageProcessor extends BatchLanguageProcessor<JavaLanguageProperties>
    implements LanguageVersionHandler {

    private static final Logger LOG = LoggerFactory.getLogger("net.sourceforge.pmd.lang.java");

    private final LanguageMetricsProvider myMetricsProvider = new JavaMetricsProvider();
    private final JavaParser parser;
    private final JavaParser parserWithoutProcessing;
    private final boolean firstClassLombok;
    private TypeSystem typeSystem;

    public JavaLanguageProcessor(JavaLanguageProperties properties, TypeSystem typeSystem) {
        super(properties);
        this.typeSystem = typeSystem;

        String suppressMarker = properties.getSuppressMarker();
        this.parser = new JavaParser(suppressMarker, this, true);
        this.parserWithoutProcessing = new JavaParser(suppressMarker, this, false);
        this.firstClassLombok = properties.getProperty(JavaLanguageProperties.FIRST_CLASS_LOMBOK);
    }

    public JavaLanguageProcessor(JavaLanguageProperties properties) {
        this(properties, TypeSystem.usingClassLoaderClasspath(properties.getAnalysisClassLoader()));
        LOG.debug("Using analysis classloader: {}", properties.getAnalysisClassLoader());
        checkClasspathVersionMatchesAnalyzedVersion(properties.getAnalysisClassLoader(), properties.getLanguageVersion());
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return this;
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    public JavaParser getParserWithoutProcessing() {
        return parserWithoutProcessing;
    }

    public TypeSystem getTypeSystem() {
        return typeSystem;
    }

    public boolean hasFirstClassLombokSupport() {
        return firstClassLombok;
    }

    TypeInferenceLogger newTypeInfLogger() {
        InferenceLoggingVerbosity verbosity = getProperties().getProperty(JavaLanguageProperties.INTERNAL_INFERENCE_LOGGING_VERBOSITY);
        if (verbosity == InferenceLoggingVerbosity.VERBOSE) {
            return new VerboseLogger(System.err);
        } else if (verbosity == InferenceLoggingVerbosity.SIMPLE) {
            return new SimpleLogger(System.err);
        } else {
            return TypeInferenceLogger.noop();
        }
    }

    @Override
    public DesignerBindings getDesignerBindings() {
        return JavaDesignerBindings.INSTANCE;
    }

    @Override
    public XPathHandler getXPathHandler() {
        return XPATH_HANDLER;
    }

    @Override
    public List<ViolationSuppressor> getExtraViolationSuppressors() {
        return AnnotationSuppressionUtil.ALL_JAVA_SUPPRESSORS;
    }

    @Override
    public ViolationDecorator getViolationDecorator() {
        return JavaViolationDecorator.INSTANCE;
    }

    @Override
    public LanguageMetricsProvider getLanguageMetricsProvider() {
        return myMetricsProvider;
    }

    private static final XPathHandler XPATH_HANDLER =
        XPathHandler.getHandlerForFunctionDefs(
            BaseContextNodeTestFun.TYPE_IS_EXACTLY,
            BaseContextNodeTestFun.TYPE_IS,
            BaseContextNodeTestFun.HAS_ANNOTATION,
            MatchesSignatureFunction.INSTANCE,
            NodeIsFunction.INSTANCE,
            GetModifiersFun.GET_EFFECTIVE,
            GetModifiersFun.GET_EXPLICIT,
            MetricFunction.INSTANCE,
            GetCommentOnFunction.INSTANCE
        );

    public void setTypeSystem(TypeSystem ts) {
        this.typeSystem = Objects.requireNonNull(ts);
    }

    @Override
    public void close() throws Exception {
        this.typeSystem.logStats();
        super.close();
    }

    static void checkClasspathVersionMatchesAnalyzedVersion(ClassLoader loader, LanguageVersion currentJavaVersion) {
        OptionalInt jdkVer = findClassVersion(loader, "java/lang/Object.class");
        if (!jdkVer.isPresent()) {
            // there is a problem, we couldn't load java.lang.Object from the classpath...
            return;
        }

        // https://javaalmanac.io/bytecode/versions/
        // Our java versions start at 1.3, which is version 47.0.
        // Minor version is only non-zero in 1.1 so we don't care about it
        // Then it increments by 1 for each version.
        int major = jdkVer.getAsInt();
        assert major >= 45 : "major version is less than 45 (Java 1.0)";
        major = Math.max(major - 44, 3); // our versions start at 3

        int analysisVersion = JavaLanguageProperties.getInternalJdkVersion(currentJavaVersion);


        if (analysisVersion != major) {
            LOG.warn(
                "JDK classes on the auxclasspath are detected to be for Java {}, but you are analyzing Java {} sources. "
                    + "This may cause false positives and other incorrect type resolution results. "
                    + "Please add the JDK classes of Java {} on your auxclasspath (see https://docs.pmd-code.org/latest/pmd_languages_java.html#providing-the-auxiliary-classpath).",
                major, analysisVersion, analysisVersion);
        }
    }

    // Note: we could use ASM for this but ASM reads the entire class file greedily when
    // we just need the first 8 bytes. Also, ASM forces us to specify an API version that
    // we must update manually when the class file format is updated, so using it is not
    // as forward compatible as just checking the first bytes manually.
    private static OptionalInt findClassVersion(ClassLoader classLoader, String classFilePath) {
        try (InputStream stream = classLoader.getResourceAsStream(classFilePath)) {
            if (stream == null) {
                return OptionalInt.empty();
            }
            // the class file header is 4 bytes for the magic number,
            // 2 bytes for the minor version, 2 bytes for the major version
            byte[] bytes = new byte[8];
            if (stream.read(bytes) != bytes.length) {
                return OptionalInt.empty();
            }

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            if (in.readInt() != 0xCAFEBABE) { // class file magic number
                return OptionalInt.empty();
            }
            int ignoredMinorVersion = in.readUnsignedShort();
            int majorVersion = in.readUnsignedShort();
            return OptionalInt.of(majorVersion);
        } catch (IOException ignored) {
        }
        return OptionalInt.empty();
    }

}