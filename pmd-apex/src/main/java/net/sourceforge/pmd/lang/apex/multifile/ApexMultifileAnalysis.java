/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;

import com.nawforce.apexlink.api.Org;
import com.nawforce.pkgforce.diagnostics.LoggerOps;
import io.github.apexdevtools.api.Issue;

/**
 * Stores multi-file analysis data. The 'Org' here is the primary ApexLink structure for maintaining information
 * about the Salesforce metadata. We load 'Packages' into it to perform analysis. Once constructed you
 * can get 'Issue' information from it on what was found. The 'Org' holds mutable state for IDE use that can get quite
 * large (a few hundred MB on very large projects). An alternative way to use this would be to cache the
 * issues after packages are loaded and throw away the 'Org'. That would be a better model if all you wanted was the
 * issues but more complex rules will need the ability to traverse the internal graph of the 'Org'.
 *
 * <p>Note: This is used by {@link net.sourceforge.pmd.lang.apex.rule.design.UnusedMethodRule}.
 *
 * @author Kevin Jones
 */
public final class ApexMultifileAnalysis {

    // test only
    static final Logger LOG = LoggerFactory.getLogger(ApexMultifileAnalysis.class);

    // Create a new org for each analysis
    // Null if failed.
    private final @Nullable Org org;

    static {
        // Setup logging
        LoggerOps.setLogger(new AnalysisLogger());
        // TODO: Provide means to control logging
        LoggerOps.setLoggingLevel(LoggerOps.NO_LOGGING());
    }


    ApexMultifileAnalysis(ApexLanguageProperties properties) {
        Optional<String> rootDir = properties.getProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY);
        LOG.debug("MultiFile Analysis created for {}", rootDir);

        Org org = null;
        try {
            // Load the package into the org, this can take some time!
            if (rootDir.isPresent() && !rootDir.get().isEmpty()) {
                Path projectPath = Paths.get(rootDir.get());
                Path sfdxProjectJson = projectPath.resolve("sfdx-project.json");

                // Limit analysis to SFDX Projects
                // MDAPI analysis is currently supported but is expected to be deprecated soon
                if (Files.isDirectory(projectPath) && Files.isRegularFile(sfdxProjectJson)) {
                    org = Org.newOrg(rootDir.get());

                    // FIXME: Syntax & Semantic errors found during Org loading are not currently being reported. These
                    // should be routed to the new SemanticErrorReporter but that is not available for use just yet.
                    // Specifically we should check sfdx-project.json was ok as errors will disable further analysis
                    Issue[] projectErrors =
                            Arrays.stream(org.issues().issuesForFile(sfdxProjectJson.toString()))
                                    .filter(Issue::isError).toArray(Issue[]::new);
                    Arrays.stream(projectErrors).forEach(issue -> LOG.info(issue.toString()));
                    if (projectErrors.length != 0) {
                        org = null;
                    }
                } else {
                    LOG.info("Missing project file at {}", sfdxProjectJson);
                }
            }
        } catch (Exception | ExceptionInInitializerError | NoClassDefFoundError e) {
            // Note: Org.newOrg() will try to find the base Apex Types through the current classloader
            // in package "com.nawforce.runforce". This requires, that directory listings can be retrievied
            // on the URL that the classloader returns from getResource("/com/nawforce/runforce"):
            // https://github.com/nawforce/apex-link/blob/7688adcb7a2d7f8aa28d0618ffb2a3aa81151858/apexlink/src/main/scala/com/nawforce/apexlink/types/platform/PlatformTypeDeclaration.scala#L260-L273
            // However, when running as an Eclipse plugin, we have a special bundle classloader, that returns
            // URIs in the form "bundleresource://...". For the schema "bundleresource", no FileSystemProvider can be
            // found, so we get a java.nio.file.ProviderNotFoundException. Since all this happens during initialization of the class
            // com.nawforce.apexlink.types.platform.PlatformTypeDeclaration we get a ExceptionInInitializerError
            // and later NoClassDefFoundErrors, because PlatformTypeDeclaration couldn't be loaded.
            LOG.error("Exception while initializing Apexlink ({})", e.getMessage(), e);
            LOG.error("PMD will not attempt to initialize Apexlink further, this can cause rules like UnusedMethod to be dysfunctional");
        }
        this.org = org;
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
                           : Collections.unmodifiableList(Arrays.asList(org.issues().issuesForFile(filename)));
    }

    /*
     * Very simple logger to aid debugging, relays ApexLink logging into PMD
     */
    private static final class AnalysisLogger implements com.nawforce.pkgforce.diagnostics.Logger {

        @Override
        public void info(String message) {
            LOG.info(message);
        }

        @Override
        public void debug(String message) {
            LOG.debug(message);
        }

        @Override
        public void trace(String message) {
            LOG.trace(message);
        }
    }
}
