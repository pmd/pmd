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
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.Language;
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
    private final AtomicReference<KotlinTypeAnnotationVisitor> annotationVisitor = new AtomicReference<>();
    private final AtomicReference<KotlinTypeAnalysisContext> analysisContext =
            new AtomicReference<>(KotlinTypeAnalysisContext.empty());

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

        // Prefer fromPaths when all files are on disk: KTM reads files itself, so source
        // strings are not duplicated between PMD's TextFile and the analyser.
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
        annotationVisitor.set(visitor);
        if (visitor != null) {
            LOG.debug("kotlin-type-mapper analyzed {} file(s)", ktFiles.size());
        }
    }

    void annotateIfPossible(KtKotlinFile root, String absPath, String sourceText) {
        KotlinTypeAnnotationVisitor visitor = annotationVisitor.get();
        if (visitor == null) {
            // Designer / single-file mode: prepare() was never called.
            String effectiveName = KotlinLanguageProcessor.sanitizeKtFilename(absPath);
            visitor = runSingleFileAnalysis(effectiveName, sourceText);
            if (visitor != null) {
                visitor.annotate(root, effectiveName);
                // Use the ctx the visitor was built with — avoids an AtomicRef re-read that
                // could return a different ctx if a concurrent single-file analysis raced here.
                InternalApiBridge.setAnalysisContext(root, visitor.getContext());
                InternalApiBridge.setTypeInfoAvailable(root);
            }
        } else {
            visitor.annotate(root, absPath);
            InternalApiBridge.setAnalysisContext(root, analysisContext.get());
            InternalApiBridge.setTypeInfoAvailable(root);
        }
    }

    void clear() {
        annotationVisitor.set(null);
        analysisContext.set(KotlinTypeAnalysisContext.empty());
    }

    private KotlinTypeAnnotationVisitor analyzeAndBuildVisitorFromPaths(List<Path> sourcePaths) {
        TypedAst ast = KotlinTypeMapper.fromPaths(sourcePaths, classpathResolver.resolve());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        analysisContext.set(ctx);
        return new KotlinTypeAnnotationVisitor(ctx);
    }

    private KotlinTypeAnnotationVisitor analyzeAndBuildVisitor(Map<String, String> sources) {
        TypedAst ast = KotlinTypeMapper.fromSources(sources, toFiles(classpathResolver.resolve()));
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        analysisContext.set(ctx);
        return new KotlinTypeAnnotationVisitor(ctx);
    }

    @SuppressWarnings("PMD.CloseResource") // TextFile lifecycle is managed by PMD framework.
    private static Map<String, String> buildSourceMap(List<TextFile> ktFiles) {
        Map<String, String> sources = new LinkedHashMap<>();
        for (TextFile ktFile : ktFiles) {
            try {
                String filename = KotlinLanguageProcessor.sanitizeKtFilename(ktFile.getFileId().getFileName());
                String text = ktFile.readContents().getNormalizedText().toString();
                sources.put(filename, text);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return sources;
    }

    private KotlinTypeAnnotationVisitor runSingleFileAnalysis(String filename, String sourceText) {
        KotlinTypeAnnotationVisitor visitor = analyzeAndBuildVisitor(Collections.singletonMap(filename, sourceText));
        if (visitor != null) {
            LOG.debug("kotlin-type-mapper single-file analysis complete for {}", filename);
        }
        return visitor;
    }

    private static List<File> toFiles(List<Path> paths) {
        List<File> files = new ArrayList<>(paths.size());
        for (Path p : paths) {
            files.add(p.toFile());
        }
        return files;
    }
}
