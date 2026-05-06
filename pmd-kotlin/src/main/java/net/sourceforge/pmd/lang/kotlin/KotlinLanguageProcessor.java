/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextFileContent;
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTypeAnnotationVisitor;
import net.sourceforge.pmd.lang.kotlin.internal.KotlinDesignerBindings;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeAnalysisContextHolder;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

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
 */
public class KotlinLanguageProcessor extends BatchLanguageProcessor<LanguagePropertyBundle> {

    private static final Logger LOG = LoggerFactory.getLogger(KotlinLanguageProcessor.class);


    /** Populated in {@link #launchAnalysis} before any file is parsed. */
    private final AtomicReference<KotlinTypeAnnotationVisitor> annotationVisitor = new AtomicReference<>();

    private final KotlinHandler baseHandler;
    private final KotlinAuxClasspathResolver classpathResolver;

    KotlinLanguageProcessor(JvmLanguagePropertyBundle bundle, KotlinHandler handler) {
        super(bundle);
        this.baseHandler = handler;
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

    private void runTypeAnalysis(List<TextFile> allFiles) {
        List<TextFile> ktFiles = new ArrayList<>();
        for (int i = 0; i < allFiles.size(); i++) {
            if (allFiles.get(i).getLanguageVersion().getLanguage().equals(getLanguage())) {
                ktFiles.add(allFiles.get(i));
            }
        }
        if (ktFiles.isEmpty()) {
            return;
        }

        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory("pmd-kotlin-analysis-").toFile();
            writeToTempDir(ktFiles, tempDir);

            TypedAst ast = new KotlinTypeMapper(tempDir, classpathResolver.resolve(), false).analyze();
            KotlinTypeAnalysisContext context = KotlinTypeAnalysisContext.from(ast);
            KotlinTypeAnalysisContextHolder.setGlobal(context);
            annotationVisitor.set(new KotlinTypeAnnotationVisitor(ast));
            LOG.debug("kotlin-type-mapper analyzed {} file(s)", ktFiles.size());
        } catch (IOException | RuntimeException e) {
            LOG.warn("kotlin-type-mapper analysis failed; typeIs/matchesSig will return false", e);
        } finally {
            if (tempDir != null) {
                deleteRecursively(tempDir);
            }
        }
    }

    private static void writeToTempDir(List<TextFile> ktFiles, File tempDir) throws IOException {
        for (int i = 0; i < ktFiles.size(); i++) {
            String filename = sanitizeKtFilename(ktFiles.get(i).getFileId().getFileName());
            TextFileContent content = ktFiles.get(i).readContents();
            String text = content.getNormalizedText().toString();
            Files.write(new File(tempDir, filename).toPath(),
                        text.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    void annotateIfPossible(KotlinNode root, String absPath, String sourceText) {
        KotlinTypeAnnotationVisitor v = annotationVisitor.get();
        if (v == null) {
            // Designer / single-file mode: launchAnalysis() was never called, so run
            // kotlin-type-mapper inline on this one file.
            String effectiveName = sanitizeKtFilename(absPath);
            v = runSingleFileAnalysis(effectiveName, sourceText);
            if (v != null) {
                v.annotate(root, effectiveName);
            }
        } else {
            v.annotate(root, absPath);
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
        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory("pmd-kotlin-analysis-").toFile();
            Files.write(new File(tempDir, filename).toPath(),
                        sourceText.getBytes(StandardCharsets.UTF_8));
            TypedAst ast = new KotlinTypeMapper(tempDir, classpathResolver.resolve(), false).analyze();
            KotlinTypeAnalysisContext context = KotlinTypeAnalysisContext.from(ast);
            KotlinTypeAnalysisContextHolder.setGlobal(context);
            LOG.debug("kotlin-type-mapper single-file analysis complete for {}", filename);
            return new KotlinTypeAnnotationVisitor(ast);
        } catch (IOException | RuntimeException e) {
            LOG.warn("kotlin-type-mapper single-file analysis failed for {}; typeIs/matchesSig will return false", filename, e);
            return null;
        } finally {
            if (tempDir != null) {
                deleteRecursively(tempDir);
            }
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
        public DesignerBindings getDesignerBindings() {
            return KotlinDesignerBindings.INSTANCE;
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
}
