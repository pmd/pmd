/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.internal.util.FileFinder;
import net.sourceforge.pmd.internal.util.FileUtil;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * @deprecated Use the module pmd-cli for CLI support.
 */
@Deprecated
public class CpdAnalysis {

    private CPDConfiguration configuration;
    private FileCollector files;
    private MessageReporter reporter;
    private CPDListener listener;


    public CpdAnalysis(CPDConfiguration theConfiguration) {
        configuration = theConfiguration;

        // Add all sources
        try {
            extractAllSources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void addAndThrowLexicalError(SourceCode sourceCode) throws IOException {
        log.debug("Tokenizing {}", sourceCode.getPathId());
        try (TextDocument doc = sourceCode.load()) {
            configuration.tokenizer().tokenize(doc, tokens);
        }
        listener.addedFile(1);
        source.put(sourceCode.getPathId(), sourceCode);
        numberOfTokensPerFile.put(sourceCode.getPathId(), tokens.size() - lastTokenSize - 1 /*EOF*/);
        lastTokenSize = tokens.size();
    }

    public CPDReport performAnalysis() {

        try (SourceManager sourceManager = new SourceManager(files.getCollectedFiles())) {
            Tokens tokens = new Tokens();


            log.debug("Running match algorithm on {} files...", sourceManager.size());
            MatchAlgorithm matchAlgorithm = new MatchAlgorithm(sourceManager, tokens, configuration.getMinimumTileSize(), listener);
            matchAlgorithm.findMatches();
            log.debug("Finished: {} duplicates found", matchAlgorithm.getMatches().size());



        } catch (Exception e) {
            reporter.errorEx("Exception while running CPD", e);
        }
    }

    public void add(File file) throws IOException {

        if (configuration.isSkipDuplicates()) {
            // TODO refactor this thing into a separate class
            String signature = file.getName() + '_' + file.length();
            if (current.contains(signature)) {
                System.err.println("Skipping " + file.getAbsolutePath()
                                       + " since it appears to be a duplicate file and --skip-duplicate-files is set");
                return;
            }
            current.add(signature);
        }

        if (!IOUtil.equalsNormalizedPaths(file.getAbsoluteFile().getCanonicalPath(), file.getAbsolutePath())) {
            System.err.println("Skipping " + file + " since it appears to be a symlink");
            return;
        }

        if (!file.exists()) {
            System.err.println("Skipping " + file + " since it doesn't exist (broken symlink?)");
            return;
        }

        SourceCode sourceCode = configuration.sourceCodeFor(file);
        add(sourceCode);
    }


}
