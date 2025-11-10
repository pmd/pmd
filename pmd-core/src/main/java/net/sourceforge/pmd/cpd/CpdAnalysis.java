/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.cpd.TokenFileSet.TokenFile;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.InternalApiBridge;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Main programmatic API of CPD. This is not a CLI entry point, see module
 * {@code pmd-cli} for that.
 *
 * <h2>Usage overview</h2>
 *
 * <p>Create and configure a {@link CPDConfiguration}, then use {@link #create(CPDConfiguration)} to
 * obtain an instance. You can perform additional configuration on the instance, e.g. adding
 * files to process or add a listener. Then call {@link #performAnalysis()} or {@link #performAnalysis(Consumer)}
 * in order to get the report directly.
 *
 * <h2>Simple example</h2>
 *
 * <pre>{@code
 *   CPDConfiguration config = new CPDConfiguration();
 *   config.setMinimumTileSize(100);
 *   config.setOnlyRecognizeLanguage(config.getLanguageRegistry().getLanguageById("java"));
 *   config.setSourceEncoding(StandardCharsets.UTF_8);
 *   config.addInputPath(Path.of("src/main/java")
 *
 *   config.setIgnoreAnnotations(true);
 *   config.setIgnoreLiterals(false);
 *
 *   config.setRendererName("text");
 *
 *   try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
 *      // note: don't use `config` once a CpdAnalysis has been created.
 *      // optional: add more files
 *      cpd.files().addFile(Paths.get("src", "main", "more-java", "ExtraSource.java"));
 *
 *      cpd.performAnalysis();
 *   }
 * }</pre>
 */
public final class CpdAnalysis implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpdAnalysis.class);
    private final CPDConfiguration configuration;
    private final FileCollector files;
    private final PmdReporter reporter;
    private final @Nullable CPDReportRenderer renderer;
    private @NonNull CPDListener listener = new CPDNullListener();


    private CpdAnalysis(CPDConfiguration config) {
        this.configuration = config;
        this.reporter = config.getReporter();
        this.files = InternalApiBridge.newCollector(
            config.getLanguageVersionDiscoverer(),
            reporter
        );

        this.renderer = config.getCPDReportRenderer();

        FileCollectionUtil.collectFiles(config, files());

        for (Language language : config.getLanguageRegistry()) {
            setLanguageProperties(language, config);
        }
    }

    /**
     * Create a new instance from the given configuration. The configuration
     * should not be modified after this.
     *
     * @param config Configuration
     *
     * @return A new analysis instance
     */
    public static CpdAnalysis create(CPDConfiguration config) {
        return new CpdAnalysis(config);
    }

    private static <T> void setPropertyIfMissing(PropertyDescriptor<T> prop, LanguagePropertyBundle sink, T value) {
        if (sink.hasDescriptor(prop) && !sink.isPropertyOverridden(prop)) {
            sink.setProperty(prop, value);
        }
    }

    static List<Match> findMatches(SourceManager sourceManager, @NonNull CPDListener cpdListener, TokenFileSet tokens, int minTileSize) {
        cpdListener.phaseUpdate(CPDListener.HASH);
        tokens.setState(TokenFileSet.CpdState.HASHING);
        List<List<TokenFileSet.SmallTokenEntry>> markGroups = tokens.hashAll(minTileSize);

        MatchCollector matchCollector = new MatchCollector(sourceManager, tokens, minTileSize);
        cpdListener.phaseUpdate(CPDListener.MATCH);
        tokens.setState(TokenFileSet.CpdState.MATCHING);
        markGroups.forEach(matchCollector::collect);

        cpdListener.phaseUpdate(CPDListener.DONE);
        return matchCollector.getMatches();
    }

    private void setLanguageProperties(Language language, CPDConfiguration configuration) {
        LanguagePropertyBundle props = configuration.getLanguageProperties(language);

        setPropertyIfMissing(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS, props, configuration.isIgnoreLiterals());
        setPropertyIfMissing(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS, props, configuration.isIgnoreIdentifiers());
        setPropertyIfMissing(CpdLanguageProperties.CPD_IGNORE_METADATA, props, configuration.isIgnoreAnnotations());
        setPropertyIfMissing(CpdLanguageProperties.CPD_IGNORE_IMPORTS, props, configuration.isIgnoreUsings());
        setPropertyIfMissing(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES, props, configuration.isIgnoreLiteralSequences());
        setPropertyIfMissing(CpdLanguageProperties.CPD_IGNORE_LITERAL_AND_IDENTIFIER_SEQUENCES, props, configuration.isIgnoreIdentifierAndLiteralSequences());
        if (!configuration.isNoSkipBlocks()) {
            // see net.sourceforge.pmd.lang.cpp.CppLanguageModule.CPD_SKIP_BLOCKS
            PropertyDescriptor<String> skipBlocks = (PropertyDescriptor) props.getPropertyDescriptor("cpdSkipBlocksPattern");
            setPropertyIfMissing(skipBlocks, props, configuration.getSkipBlocksPattern());
        }
    }

    public FileCollector files() {
        return files;
    }

    public void setCpdListener(@Nullable CPDListener cpdListener) {
        if (cpdListener == null) {
            cpdListener = new CPDNullListener();
        }
        this.listener = cpdListener;
    }

    private int doTokenize(TextDocument document, CpdLexer cpdLexer, TokenFileSet tokens, int index) throws IOException, LexException {
        LOGGER.trace("Tokenizing {}", document.getFileId().getAbsolutePath());
        TokenFile tokenFile = tokens.tokenize(document, cpdLexer, index);
        return tokenFile.size();
    }

    public void performAnalysis() {
        performAnalysis(r -> { });
    }

    public void performAnalysis(Consumer<CPDReport> consumer) {
        try (SourceManager sourceManager = new SourceManager(files.getCollectedFiles())) {
            if (sourceManager.isEmpty()) {
                reporter.warn("No files to analyze. Check input paths and exclude parameters, use --debug to see file collection traces.");
            }

            CPDReport cpdReport = findMatches(sourceManager);
            if (cpdReport == null) {
                return;
            }

            if (renderer != null) {
                Path reportFilePath = configuration.getReportFilePath();
                String reportFileAsString = reportFilePath != null ? reportFilePath.toAbsolutePath().toString() : null;
                try (Writer writer = IOUtil.createWriter(Charset.defaultCharset(), reportFileAsString)) {
                    renderer.render(cpdReport, writer);
                }
            }

            consumer.accept(cpdReport);
        } catch (IOException | ExecutionException | InterruptedException e) {
            reporter.errorEx("Exception while running CPD", e);
        }
        // source manager is closed and closes all text files now.
    }

    private CPDReport findMatches(SourceManager sourceManager) throws ExecutionException, InterruptedException {
        Map<Language, CpdLexer> tokenizers =
            sourceManager.getTextFiles().stream()
                         .map(it -> it.getLanguageVersion().getLanguage())
                         .distinct()
                         .filter(it -> it instanceof CpdCapableLanguage)
                         .collect(Collectors.toMap(lang -> lang, lang -> ((CpdCapableLanguage) lang).createCpdLexer(configuration.getLanguageProperties(lang))));

        // Note: tokens contains all tokens of all analyzed files which is a huge data structure.
        // The tokens are only needed for finding the matches and can be garbage collected afterwards.
        // The report only needs the matches. Especially, the tokens are only referenced here and in
        // matchAlgorithm. When this method finishes, tokens should be eligible for garbage collection
        // making it possible to free up memory for render the report if needed.
        TokenFileSet tokens = new TokenFileSet(sourceManager, configuration.getThreads());
        for (CpdLexer tokenizer : tokenizers.values()) {
            tokens.preallocImages(tokenizer.commonImages());
        }
        sourceManager.size();
        tokens.setState(TokenFileSet.CpdState.BUILDING);

        Map<FileId, Integer> numberOfTokensPerFile = new HashMap<>();
        List<Report.ProcessingError> processingErrors = processAllFiles(
            configuration.getThreads(),
            sourceManager,
            (textFile, index) -> {
                try (TextDocument textDocument = sourceManager.load(textFile)) {
                    CpdLexer lexer = tokenizers.get(textFile.getLanguageVersion().getLanguage());
                    int newTokens = doTokenize(textDocument, lexer, tokens, index);
                    synchronized (this) {
                        numberOfTokensPerFile.put(textDocument.getFileId(), newTokens);
                        listener.addedFile(1);
                    }
                    return null;
                } catch (IOException | FileAnalysisException e) {
                    if (e instanceof FileAnalysisException) { // NOPMD
                        ((FileAnalysisException) e).setFileId(textFile.getFileId());
                    }
                    Level level = configuration.isFailOnError() ? Level.ERROR : Level.WARN;
                    reporter.logEx(level, "Skipping file", new Object[0], e);
                    return new Report.ProcessingError(e, textFile.getFileId());
                }
            });

        if (!processingErrors.isEmpty() && !configuration.isSkipLexicalErrors()) {
            reporter.error("Errors were detected while lexing source, exiting because --skip-lexical-errors is unset.");
            return null;
        }

        LOGGER.debug("Done lexing. CPD token count stats {}", tokens.getStats());
        LOGGER.debug("Running match algorithm on {} files...", sourceManager.size());
        List<Match> matches = findMatches(sourceManager, listener, tokens, configuration.getMinimumTileSize());
        LOGGER.debug("Finished: {} duplicates found", matches.size());

        return new CPDReport(sourceManager, matches, numberOfTokensPerFile, processingErrors);
    }

    /**
     * Execute a callback on all the files in the source manager.
     * Errors are returned by the callback, not thrown.
     */
    private static List<ProcessingError> processAllFiles(
        int threads, SourceManager sourceManager, ProcessFileFunc processFile)
        throws InterruptedException, ExecutionException {

        List<TextFile> textFiles = sourceManager.getTextFiles();
        IntStream indexStream = IntStream.range(0, textFiles.size());

        if (threads == 0) {
            return processWithStream(indexStream, textFiles, processFile);
        }

        // To make parallel streams use a custom ForkJoinPool, we need
        // to execute it in its "context" using submit.

        ForkJoinPool forkJoinPool = new ForkJoinPool(threads);
        try {
            return forkJoinPool
                .submit(() -> processWithStream(indexStream.parallel(), textFiles, processFile))
                .get();
        } finally {
            forkJoinPool.shutdown();
        }
    }

    /** Process all files with the given callback. Errors are collected into the result list. */
    private static List<ProcessingError> processWithStream(
        IntStream indexStream, List<TextFile> files, ProcessFileFunc processFile) {

        return indexStream.mapToObj(index -> processFile.process(files.get(index), index))
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    @FunctionalInterface
    private interface ProcessFileFunc {
        @Nullable
        ProcessingError process(TextFile file, int id);
    }

    @Override
    public void close() throws IOException {
        // nothing for now
    }

}
