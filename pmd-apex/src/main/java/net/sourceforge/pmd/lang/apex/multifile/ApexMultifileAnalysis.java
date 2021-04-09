/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.apex.ast.ApexParser;

import com.nawforce.common.api.FileIssueOptions;
import com.nawforce.common.api.Org;
import com.nawforce.common.api.ServerOps;
import com.nawforce.common.diagnostics.Issue;

/**
 * Stores multi-file analysis data. The 'Org' here is the primary ApexLink structure for maintaining information
 * about the Salesforce metadata. We load 'Packages' into it to perform analysis. Once constructed you
 * can get 'Issue' information from it on what was found. The 'Org' holds mutable state for IDE use that can get quite
 * large (a few hundred MB on very large projects). An alternative way to use this would be to cache the
 * issues after packages are loaded and throw away the 'Org'. That would be a better model if all you wanted was the
 * issues but more complex rules will need the ability to traverse the internal graph of the 'Org'.
 *
 * @author Kevin Jones
 */
@Experimental
public final class ApexMultifileAnalysis {

    // test only
    static final Logger LOG = Logger.getLogger(ApexMultifileAnalysis.class.getName());

    /**
     * Instances of the apexlink index and data structures ({@link Org})
     * are stored statically for now. TODO make that language-wide (#2518).
     */
    private static final Map<String, ApexMultifileAnalysis> INSTANCE_MAP = new ConcurrentHashMap<>();

    // An arbitrary large number of errors to report
    private static final Integer MAX_ERRORS_PER_FILE = 100;

    // Create a new org for each analysis
    // Null if failed.
    private final @Nullable Org org;
    private final FileIssueOptions options = makeOptions();

    private static final ApexMultifileAnalysis FAILED_INSTANCE = new ApexMultifileAnalysis();

    /** Ctor for the failed instance. */
    private ApexMultifileAnalysis() {
        org = null;
    }

    private ApexMultifileAnalysis(String multiFileAnalysisDirectory) {
        LOG.fine("MultiFile Analysis created for " + multiFileAnalysisDirectory);
        org = Org.newOrg();
        if (multiFileAnalysisDirectory != null && !multiFileAnalysisDirectory.isEmpty()) {
            // Load the package into the org, this can take some time!
            org.newSFDXPackage(multiFileAnalysisDirectory); // this may fail if the config is wrong
            org.flush();
        }
    }

    private static FileIssueOptions makeOptions() {
        FileIssueOptions options = new FileIssueOptions();
        // Default issue options, zombies gets us unused methods & fields as well as deploy problems
        options.includeZombies_$eq(true);
        options.maxErrorsPerFile_$eq(MAX_ERRORS_PER_FILE);
        return options;
    }

    /**
     * Returns true if this is analysis index is in a failed state.
     * This object is then useless. The failed instance is returned
     * from {@link #getAnalysisInstance(String)} if loading the org
     * failed, maybe because of malformed configuration.
     */
    public boolean isFailed() {
        return org == null;
    }

    public List<Issue> getFileIssues(String filename) {
        // Extract issues for a specific metadata file from the org
        return org == null ? Collections.emptyList()
                           : Collections.unmodifiableList(Arrays.asList(org.getFileIssues(filename, options)));
    }

    /**
     * Returns the analysis instance. Returns a {@linkplain #isFailed() failed instance}
     * if this fails.
     *
     * @param multiFileAnalysisDirectory Root directory of the configuration (see {@link ApexParser#MULTIFILE_DIRECTORY}).
     */
    public static @NonNull ApexMultifileAnalysis getAnalysisInstance(String multiFileAnalysisDirectory) {
        if (INSTANCE_MAP.isEmpty()) {
            // Default some library wide settings
            ServerOps.setAutoFlush(false);
            ServerOps.setLogger(new AnalysisLogger());
            ServerOps.setDebugLogging(new String[] { "ALL" });
        }

        return INSTANCE_MAP.computeIfAbsent(
            multiFileAnalysisDirectory,
            dir -> {
                try {
                    return new ApexMultifileAnalysis(dir);
                } catch (Exception e) {
                    LOG.severe("Exception while initializing Apexlink (" + e.getMessage() + ")");
                    LOG.severe(ExceptionUtils.getStackTrace(e));
                    LOG.severe("PMD will not attempt to initialize Apexlink further, this can cause rules like AvoidUnusedMethod to be dysfunctional");
                    return FAILED_INSTANCE;
                }
            });
    }

    /*
     * Very simple logger to aid debugging, relays ApexLink logging into PMD
     */
    private static final class AnalysisLogger implements com.nawforce.common.api.Logger {

        @Override
        public void error(String message) {
            LOG.fine(message);
        }

        @Override
        public void info(String message) {
            LOG.info(message);
        }

        @Override
        public void debug(String message) {
            LOG.fine(message);
        }
    }
}
