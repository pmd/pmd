/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.Experimental;

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
    private static final Logger LOG = Logger.getLogger(ApexMultifileAnalysis.class.getName());

    private static Map<String, ApexMultifileAnalysis> instanceMap = new HashMap<>();

    // An arbitrary large number of errors to report
    private static final Integer MAX_ERRORS_PER_FILE = 100;

    // Create a new org for each analysis
    private Org org = Org.newOrg(true);
    private FileIssueOptions options = new FileIssueOptions();

    private ApexMultifileAnalysis(String multiFileAnalysisDirectory) {
        LOG.fine("MultiFile Analysis created for " + multiFileAnalysisDirectory);
        if (multiFileAnalysisDirectory != null && !multiFileAnalysisDirectory.isEmpty()) {
            // Default issue options, zombies gets us unused methods & fields as well as deploy problems
            options.includeZombies_$eq(true);
            options.maxErrorsPerFile_$eq(MAX_ERRORS_PER_FILE);

            // Load the package into the org, this can take some time!
            org.newSFDXPackage(multiFileAnalysisDirectory);
            org.flush();
        }
    }

    public Issue[] getFileIssues(String filename) {
        // Extract issues for a specific metadata file from the org
        return org.getFileIssues(filename, options);
    }

    public static ApexMultifileAnalysis getAnalysisInstance(String multiFileAnalysisDirectory) {
        if (instanceMap.isEmpty()) {
            // Default some library wide settings
            ServerOps.setAutoFlush(false);
            ServerOps.setLogger(new AnalysisLogger());
            ServerOps.setDebugLogging(new String[] {"ALL"});
        }

        return instanceMap.computeIfAbsent(multiFileAnalysisDirectory, ApexMultifileAnalysis::create);
    }

    private static ApexMultifileAnalysis create(String multiFileAnalysisDirectory) {
        return new ApexMultifileAnalysis(multiFileAnalysisDirectory);
    }

    /*
     * Very simple logger to aid debugging, relays ApexLink logging into PMD
     */
    private static class AnalysisLogger implements com.nawforce.common.api.Logger {
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
