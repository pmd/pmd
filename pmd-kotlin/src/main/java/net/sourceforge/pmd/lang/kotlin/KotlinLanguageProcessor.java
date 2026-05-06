/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

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
    private static final String FILE_PROTOCOL = "file";

    /** Populated in {@link #launchAnalysis} before any file is parsed. */
    private final AtomicReference<KotlinTypeAnnotationVisitor> annotationVisitor = new AtomicReference<>();

    private final KotlinHandler baseHandler;
    private final JvmLanguagePropertyBundle jvmBundle;

    KotlinLanguageProcessor(JvmLanguagePropertyBundle bundle, KotlinHandler handler) {
        super(bundle);
        this.baseHandler = handler;
        this.jvmBundle = bundle;
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

            TypedAst ast = new KotlinTypeMapper(tempDir, getAuxClasspathEntries(), false).analyze();
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

    /**
     * Returns the auxiliary classpath entries configured for this analysis as a list
     * of File objects, forwarded to kotlin-type-mapper so it can resolve external types
     * (e.g. Spring, JPA annotations).
     *
     * <p>Resolution order:
     * <ol>
     *   <li>String property -- set when {@code --aux-classpath} is passed on the command line.</li>
     *   <li>URLClassLoader hierarchy -- how the PMD Designer propagates the auxiliary classpath.</li>
     *   <li>{@code java.class.path} system property -- Maven Surefire puts all test dependencies here.</li>
     * </ol>
     */
    private List<File> getAuxClasspathEntries() {
        // 1. String property (set via --aux-classpath on the command line)
        String raw = jvmBundle.getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH);
        if (raw != null && !raw.isEmpty()) {
            String sep = System.getProperty("path.separator", ":");
            List<File> entries = new ArrayList<>();
            for (String entry : raw.split(Pattern.quote(sep))) {
                String trimmed = entry.trim();
                if (!trimmed.isEmpty()) {
                    entries.add(new File(trimmed));
                }
            }
            LOG.debug("kotlin-type-mapper aux classpath from string property ({} entries)", entries.size());
            return filterAuxClasspathEntries(entries, "aux-classpath property");
        }
        // 2. URLClassLoader hierarchy -- PMD Designer (and CLI via ClasspathClassLoader) sets this.
        ClassLoader cl = jvmBundle.getAnalysisClassLoader();
        List<File> urlEntries = new ArrayList<>();
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                for (URL url : ((URLClassLoader) cl).getURLs()) {
                    if (FILE_PROTOCOL.equals(url.getProtocol())) {
                        try {
                            urlEntries.add(new File(url.toURI()));
                        } catch (URISyntaxException e) {
                            LOG.debug("Could not convert classpath URL to File: {}", url);
                        }
                    }
                }
            }
            cl = cl.getParent();
        }
        if (!urlEntries.isEmpty()) {
            LOG.debug("kotlin-type-mapper aux classpath from URLClassLoader hierarchy ({} entries)", urlEntries.size());
            return filterAuxClasspathEntries(urlEntries, "analysis classloader");
        }
        // 3. java.class.path system property -- Maven Surefire puts all test dependencies here.
        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null && !javaClassPath.isEmpty()) {
            List<File> entries = new ArrayList<>();
            for (String entry : javaClassPath.split(Pattern.quote(File.pathSeparator))) {
                if (!entry.isEmpty()) {
                    entries.add(new File(entry));
                }
            }
            LOG.debug("kotlin-type-mapper aux classpath from java.class.path ({} entries)", entries.size());
            return filterAuxClasspathEntries(entries, "java.class.path");
        }
        return new ArrayList<>();
    }

    private static List<File> filterAuxClasspathEntries(List<File> entries, String source) {
        List<File> filtered = new ArrayList<>(entries.size());
        for (File entry : entries) {
            if (entry.exists() && (entry.isDirectory() || entry.getName().endsWith(".jar"))) {
                filtered.add(entry);
            } else {
                LOG.warn("Skipping invalid Kotlin aux classpath entry from {}: {}", source, entry);
            }
        }
        return filtered;
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
            TypedAst ast = new KotlinTypeMapper(tempDir, getAuxClasspathEntries(), false).analyze();
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
