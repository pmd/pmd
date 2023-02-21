/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.FileFinder;
import net.sourceforge.pmd.internal.util.FileUtil;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;

/**
 * @deprecated Use the module pmd-cli for CLI support.
 */
@Deprecated
public class CPD {
    // not final, in order to re-initialize logging
    private static Logger log = LoggerFactory.getLogger(CPD.class);

    private CPDConfiguration configuration;

    private Map<String, SourceCode> source = new TreeMap<>();
    private CPDListener listener = new CPDNullListener();
    private Tokens tokens = new Tokens();
    private MatchAlgorithm matchAlgorithm;
    private Set<String> current = new HashSet<>();
    private final Map<String, Integer> numberOfTokensPerFile = new HashMap<>();
    private int lastTokenSize = 0;

    public CPD(CPDConfiguration theConfiguration) {
        configuration = theConfiguration;
        // before we start any tokenizing (add(File...)), we need to reset the
        // static TokenEntry status
        TokenEntry.clearImages();

        // Add all sources
        extractAllSources();
    }

    private void extractAllSources() {
        // Add files
        if (null != configuration.getFiles() && !configuration.getFiles().isEmpty()) {
            addSourcesFilesToCPD(configuration.getFiles());
        }

        // Add Database URIS
        if (null != configuration.getURI() && !"".equals(configuration.getURI())) {
            addSourceURIToCPD(configuration.getURI());
        }

        if (null != configuration.getFileListPath() && !"".equals(configuration.getFileListPath())) {
            addFilesFromFilelist(configuration.getFileListPath());
        }
    }

    private void addSourcesFilesToCPD(List<File> files) {
        try {
            for (File file : files) {
                if (!file.exists()) {
                    throw new FileNotFoundException("Could not find directory/file '" + file + "'");
                } else if (file.isDirectory()) {
                    if (configuration.isNonRecursive()) {
                        addAllInDirectory(file);
                    } else {
                        addRecursively(file);
                    }
                } else {
                    add(file);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addFilesFromFilelist(String inputFilePath) {
        List<File> files = new ArrayList<>();
        try {
            Path file = FileUtil.toExistingPath(inputFilePath);
            for (Path fileToAdd : FileUtil.readFilelistEntries(file)) {
                if (!Files.exists(fileToAdd)) {
                    throw new RuntimeException("No such file " + fileToAdd);
                }
                files.add(fileToAdd.toFile());
            }
            addSourcesFilesToCPD(files);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void addSourceURIToCPD(String uri) {
        try {
            log.debug("Attempting DBURI={}", uri);
            DBURI dburi = new DBURI(uri);
            log.debug("Initialised DBURI={}", dburi);
            log.debug("Adding DBURI={} with DBType={}", dburi, dburi.getDbType());
            add(dburi);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("uri=" + uri, e);
        }
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    public void go() {
        log.debug("Running match algorithm on {} files...", source.size());
        matchAlgorithm = new MatchAlgorithm(source, tokens, configuration.getMinimumTileSize(), listener);
        matchAlgorithm.findMatches();
        log.debug("Finished: {} duplicates found", matchAlgorithm.getMatches().size());
    }

    /**
     * @deprecated Use {@link #toReport()}.
     */
    @Deprecated
    public Iterator<Match> getMatches() {
        return matchAlgorithm.matches();
    }

    public void addAllInDirectory(File dir) throws IOException {
        addDirectory(dir, false);
    }

    public void addRecursively(File dir) throws IOException {
        addDirectory(dir, true);
    }

    public void add(List<File> files) throws IOException {
        for (File f : files) {
            add(f);
        }
    }

    private void addDirectory(File dir, boolean recurse) throws IOException {
        if (!dir.exists()) {
            throw new FileNotFoundException("Couldn't find directory " + dir);
        }
        log.debug("Searching directory " + dir + " for files");
        FileFinder finder = new FileFinder();
        // TODO - could use SourceFileSelector here
        add(finder.findFilesFrom(dir, configuration.filenameFilter(), recurse));
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

    public void add(DBURI dburi) {

        try {
            DBMSMetadata dbmsmetadata = new DBMSMetadata(dburi);

            List<SourceObject> sourceObjectList = dbmsmetadata.getSourceObjectList();
            log.debug("Located {} database source objects", sourceObjectList.size());

            for (SourceObject sourceObject : sourceObjectList) {
                // Add DBURI as a faux-file
                String falseFilePath = sourceObject.getPseudoFileName();
                log.trace("Adding database source object {}", falseFilePath);

                SourceCode sourceCode = configuration.sourceCodeFor(dbmsmetadata.getSourceCode(sourceObject),
                        falseFilePath);
                add(sourceCode);
            }
        } catch (Exception sqlException) {
            log.error("Problem with Input URI", sqlException);
            throw new RuntimeException("Problem with DBURI: " + dburi, sqlException);
        }
    }

    @Experimental
    public void add(SourceCode sourceCode) throws IOException {
        if (configuration.isSkipLexicalErrors()) {
            addAndSkipLexicalErrors(sourceCode);
        } else {
            addAndThrowLexicalError(sourceCode);
        }
    }

    private void addAndThrowLexicalError(SourceCode sourceCode) throws IOException {
        log.debug("Tokenizing {}", sourceCode.getFileName());
        configuration.tokenizer().tokenize(sourceCode, tokens);
        listener.addedFile(1, new File(sourceCode.getFileName()));
        source.put(sourceCode.getFileName(), sourceCode);
        numberOfTokensPerFile.put(sourceCode.getFileName(), tokens.size() - lastTokenSize - 1 /*EOF*/);
        lastTokenSize = tokens.size();
    }

    private void addAndSkipLexicalErrors(SourceCode sourceCode) throws IOException {
        final TokenEntry.State savedState = new TokenEntry.State();
        try {
            addAndThrowLexicalError(sourceCode);
        } catch (TokenMgrError e) {
            System.err.println("Skipping " + sourceCode.getFileName() + ". Reason: " + e.getMessage());
            savedState.restore(tokens);
        }
    }

    /**
     * List names/paths of each source to be processed.
     *
     * @return names of sources to be processed
     */
    public List<String> getSourcePaths() {
        return new ArrayList<>(source.keySet());
    }

    /**
     * Get each Source to be processed.
     *
     * @return all Sources to be processed
     */
    public List<SourceCode> getSources() {
        return new ArrayList<>(source.values());
    }

    /**
     * Entry to invoke CPD as command line tool. Note that this will
     * invoke {@link System#exit(int)}.
     *
     * @param args command line arguments
     *
     * @deprecated Use module pmd-cli -- to be removed before 7.0.0 is out.
     */
    @Deprecated
    public static void main(String[] args) {
        throw new UnsupportedOperationException("Use the pmd-cli module.");
    }

    public CPDReport toReport() {
        return new CPDReport(matchAlgorithm.getMatches(), numberOfTokensPerFile);
    }

}
