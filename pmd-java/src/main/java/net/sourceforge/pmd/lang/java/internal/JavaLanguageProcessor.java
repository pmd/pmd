/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.internal.util.AuxClasspathLoader;
import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
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
import net.sourceforge.pmd.util.internal.AuxClasspathUtil;

/**
 * @author Clément Fournier
 */
public class JavaLanguageProcessor extends BatchLanguageProcessor<JavaLanguageProperties>
    implements LanguageVersionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JavaLanguageProcessor.class);

    private final LanguageMetricsProvider myMetricsProvider = new JavaMetricsProvider();
    private final JavaParser parser;
    private final JavaParser parserWithoutProcessing;
    private final boolean firstClassLombok;
    private TypeSystem typeSystem;
    private AuxClasspathLoader auxClasspathLoader;

    public JavaLanguageProcessor(JavaLanguageProperties properties) {
        super(properties);

        initTypeSystem(properties);

        String suppressMarker = properties.getSuppressMarker();
        this.parser = new JavaParser(suppressMarker, this, true);
        this.parserWithoutProcessing = new JavaParser(suppressMarker, this, false);
        this.firstClassLombok = properties.getProperty(JavaLanguageProperties.FIRST_CLASS_LOMBOK);
    }

    private void initTypeSystem(JavaLanguageProperties properties) {
        ClassLoader externallyConfiguredClassLoader = properties.getExternalClassLoader();
        if (externallyConfiguredClassLoader != null) {
            LOG.debug("Using externally configured classloader as analysis classloader: {}", externallyConfiguredClassLoader);
            this.typeSystem = TypeSystem.usingClassLoaderClasspath(externallyConfiguredClassLoader);
        } else {
            String auxClasspath = properties.getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH);

            Path relativeJrtFsJar = Paths.get("lib/jrt-fs.jar");
            Path relativeRtJar = Paths.get("lib/rt.jar");
            if (!auxClasspath.contains(relativeJrtFsJar.toString()) && !auxClasspath.contains(relativeRtJar.toString())) {
                Path platformClasspath = AuxClasspathUtil.getPlatformClasspath();
                auxClasspath += File.pathSeparator + platformClasspath;
            }
            LOG.debug("Using auxClasspath as analysis classloader: {}", auxClasspath);
            this.auxClasspathLoader = AuxClasspathLoader.create(auxClasspath);
            this.typeSystem = TypeSystem.usingClasspath(name -> auxClasspathLoader.findResource(name));
        }
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
        return JavaAnnotationSuppressor.ALL_JAVA_SUPPRESSORS;
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
        if (this.auxClasspathLoader != null) {
            this.auxClasspathLoader.close();
        }
        super.close();
    }
}
