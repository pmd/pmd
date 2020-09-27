/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.nawforce.common.api.IssueOptions;
import com.nawforce.common.api.Org;
import com.nawforce.common.api.ServerOps;
import com.nawforce.common.diagnostics.IssueLog;
import com.nawforce.common.org.OrgImpl;

/**
 * Stores multi-file analysis data.
 *
 * @author Kevin Jones
 */
@SuppressWarnings("PMD")
public final class ApexMultifileAnalysis {
    private static final Logger LOG = Logger.getLogger(ApexMultifileAnalysis.class.getName());

    private static Map<String, ApexMultifileAnalysis> instanceMap = new HashMap<>();

    private static final Integer MAGIX = 10;

    private static IssueLog issues;

    private ApexMultifileAnalysis(String multiFileAnalysisDirectory) {
        LOG.fine("MultiFile Analysis created for " + multiFileAnalysisDirectory);

        if (multiFileAnalysisDirectory != null && !multiFileAnalysisDirectory.isEmpty()) {
            Org org = Org.newOrg(true);
            org.newSFDXPackage(multiFileAnalysisDirectory);
            org.flush();
            IssueOptions options = new IssueOptions();
            options.includeZombies_$eq(true);
            issues = org.getIssues(options);
            LOG.fine("Issues\n" + issues.asString(true, MAGIX, ""));
            LOG.fine("Root package created");

        }
    }

    public IssueLog getIssues() {
        return issues;
    }

    public static ApexMultifileAnalysis getAnalysisInstance(String multiFileAnalysisDirectory) {
        if (instanceMap.isEmpty()) {
            ServerOps.setAutoFlush(false);
            ServerOps.setLogger(new AnalysisLogger());
            ServerOps.setDebugLogging(new String[] {"ALL"});
        }

        ApexMultifileAnalysis instance = instanceMap.get(multiFileAnalysisDirectory);
        if (null == instance) {
            instance = create(multiFileAnalysisDirectory);
            instanceMap.put(multiFileAnalysisDirectory, instance);
        }
        return instance;
    }

    private static ApexMultifileAnalysis create(String multiFileAnalysisDirectory) {
        return new ApexMultifileAnalysis(multiFileAnalysisDirectory);
    }

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
