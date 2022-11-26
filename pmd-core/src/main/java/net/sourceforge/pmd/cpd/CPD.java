/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
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
import org.slf4j.event.Level;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;

/**
 * @deprecated {@link PmdCli} under the pmd-cli offers CLI support.
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
                    throw new FileNotFoundException("Couldn't find directory/file '" + file + "'");
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
     */
    public static void main(String[] args) {
        StatusCode statusCode = runCpd(args);
        CPDCommandLineInterface.setStatusCodeOrExit(statusCode.toInt());
    }

    /**
     * Parses the command line and executes CPD. Returns the status code
     * without exiting the VM.
     *
     * @param args command line arguments
     *
     * @return the status code
     */
    public static StatusCode runCpd(String... args) {
        CPDConfiguration arguments = new CPDConfiguration();
        CPD.StatusCode statusCode = CPDCommandLineInterface.parseArgs(arguments, args);
        if (statusCode != null) {
            return statusCode;
        }

        // only reconfigure logging, if debug flag was used on command line
        // otherwise just use whatever is in conf/simplelogger.properties which happens automatically
        if (arguments.isDebug()) {
            Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(Level.TRACE);
        }
        // always need to reload the logger with the new/changed configuration
        // unit tests might reset the logging configuration
        log = LoggerFactory.getLogger(CPD.class);

        // TODO CLI errors should also be reported through this
        // TODO this should not use the logger as backend, otherwise without
        //  slf4j implementation binding, errors are entirely ignored.
        // always install java.util.logging to slf4j bridge
        Slf4jSimpleConfiguration.installJulBridge();
        // logging, mostly for testing purposes
        Level defaultLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        log.info("Log level is at {}", defaultLogLevel);

        CPD cpd = new CPD(arguments);

        try {
            cpd.go();
            final CPDReportRenderer renderer = arguments.getCPDReportRenderer();
            if (renderer == null) {
                // legacy writer
                System.out.println(arguments.getRenderer().render(cpd.getMatches()));
            } else {
                final CPDReport report = cpd.toReport();
                renderer.render(report, IOUtil.createWriter(Charset.defaultCharset(), null));
            }
            if (cpd.getMatches().hasNext()) {
                if (arguments.isFailOnViolation()) {
                    statusCode = StatusCode.DUPLICATE_CODE_FOUND;
                } else {
                    statusCode = StatusCode.OK;
                }
            } else {
                statusCode = StatusCode.OK;
            }
        } catch (IOException | RuntimeException e) {
            log.debug(e.toString(), e);
            log.error(LogMessages.errorDetectedMessage(1, CPDCommandLineInterface.PROGRAM_NAME));
            statusCode = StatusCode.ERROR;
        }
        return statusCode;
    }

    public CPDReport toReport() {
        return new CPDReport(matchAlgorithm.getMatches(), numberOfTokensPerFile);
    }

    /**
     * @deprecated This class is to be removed in PMD 7 in favor of a unified PmdCli entry point.
     */
    @Deprecated
    public enum StatusCode {
        OK(0),
        ERROR(1),
        DUPLICATE_CODE_FOUND(4);

        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        /** Returns the exit code as used in CLI. */
        public int toInt() {
            return this.code;
        }
    }
}
