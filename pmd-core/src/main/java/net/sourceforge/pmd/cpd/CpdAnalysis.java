/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.log.MessageReporter;

public final class CpdAnalysis implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpdAnalysis.class);
    private final CPDConfiguration configuration;
    private final FileCollector files;
    private final MessageReporter reporter;
    private final @Nullable CPDReportRenderer renderer;
    private @NonNull CPDListener listener = new CPDNullListener();


    private CpdAnalysis(CPDConfiguration config) {
        this.configuration = config;
        this.reporter = config.getReporter();
        this.files = FileCollector.newCollector(
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

    private void setLanguageProperties(Language language, CPDConfiguration configuration) {
        LanguagePropertyBundle props = configuration.getLanguageProperties(language);

        setPropertyIfMissing(Tokenizer.CPD_ANONYMIZE_LITERALS, props, configuration.isIgnoreLiterals());
        setPropertyIfMissing(Tokenizer.CPD_ANONYMIZE_IDENTIFIERS, props, configuration.isIgnoreIdentifiers());
        setPropertyIfMissing(Tokenizer.CPD_IGNORE_METADATA, props, configuration.isIgnoreAnnotations());
        setPropertyIfMissing(Tokenizer.CPD_IGNORE_IMPORTS, props, configuration.isIgnoreUsings());
        setPropertyIfMissing(Tokenizer.CPD_IGNORE_LITERAL_SEQUENCES, props, configuration.isIgnoreLiteralSequences());
        if (!configuration.isNoSkipBlocks()) {
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

    private int doTokenize(TextDocument document, Tokenizer tokenizer, Tokens tokens) throws IOException, TokenMgrError {
        LOGGER.trace("Tokenizing {}", document.getFileId().getAbsolutePath());
        int lastTokenSize = tokens.size();
        Tokenizer.tokenize(tokenizer, document, tokens);
        return tokens.size() - lastTokenSize - 1; /* EOF */
    }

    public void performAnalysis() {
        performAnalysis(r -> { });
    }

    @SuppressWarnings("PMD.CloseResource")
    public void performAnalysis(Consumer<CPDReport> consumer) {

        try (SourceManager sourceManager = new SourceManager(files.getCollectedFiles())) {
            Map<Language, Tokenizer> tokenizers =
                sourceManager.getTextFiles().stream()
                             .map(it -> it.getLanguageVersion().getLanguage())
                             .distinct()
                             .filter(it -> it instanceof CpdCapableLanguage)
                             .collect(Collectors.toMap(lang -> lang, lang -> ((CpdCapableLanguage) lang).createCpdTokenizer(configuration.getLanguageProperties(lang))));

            Map<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

            boolean hasErrors = false;
            Tokens tokens = new Tokens();
            for (TextFile textFile : sourceManager.getTextFiles()) {
                TextDocument textDocument = sourceManager.get(textFile);
                Tokens.State savedState = tokens.savePoint();
                try {
                    int newTokens = doTokenize(textDocument, tokenizers.get(textFile.getLanguageVersion().getLanguage()), tokens);
                    numberOfTokensPerFile.put(textDocument.getFileId(), newTokens);
                    listener.addedFile(1);
                } catch (TokenMgrError | IOException e) {
                    if (e instanceof TokenMgrError) { // NOPMD
                        ((TokenMgrError) e).setFileId(textFile.getFileId());
                    }
                    String message = configuration.isSkipLexicalErrors() ? "Skipping file" : "Error while tokenizing";
                    reporter.errorEx(message, e);
                    hasErrors = true;
                    savedState.restore(tokens);
                }
            }
            if (hasErrors && !configuration.isSkipLexicalErrors()) {
                // will be caught by CPD command
                throw new IllegalStateException("Errors were detected while lexing source, exiting because --skip-lexical-errors is unset.");
            }

            LOGGER.debug("Running match algorithm on {} files...", sourceManager.size());
            MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, configuration.getMinimumTileSize());
            List<Match> matches = matchAlgorithm.findMatches(listener, sourceManager);
            tokens = null; // NOPMD null it out before rendering
            LOGGER.debug("Finished: {} duplicates found", matches.size());

            CPDReport cpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile);

            if (renderer != null) {
                renderer.render(cpdReport, IOUtil.createWriter(Charset.defaultCharset(), null));
            }

            consumer.accept(cpdReport);
        } catch (Exception e) {
            e.printStackTrace();
            reporter.errorEx("Exception while running CPD", e);
        }
        // source manager is closed and closes all text files now.
    }


    @Override
    public void close() throws IOException {
        // nothing for now
    }

}
