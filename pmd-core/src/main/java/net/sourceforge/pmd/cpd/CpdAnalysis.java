/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.sourceforge.pmd.properties.PropertyDescriptor;
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
        Iterator<List<TokenFileSet.SmallTokenEntry>> markGroups = tokens.hashAll(minTileSize);

        MatchCollector matchCollector = new MatchCollector(sourceManager, tokens, minTileSize);
        cpdListener.phaseUpdate(CPDListener.MATCH);
        markGroups.forEachRemaining(matchCollector::collect);

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

    private int doTokenize(TextDocument document, CpdLexer cpdLexer, TokenFileSet tokens) throws IOException, LexException {
        LOGGER.trace("Tokenizing {}", document.getFileId().getAbsolutePath());
        TokenFile tokenFile = tokens.tokenize(cpdLexer, document);
        return tokenFile.size();
    }

    public void performAnalysis() {
        performAnalysis(r -> { });
    }

    @SuppressWarnings("PMD.CloseResource")
    public void performAnalysis(Consumer<CPDReport> consumer) {

        try (SourceManager sourceManager = new SourceManager(files.getCollectedFiles())) {
            Map<Language, CpdLexer> tokenizers =
                sourceManager.getTextFiles().stream()
                             .map(it -> it.getLanguageVersion().getLanguage())
                             .distinct()
                             .filter(it -> it instanceof CpdCapableLanguage)
                             .collect(Collectors.toMap(lang -> lang, lang -> ((CpdCapableLanguage) lang).createCpdLexer(configuration.getLanguageProperties(lang))));

            Map<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

            final List<Match> matches;
            {
                TokenFileSet tokens = new TokenFileSet(sourceManager);

                ForkJoinPool forkJoinPool = new ForkJoinPool(configuration.getThreads());
                try {
                    boolean hasErrors = forkJoinPool.submit(() -> sourceManager.getShieldedTextFiles().parallel().reduce(false, (hasErrorSoFar, textFile) -> {
                        try (TextDocument textDocument = sourceManager.load(textFile)) {
                            int newTokens = doTokenize(textDocument, tokenizers.get(textFile.getLanguageVersion().getLanguage()), tokens);
                            synchronized (this) {
                                numberOfTokensPerFile.put(textDocument.getFileId(), newTokens);
                                listener.addedFile(1);
                            }
                        } catch (IOException | FileAnalysisException e) {
                            if (e instanceof FileAnalysisException) { // NOPMD
                                ((FileAnalysisException) e).setFileId(textFile.getFileId());
                            }
                            String message = configuration.isSkipLexicalErrors() ? "Skipping file" : "Error while tokenizing";
                            reporter.errorEx(message, e);
                            hasErrorSoFar = true;
                        }
                        return hasErrorSoFar;
                    }, Boolean::logicalOr)).get();

                    if (hasErrors && !configuration.isSkipLexicalErrors()) {
                        // will be caught by CPD command
                        throw new IllegalStateException("Errors were detected while lexing source, exiting because --skip-lexical-errors is unset.");
                    }
                } finally {
                    forkJoinPool.shutdown();
                }

                LOGGER.debug("Running match algorithm on {} files...", sourceManager.size());
                matches = findMatches(sourceManager, listener, tokens, configuration.getMinimumTileSize());
            }
            LOGGER.debug("Finished: {} duplicates found", matches.size());

            CPDReport cpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile);

            if (renderer != null) {
                try (Writer writer = IOUtil.createWriter(Charset.defaultCharset(), null)) {
                    renderer.render(cpdReport, writer);
                }
            }

            consumer.accept(cpdReport);
        } catch (Exception e) {
            reporter.errorEx("Exception while running CPD", e);
        }
        // source manager is closed and closes all text files now.
    }


    @Override
    public void close() throws IOException {
        // nothing for now
    }

}
