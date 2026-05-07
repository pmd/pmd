/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.TypedAst;

/**
 * Test utility that runs kotlin-type-mapper analysis on Kotlin source files (or inline
 * code strings) and injects the resulting {@link KotlinTypeAnalysisContext} into
 * {@link KotlinTypeAnalysisContextHolder} so XPath functions have type data available.
 *
 * <p>Usage in tests:
 * <pre>{@code
 * @BeforeEach
 * void setUp() {
 *     helper = KotlinTypeXPathTestHelper.forDirectory(testResourceDir);
 *     helper.injectContext();
 * }
 *
 * @AfterEach
 * void tearDown() {
 *     KotlinTypeAnalysisContextHolder.clear();
 * }
 * }</pre>
 */
public class KotlinTypeXPathTestHelper implements AutoCloseable {

    private final File sourceDir;
    private File tempDir;

    KotlinTypeXPathTestHelper(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    /** Creates a helper that will analyze all .kt files in the given directory. */
    public static KotlinTypeXPathTestHelper forDirectory(File dir) {
        return new KotlinTypeXPathTestHelper(dir);
    }

    /** Returns the source directory used by this helper (for locating snippet files). */
    public File getSourceDir() {
        return sourceDir;
    }

    /**
     * Creates a helper that will analyze a single Kotlin source code string.
     * The code is written to a temporary file named {@code snippet.kt}.
     */
    public static KotlinTypeXPathTestHelper forCode(String kotlinCode) {
        try {
            File tempDir = Files.createTempDirectory("pmd-kotlin-test-").toFile();
            File snippetFile = new File(tempDir, "snippet.kt");
            Files.write(snippetFile.toPath(), normalizeLf(kotlinCode).getBytes(StandardCharsets.UTF_8));
            KotlinTypeXPathTestHelper helper = new KotlinTypeXPathTestHelper(tempDir) {
                @Override
                public void close() {
                    KotlinTypeAnalysisContextHolder.clear();
                    deleteRecursively(tempDir);
                }
            };
            helper.tempDir = tempDir;
            return helper;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Runs kotlin-type-mapper analysis and injects the context into the global holder
     * so PMD's worker threads can access it during analysis.
     * Call this in {@code @BeforeEach}.
     *
     * <p>Source files are copied to a temp directory with LF line endings before
     * analysis to match PMD's own line-ending normalization. Without this, K1
     * analysis on Windows (where files are checked out with CRLF) would produce
     * offsets that do not align with PMD's LF-normalized text, causing position
     * mismatches and "Wrong line separators" processing errors.
     */
    public void injectContext() {
        File analysisDir = writeLfNormalizedTempDir(sourceDir);
        try {
            TypedAst ast = new KotlinTypeMapper(analysisDir, new java.util.ArrayList<>(), false).analyze();
            KotlinTypeAnalysisContextHolder.setGlobal(KotlinTypeAnalysisContext.from(ast));
        } finally {
            deleteRecursively(analysisDir);
        }
    }

    private static File writeLfNormalizedTempDir(File sourceDir) {
        try {
            File tempDir = Files.createTempDirectory("pmd-kotlin-test-norm-").toFile();
            File[] ktFiles = sourceDir.listFiles((d, name) -> name.endsWith(".kt"));
            if (ktFiles != null) {
                for (File src : ktFiles) {
                    String content = new String(Files.readAllBytes(src.toPath()), StandardCharsets.UTF_8);
                    Files.write(new File(tempDir, src.getName()).toPath(),
                            normalizeLf(content).getBytes(StandardCharsets.UTF_8));
                }
            }
            return tempDir;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String normalizeLf(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }

    @Override
    public void close() {
        KotlinTypeAnalysisContextHolder.clearGlobal();
        if (tempDir != null) {
            deleteRecursively(tempDir);
        }
    }

    static void deleteRecursively(File file) {
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
}
