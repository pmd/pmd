/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
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
        OptionalInt jdkVer = getVersionOfObject(loader);
        if (!jdkVer.isPresent()) {
            // there is a problem, we couldn't load java.lang.Object from the classpath...
            return;
        }

        int version = jdkVer.getAsInt();
        version = Math.max(version, 3); // our versions start at 3

        int analysisVersion = JavaLanguageProperties.getInternalJdkVersion(currentJavaVersion);


        String message =
            "JDK classes on the auxclasspath are detected to be for Java {}, but you are analyzing Java {} sources. "
                + "Please add the JDK classes of Java {} on your auxclasspath (see https://docs.pmd-code.org/latest/pmd_languages_java.html#providing-the-auxiliary-classpath).";
        Object[] params = {version, analysisVersion, analysisVersion};
        if (analysisVersion > version) {
            // This is a warning because it's much more likely that this causes problems.
            LOG.warn(message, params);
        } else {
            LOG.debug(message, params);
        }
    }


    static OptionalInt getVersionOfObject(ClassLoader classLoader) {
        OptionalInt internalVersion = findClassVersion(classLoader, "java/lang/Object.class");
        if (!internalVersion.isPresent()) {
            return OptionalInt.empty();
        }
        int version = internalVersion.getAsInt();
        int major = version & 0xffff;
        // https://javaalmanac.io/bytecode/versions/
        // Our java versions start at 1.3, which is version 47.0.
        // Minor version is only non-zero in 1.1 so we don't care about it
        // Then it increments by 1 for each version.
        assert major >= 45 : "major version is less than 45 (Java 1.0)";
        int jdkVersion = major - 44;
        return OptionalInt.of(jdkVersion); // this ranges from 1,2,3...25 for JDK 25 for instance.
    }

    private static OptionalInt findClassVersion(ClassLoader classLoader, String classFilePath) {

        class FoundVersionException extends RuntimeException {
            private final int internalVersion;

            private FoundVersionException(int internalVersion) {
                this.internalVersion = internalVersion;
            }
        }

        try (InputStream stream = classLoader.getResourceAsStream(classFilePath)) {
            if (stream == null) {
                return OptionalInt.empty();
            }

            ClassReader classReader = new ClassReader(stream);
            try {
                classReader.accept(new ClassVisitor(Opcodes.ASM9) {
                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        throw new FoundVersionException(version);
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } catch (FoundVersionException found) {
                return OptionalInt.of(found.internalVersion);
            }
        } catch (IOException ignored) {
        }
        return OptionalInt.empty();
    }

}