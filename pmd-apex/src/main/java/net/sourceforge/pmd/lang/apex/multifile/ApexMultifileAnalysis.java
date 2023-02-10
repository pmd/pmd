/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;

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
    static final Logger LOG = LoggerFactory.getLogger(ApexMultifileAnalysis.class);

    // An arbitrary large number of errors to report
    private static final int MAX_ERRORS_PER_FILE = 100;

    // Create a new org for each analysis
    // Null if failed.
    private final @Nullable Org org;
    private final FileIssueOptions options = makeOptions();


    static {
        // Default some library wide settings
        ServerOps.setAutoFlush(false);
        ServerOps.setLogger(new AnalysisLogger());
        ServerOps.setDebugLogging(new String[] { "ALL" });
    }


    @InternalApi
    public ApexMultifileAnalysis(ApexLanguageProperties properties) {
        String rootDir = properties.getProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY);
        LOG.debug("MultiFile Analysis created for {}", rootDir);

        Org org;
        try {
            org = Org.newOrg();
            if (rootDir != null && !rootDir.isEmpty()) {
                // Load the package into the org, this can take some time!
                org.newSFDXPackage(rootDir); // this may fail if the config is wrong
                org.flush();

                // FIXME: Syntax & Semantic errors found during Org loading are not currently being reported. These
                // should be routed to the new SemanticErrorReporter but that is not available for use just yet.
            }
        } catch (Exception e) {
            LOG.error("Exception while initializing Apexlink ({})", e.getMessage(), e);
            LOG.error("PMD will not attempt to initialize Apexlink further, this can cause rules like UnusedMethod to be dysfunctional");
            org = null;
        }
        this.org = org;
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
     * from {@link ApexLanguageProcessor#getMultiFileState()} if
     * loading the org failed, maybe because of malformed configuration.
     */
    public boolean isFailed() {
        return org == null;
    }

    public List<Issue> getFileIssues(String filename) {
        // Extract issues for a specific metadata file from the org
        return org == null ? Collections.emptyList()
                           : Collections.unmodifiableList(Arrays.asList(org.getFileIssues(filename, options)));
    }

    /*
     * Very simple logger to aid debugging, relays ApexLink logging into PMD
     */
    private static final class AnalysisLogger implements com.nawforce.common.api.Logger {

        @Override
        public void error(String message) {
            LOG.error(message);
        }

        @Override
        public void info(String message) {
            LOG.info(message);
        }

        @Override
        public void debug(String message) {
            LOG.debug(message);
        }
    }
}
