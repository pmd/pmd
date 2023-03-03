/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.ReportStats;
import net.sourceforge.pmd.reporting.ReportStatsListener;

public class PMDTaskImpl {

    private Path classpath;
    private Path auxClasspath;
    private final List<Formatter> formatters = new ArrayList<>();
    private final List<FileSet> filesets = new ArrayList<>();
    private final PMDConfiguration configuration = new PMDConfiguration();
    private boolean failOnError;
    private boolean failOnRuleViolation;
    private int maxRuleViolations = 0;
    private String failuresPropertyName;
    private Project project;

    public PMDTaskImpl(PMDTask task) {
        configuration.addRelativizeRoots(task.getRelativizeRoots());
        if (task.getSuppressMarker() != null) {
            configuration.setSuppressMarker(task.getSuppressMarker());
        }
        this.failOnError = task.isFailOnError();
        this.failOnRuleViolation = task.isFailOnRuleViolation();
        this.maxRuleViolations = task.getMaxRuleViolations();
        if (this.maxRuleViolations > 0) {
            this.failOnRuleViolation = true;
        }
        if (task.getRulesetFiles() != null) {
            configuration.setRuleSets(Arrays.asList(task.getRulesetFiles().split(",")));
        }

        configuration.setRuleSetFactoryCompatibilityEnabled(!task.isNoRuleSetCompatibility());
        if (task.getEncoding() != null) {
            configuration.setSourceEncoding(task.getEncoding());
        }
        configuration.setThreads(task.getThreads());
        this.failuresPropertyName = task.getFailuresPropertyName();
        configuration.setMinimumPriority(RulePriority.valueOf(task.getMinimumPriority()));
        configuration.setAnalysisCacheLocation(task.getCacheLocation());
        configuration.setIgnoreIncrementalAnalysis(task.isNoCache());

        SourceLanguage version = task.getSourceLanguage();
        if (version != null) {
            Language lang = LanguageRegistry.PMD.getLanguageById(version.getName());
            LanguageVersion languageVersion = lang == null ? null : lang.getVersion(version.getVersion());
            if (languageVersion == null) {
                throw new BuildException("The following language is not supported:" + version + '.');
            }
            configuration.setDefaultLanguageVersion(languageVersion);
        }

        classpath = task.getClasspath();
        auxClasspath = task.getAuxClasspath();

        filesets.addAll(task.getFilesets());
        formatters.addAll(task.getFormatters());

        project = task.getProject();
    }

    private void doTask() {
        setupClassLoader();

        if (configuration.getSuppressMarker() != null) {
            project.log("Setting suppress marker to be " + configuration.getSuppressMarker(), Project.MSG_VERBOSE);
        }

        List<String> ruleSetPaths = expandRuleSetPaths(configuration.getRuleSetPaths());
        // don't let PmdAnalysis.create create rulesets itself.
        configuration.setRuleSets(Collections.emptyList());

        ReportStats stats;
        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            RuleSetLoader rulesetLoader =
                pmd.newRuleSetLoader().loadResourcesWith(setupResourceLoader());
            pmd.addRuleSets(rulesetLoader.loadRuleSetsWithoutException(ruleSetPaths));

            for (FileSet fileset : filesets) {
                DirectoryScanner ds = fileset.getDirectoryScanner(project);
                for (String srcFile : ds.getIncludedFiles()) {
                    pmd.files().addFile(ds.getBasedir().toPath().resolve(srcFile));
                }
            }

            @SuppressWarnings("PMD.CloseResource")
            ReportStatsListener reportStatsListener = new ReportStatsListener();
            pmd.addListener(getListener(reportStatsListener));

            pmd.performAnalysis();
            stats = reportStatsListener.getResult();
            if (failOnError && pmd.getReporter().numErrors() > 0) {
                throw new BuildException("Some errors occurred while running PMD");
            }
        }

        int problemCount = stats.getNumViolations();
        project.log(problemCount + " problems found", Project.MSG_VERBOSE);

        if (failuresPropertyName != null && problemCount > 0) {
            project.setProperty(failuresPropertyName, String.valueOf(problemCount));
            project.log("Setting property " + failuresPropertyName + " to " + problemCount, Project.MSG_VERBOSE);
        }

        if (failOnRuleViolation && problemCount > maxRuleViolations) {
            throw new BuildException("Stopping build since PMD found " + problemCount + " rule violations in the code");
        }
    }

    private List<String> expandRuleSetPaths(List<String> ruleSetPaths) {
        List<String> paths = new ArrayList<>(ruleSetPaths);
        for (int i = 0; i < paths.size(); i++) {
            paths.set(i, project.replaceProperties(paths.get(i)));
        }
        return paths;
    }

    private @NonNull GlobalAnalysisListener getListener(ReportStatsListener reportSizeListener) {
        List<GlobalAnalysisListener> renderers = new ArrayList<>(formatters.size() + 1);
        try {
            renderers.add(makeLogListener());
            renderers.add(reportSizeListener);
            for (Formatter formatter : formatters) {
                project.log("Sending a report to " + formatter, Project.MSG_VERBOSE);
                renderers.add(formatter.newListener(project));
            }
            return GlobalAnalysisListener.tee(renderers);
        } catch (Exception e) {
            // close those opened so far
            Exception e2 = IOUtil.closeAll(renderers);
            if (e2 != null) {
                e.addSuppressed(e2);
            }
            throw new BuildException("Exception while initializing renderers", e);
        }
    }

    private GlobalAnalysisListener makeLogListener() {
        return new GlobalAnalysisListener() {

            @Override
            public FileAnalysisListener startFileAnalysis(TextFile dataSource) {
                String name = dataSource.getDisplayName();
                project.log("Processing file " + name, Project.MSG_VERBOSE);
                return FileAnalysisListener.noop();
            }

            @Override
            public void close() {
                // nothing to do
            }
        };
    }

    private ClassLoader setupResourceLoader() {
        if (classpath == null) {
            classpath = new Path(project);
        }

        /*
         * 'basedir' is added to the path to make sure that relative paths such
         * as "<ruleset>resources/custom_ruleset.xml</ruleset>" still work when
         * ant is invoked from a different directory using "-f"
         */
        classpath.add(new Path(null, project.getBaseDir().toString()));

        project.log("Using the AntClassLoader: " + classpath, Project.MSG_VERBOSE);
        // must be true, otherwise you'll get ClassCastExceptions as classes
        // are loaded twice
        // and exist in multiple class loaders
        final boolean parentFirst = true;
        return new AntClassLoader(Thread.currentThread().getContextClassLoader(),
                                  project, classpath, parentFirst);
    }

    private void setupClassLoader() {
        try {
            if (auxClasspath != null) {
                project.log("Using auxclasspath: " + auxClasspath, Project.MSG_VERBOSE);
                configuration.prependAuxClasspath(auxClasspath.toString());
            }
        } catch (IllegalArgumentException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }

    public void execute() throws BuildException {
        Level level = Slf4jSimpleConfigurationForAnt.reconfigureLoggingForAnt(project);
        Slf4jSimpleConfiguration.installJulBridge();
        // need to reload the logger with the new configuration
        Logger log = LoggerFactory.getLogger(PMDTaskImpl.class);
        log.info("Logging is at {}", level);
        try {
            doTask();
        } finally {
            // only close the classloader, if it is ours. Otherwise we end up with class not found
            // exceptions
            if (configuration.getClassLoader() instanceof ClasspathClassLoader) {
                IOUtil.tryCloseClassLoader(configuration.getClassLoader());
            }
        }
    }

}
