/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.rule.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.lang.kotlin.types.InternalApiBridge;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeAnnotationVisitor;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.TypedAst;

/**
 * Encapsulates kotlin-type-mapper analysis and per-file annotation behavior.
 */
final class KotlinTypeAwarenessSupport {

    private static final Logger LOG = LoggerFactory.getLogger(KotlinTypeAwarenessSupport.class);

    /** Populated in {@link #prepare} before any file is parsed. */
    private KotlinTypeAnnotationVisitor annotationVisitor;
    private KotlinTypeAnalysisContext analysisContext = KotlinTypeAnalysisContext.empty();

    private final KotlinAuxClasspathResolver classpathResolver;

    KotlinTypeAwarenessSupport(KotlinAuxClasspathResolver classpathResolver) {
        this.classpathResolver = classpathResolver;
    }

    @SuppressWarnings("PMD.CloseResource") // TextFile lifecycle is managed by PMD framework.
    void prepare(List<TextFile> allFiles, Language language) {
        List<TextFile> ktFiles = new ArrayList<>();
        for (TextFile textFile : allFiles) {
            if (textFile.getLanguageVersion().getLanguage().equals(language)) {
                ktFiles.add(textFile);
            }
        }
        if (ktFiles.isEmpty()) {
            return;
        }

        // Use fromPaths when all files are on disk: KTM reads files itself, so source
        // strings are not duplicated between PMD's TextFile and the analyser.
        // The in-memory fallback (fromSources) is needed for the PMD rule test framework,
        // which supplies Kotlin snippets as virtual in-memory files (not on disk).
        List<Path> sourcePaths = new ArrayList<>(ktFiles.size());
        boolean allOnDisk = true;
        for (TextFile ktFile : ktFiles) {
            Path p = Paths.get(ktFile.getFileId().getFileName());
            if (Files.isRegularFile(p)) {
                sourcePaths.add(p);
            } else {
                allOnDisk = false;
                break;
            }
        }

        KotlinTypeAnnotationVisitor visitor;
        if (allOnDisk) {
            visitor = analyzeAndBuildVisitorFromPaths(sourcePaths);
        } else {
            visitor = analyzeAndBuildVisitor(buildSourceMap(ktFiles));
        }
        annotationVisitor = visitor;
        if (visitor != null) {
            LOG.debug("kotlin-type-mapper analyzed {} file(s)", ktFiles.size());
        }
    }

    void annotateIfPossible(KtKotlinFile root, String absPath) {
        validateKotlinPath(absPath);
        if (annotationVisitor != null) {
            annotationVisitor.annotate(root, absPath);
            InternalApiBridge.setAnalysisContext(root, analysisContext);
            InternalApiBridge.setTypeInfoAvailable(root);
        }
    }

    void clear() {
        annotationVisitor = null;
        analysisContext = KotlinTypeAnalysisContext.empty();
    }

    private KotlinTypeAnnotationVisitor analyzeAndBuildVisitorFromPaths(List<Path> sourcePaths) {
        TypedAst ast = KotlinTypeMapper.fromPaths(sourcePaths, classpathResolver.resolve());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        analysisContext = ctx;
        return new KotlinTypeAnnotationVisitor(ctx);
    }

    private KotlinTypeAnnotationVisitor analyzeAndBuildVisitor(Map<String, String> sources) {
        TypedAst ast = KotlinTypeMapper.fromSources(sources, toFiles(classpathResolver.resolve()));
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        analysisContext = ctx;
        return new KotlinTypeAnnotationVisitor(ctx);
    }

    @SuppressWarnings("PMD.CloseResource") // TextFile lifecycle is managed by PMD framework.
    private static Map<String, String> buildSourceMap(List<TextFile> ktFiles) {
        Map<String, String> sources = new LinkedHashMap<>();
        for (TextFile ktFile : ktFiles) {
            try {
                String filename = ktFile.getFileId().getAbsolutePath();
                String text = ktFile.readContents().getNormalizedText().toString();
                sources.put(filename, text);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return sources;
    }

    void prepareForSingleDocument(TextDocument doc) {
        String absPath = doc.getFileId().getAbsolutePath();
        String text = doc.getText().toString();
        KotlinTypeAnnotationVisitor visitor = analyzeAndBuildVisitor(Collections.singletonMap(absPath, text));
        annotationVisitor = visitor;
        if (visitor != null) {
            LOG.debug("kotlin-type-mapper single-file analysis complete for {}", absPath);
        }
    }

    static void validateKotlinPath(String absPath) {
        if (absPath == null || absPath.isEmpty()) {
            throw new IllegalStateException("kotlin type analysis: file has no absPath — this is a PMD bug");
        }
    }

    private static List<File> toFiles(List<Path> paths) {
        List<File> files = new ArrayList<>(paths.size());
        for (Path p : paths) {
            files.add(p.toFile());
        }
        return files;
    }
}
