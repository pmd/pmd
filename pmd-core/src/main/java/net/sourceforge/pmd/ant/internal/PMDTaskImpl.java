/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.AntLogHandler;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

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
        configuration.setReportShortNames(task.isShortFilenames());
        configuration.addRelativizeRoots(task.getRelativizeRoots());
        configuration.setSuppressMarker(task.getSuppressMarker());
        this.failOnError = task.isFailOnError();
        this.failOnRuleViolation = task.isFailOnRuleViolation();
        this.maxRuleViolations = task.getMaxRuleViolations();
        if (this.maxRuleViolations > 0) {
            this.failOnRuleViolation = true;
        }
        configuration.setRuleSets(task.getRulesetFiles());
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
            LanguageVersion languageVersion = LanguageRegistry
                    .findLanguageVersionByTerseName(version.getName() + ' ' + version.getVersion());
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


        @SuppressWarnings("PMD.CloseResource") final List<String> reportShortNamesPaths = new ArrayList<>();

        List<String> ruleSetPaths = expandRuleSetPaths();
        // don't let PmdAnalysis.create create rulesets itself.
        configuration.setRuleSets(Collections.<String>emptyList());

        Report report;
        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            RuleSetLoader rulesetLoader =
                pmd.newRuleSetLoader().loadResourcesWith(setupResourceLoader());
            pmd.addRuleSets(rulesetLoader.loadRuleSetsWithoutException(ruleSetPaths));

            for (FileSet fileset : filesets) {
                DirectoryScanner ds = fileset.getDirectoryScanner(project);
                for (String srcFile : ds.getIncludedFiles()) {
                    pmd.files().addFile(ds.getBasedir().toPath().resolve(srcFile));
                }

                final String commonInputPath = ds.getBasedir().getPath();
                if (configuration.isReportShortNames()) {
                    reportShortNamesPaths.add(commonInputPath);
                }
            }

            for (Formatter formatter : formatters) {
                project.log("Sending a report to " + formatter, Project.MSG_VERBOSE);
                pmd.addRenderer(formatter.toRenderer(project, reportShortNamesPaths));
            }

            pmd.addRenderer(getLogRenderer());

            report = pmd.performAnalysisAndCollectReport();
            if (failOnError && pmd.getReporter().numErrors() > 0) {
                throw new BuildException("Some errors occurred while running PMD");
            }

        }

        int problemCount = report.getViolations().size();
        project.log(problemCount + " problems found", Project.MSG_VERBOSE);

        if (failuresPropertyName != null && problemCount > 0) {
            project.setProperty(failuresPropertyName, String.valueOf(problemCount));
            project.log("Setting property " + failuresPropertyName + " to " + problemCount, Project.MSG_VERBOSE);
        }

        if (failOnRuleViolation && problemCount > maxRuleViolations) {
            throw new BuildException("Stopping build since PMD found " + problemCount + " rule violations in the code");
        }
    }

    private List<String> expandRuleSetPaths() {
        List<String> paths = new ArrayList<>(configuration.getRuleSetPaths());
        for (int i = 0; i < paths.size(); i++) {
            paths.set(i, project.replaceProperties(paths.get(i)));
        }
        return paths;
    }

    private AbstractRenderer getLogRenderer() {
        return new AbstractRenderer("log", "Logging renderer") {
            @Override
            public void start() {
                // Nothing to do
            }

            @Override
            public void startFileAnalysis(DataSource dataSource) {
                project.log("Processing file " + dataSource.getNiceFileName(false, null),
                            Project.MSG_VERBOSE);
            }

            @Override
            public void renderFileReport(Report r) {
                // Nothing to do
            }

            @Override
            public void end() {
                // Nothing to do
            }

            @Override
            public void flush() {
                // Nothing to do
            }

            @Override
            public String defaultFileExtension() {
                return null;
            } // not relevant
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
        final AntLogHandler antLogHandler = new AntLogHandler(project);
        final ScopedLogHandlersManager logManager = new ScopedLogHandlersManager(antLogHandler.getAntLogLevel(), antLogHandler);
        try {
            doTask();
        } catch (BuildException e) {
            throw e;
        } catch (Exception other) {
            throw new BuildException(other);
        } finally {
            logManager.close();
            // only close the classloader, if it is ours. Otherwise we end up with class not found
            // exceptions
            if (configuration.getClassLoader() instanceof ClasspathClassLoader) {
                IOUtil.tryCloseClassLoader(configuration.getClassLoader());
            }
        }
    }

}
