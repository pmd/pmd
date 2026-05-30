/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeAnalysisContextHolder;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeAnnotationVisitor;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.TypedAst;

/**
 * Language processor for Kotlin. Extends the default batch processor with a
 * pre-analysis step: before any file is parsed, all Kotlin source files are
 * analyzed by kotlin-type-mapper to resolve types. The resulting type data is
 * then:
 * <ol>
 *   <li>Set as node attributes ({@code @TypeName}, {@code @ReturnTypeName}) on each
 *       {@code PropertyDeclaration} / {@code FunctionDeclaration} node during parsing,
 *       making them available in the PMD Designer and via XPath {@code @} syntax.</li>
 *   <li>Stored in {@link KotlinTypeAnalysisContextHolder} for use by the
 *       {@code pmd-kotlin:typeIs()} and {@code pmd-kotlin:matchesSig()} XPath functions.</li>
 * </ol>
 *
 * <p>If type analysis fails (e.g. Kotlin compiler not on classpath), the processor
 * falls back gracefully: nodes have no type attributes and the custom XPath functions
 * return {@code false} for all nodes, so rule evaluation still completes.
 *
 * @since 7.25.0
 */
public class KotlinLanguageProcessor extends BatchLanguageProcessor<KotlinLanguageProperties> {
    private static final Logger LOG = LoggerFactory.getLogger(KotlinLanguageProcessor.class);

    private final ExecutorService parseTimeoutExecutor =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "kotlin-parse-timeout");
                t.setDaemon(true);
                return t;
            });

    /** Populated in {@link #launchAnalysis} before any file is parsed. */
    private final AtomicReference<KotlinTypeAnnotationVisitor> annotationVisitor = new AtomicReference<>();

    private final KotlinHandler baseHandler;
    private final KotlinAuxClasspathResolver classpathResolver;

    KotlinLanguageProcessor(KotlinLanguageProperties bundle) {
        super(bundle);
        this.baseHandler = new KotlinHandler(parseTimeoutExecutor);
        this.classpathResolver = new KotlinAuxClasspathResolver(bundle);
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return new AnnotatingKotlinHandler();
    }

    @Override
    public @NonNull AutoCloseable launchAnalysis(@NonNull AnalysisTask task) {
        runTypeAnalysis(task.getFiles());
        return super.launchAnalysis(task);
    }

    @SuppressWarnings("PMD.CloseResource") // TextFile lifecycle is managed by PMD framework.
    private void runTypeAnalysis(List<TextFile> allFiles) {
        List<TextFile> ktFiles = new ArrayList<>();
        for (TextFile textFile : allFiles) {
            if (textFile.getLanguageVersion().getLanguage().equals(getLanguage())) {
                ktFiles.add(textFile);
            }
        }
        if (ktFiles.isEmpty()) {
            return;
        }

        Map<String, String> sources = buildSourceMap(ktFiles);

        try {
            TypedAst ast = KotlinTypeMapper.fromSources(sources, classpathResolver.resolve());
            KotlinTypeAnalysisContext context = KotlinTypeAnalysisContext.from(ast);
            KotlinTypeAnalysisContextHolder.setGlobal(context);
            annotationVisitor.set(new KotlinTypeAnnotationVisitor(ast));
            LOG.debug("kotlin-type-mapper analyzed {} file(s)", ktFiles.size());
        } catch (RuntimeException e) {
            KotlinTypeAnalysisContextHolder.clearGlobal();
            annotationVisitor.set(null);
            LOG.warn("kotlin-type-mapper analysis failed; typeIs/matchesSig will return false", e);
        }
    }

    @SuppressWarnings("PMD.CloseResource") // TextFile lifecycle is managed by PMD framework (closed by PMDRunnable); same pattern used in MonoThreadProcessor
    private static Map<String, String> buildSourceMap(List<TextFile> ktFiles) {
        Map<String, String> sources = new LinkedHashMap<>();
        for (TextFile ktFile : ktFiles) {
            try {
                String filename = sanitizeKtFilename(ktFile.getFileId().getFileName());
                String text = ktFile.readContents().getNormalizedText().toString();
                sources.put(filename, text);
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
        }
        return sources;
    }

    void annotateIfPossible(KotlinNode root, String absPath, String sourceText) {
        KotlinTypeAnnotationVisitor visitor = annotationVisitor.get();
        if (visitor == null) {
            // Designer / single-file mode: launchAnalysis() was never called, so run
            // kotlin-type-mapper inline on this one file.
            String effectiveName = sanitizeKtFilename(absPath);
            visitor = runSingleFileAnalysis(effectiveName, sourceText);
            if (visitor != null) {
                visitor.annotate(root, effectiveName);
            }
        } else {
            visitor.annotate(root, absPath);
        }
    }

    /**
     * Returns a valid {@code .kt} filename derived from {@code absPath}.
     * Appends {@code .kt} when the name lacks it; falls back to {@code "snippet.kt"}
     * for empty or path-separator-containing names (e.g. Designer's synthetic paths).
     */
    static String sanitizeKtFilename(String absPath) {
        String name = new File(absPath).getName();
        if (name.endsWith(".kt")) {
            return name;
        }
        if (name.isEmpty() || name.contains(File.separator)) {
            return "snippet.kt";
        }
        return name + ".kt";
    }

    private KotlinTypeAnnotationVisitor runSingleFileAnalysis(String filename, String sourceText) {
        try {
            TypedAst ast = KotlinTypeMapper.fromSources(
                    Collections.singletonMap(filename, sourceText), classpathResolver.resolve());
            KotlinTypeAnalysisContext context = KotlinTypeAnalysisContext.from(ast);
            KotlinTypeAnalysisContextHolder.setGlobal(context);
            LOG.debug("kotlin-type-mapper single-file analysis complete for {}", filename);
            return new KotlinTypeAnnotationVisitor(ast);
        } catch (RuntimeException e) {
            KotlinTypeAnalysisContextHolder.clearGlobal();
            LOG.warn("kotlin-type-mapper single-file analysis failed for {}; typeIs/matchesSig will return false", filename, e);
            return null;
        }
    }

    /**
     * Wraps {@link KotlinHandler#getParser()} to run the type annotation visitor
     * after each file is parsed. All other services delegate to the base handler.
     */
    private final class AnnotatingKotlinHandler extends KotlinHandler {

        @Override
        public XPathHandler getXPathHandler() {
            return baseHandler.getXPathHandler();
        }

        @Override
        public Parser getParser() {
            final Parser base = baseHandler.getParser();
            return task -> {
                RootNode root = base.parse(task);
                annotateIfPossible(
                        (KotlinNode) root,
                        task.getTextDocument().getFileId().getAbsolutePath(),
                        task.getTextDocument().getText().toString());
                return root;
            };
        }
    }

    @Override
    public void close() throws Exception {
        parseTimeoutExecutor.shutdown();
        boolean result = parseTimeoutExecutor.awaitTermination(getProperties().getParseTimeoutSeconds() * 2L, TimeUnit.SECONDS);
        if (!result) {
            LOG.error("Couldn't properly shutdown parseTimeoutExecutor - threads might still be running!");
        }
        KotlinTypeAnalysisContextHolder.clearGlobal();
        super.close();
    }
}
