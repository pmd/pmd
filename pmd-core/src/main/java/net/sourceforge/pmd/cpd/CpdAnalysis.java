/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.internal.util.FileUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.log.MessageReporter;

public final class CpdAnalysis implements AutoCloseable {

    private static Logger log = LoggerFactory.getLogger(CpdAnalysis.class);
    private CPDConfiguration configuration;
    private FileCollector files;
    private MessageReporter reporter;
    private CPDListener listener;


    public CpdAnalysis(CPDConfiguration theConfiguration) throws IOException {
        configuration = theConfiguration;

        // Add all sources
        extractAllSources();
    }

    public FileCollector files() {
        return files;
    }

    private void extractAllSources() throws IOException {
        // Add files
        if (null != configuration.getFiles() && !configuration.getFiles().isEmpty()) {
            addSourcesFilesToCPD(configuration.getFiles());
        }

        // Add Database URIS
        if (null != configuration.getURI()) {
            FileCollectionUtil.collectDB(files(), configuration.getURI());
        }

        if (null != configuration.getFileListPath()) {
            FileCollectionUtil.collectFileList(files(), FileUtil.toExistingPath(configuration.getFileListPath()));
        }
    }

    private void addSourcesFilesToCPD(List<File> files) throws IOException {
        for (File file : files) {
            files().addFileOrDirectory(file.toPath());
        }
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    private int doTokenize(TextDocument document, Tokenizer tokenizer, Tokens tokens) throws IOException {
        log.trace("Tokenizing {}", document.getPathId());
        int lastTokenSize = tokens.size();
        try {
            tokenizer.tokenize(document, TokenFactory.forFile(document, tokens));
        } catch (IOException ioe) {
            reporter.errorEx("Error while lexing.", ioe);
        } catch (TokenMgrError e) {
            e.setFileName(document.getDisplayName());
            reporter.errorEx("Error while lexing.", e);
        } finally {
            tokens.addEof();
        }
        return tokens.size() - lastTokenSize - 1; /* EOF */
    }

    public void performAnalysis(Consumer<CPDReport> consumer) {

        try (SourceManager sourceManager = new SourceManager(files.getCollectedFiles())) {
            Map<Language, Tokenizer> tokenizers =
                sourceManager.getTextFiles().stream()
                             .map(it -> it.getLanguageVersion().getLanguage())
                             .collect(Collectors.toMap(lang -> lang, lang -> lang.createCpdTokenizer(configuration.getLanguageProperties(lang))));

            Map<String, Integer> numberOfTokensPerFile = new HashMap<>();

            Tokens tokens = new Tokens();
            for (TextFile textFile : sourceManager.getTextFiles()) {

                TextDocument textDocument = sourceManager.get(textFile);

                int newTokens = doTokenize(textDocument, tokenizers.get(textFile.getLanguageVersion().getLanguage()), tokens);
                numberOfTokensPerFile.put(textDocument.getPathId(), newTokens);
                listener.addedFile(1);
            }


            log.debug("Running match algorithm on {} files...", sourceManager.size());
            MatchAlgorithm matchAlgorithm = new MatchAlgorithm(sourceManager, tokens, configuration.getMinimumTileSize(), listener);
            matchAlgorithm.findMatches();
            log.debug("Finished: {} duplicates found", matchAlgorithm.getMatches().size());

            new CPDReport(matchAlgorithm.getMatches(), matchAlgorithm.to)

        } catch (Exception e) {
            reporter.errorEx("Exception while running CPD", e);
        }
    }


    @Override
    public void close() throws IOException {
        // nothing for now
    }
}
