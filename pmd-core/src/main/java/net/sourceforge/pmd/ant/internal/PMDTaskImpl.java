/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant.internal;

import java.io.File;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoadException;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener.ViolationCounterListener;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.document.TextFile;
import net.sourceforge.pmd.util.document.TextFileBuilder;
import net.sourceforge.pmd.util.log.AntLogHandler;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

public class PMDTaskImpl {

    private Path classpath;
    private Path auxClasspath;
    private final List<Formatter> formatters = new ArrayList<>();
    private final List<FileSet> filesets = new ArrayList<>();
    private final PMDConfiguration configuration = new PMDConfiguration();
    private final String rulesetPaths;
    private boolean failOnRuleViolation;
    private int maxRuleViolations = 0;
    private String failuresPropertyName;
    private Project project;

    public PMDTaskImpl(PMDTask task) {
        configuration.setReportShortNames(task.isShortFilenames());
        if (task.getSuppressMarker() != null) {
            configuration.setSuppressMarker(task.getSuppressMarker());
        }
        this.failOnRuleViolation = task.isFailOnRuleViolation();
        this.maxRuleViolations = task.getMaxRuleViolations();
        if (this.maxRuleViolations > 0) {
            this.failOnRuleViolation = true;
        }
        this.rulesetPaths = task.getRulesetFiles() == null ? "" : task.getRulesetFiles();
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
            Language lang = LanguageRegistry.findLanguageByTerseName(version.getName());
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

        // Setup RuleSetFactory and validate RuleSets
        RuleSetLoader rulesetLoader = RuleSetLoader.fromPmdConfig(configuration)
                                                   .loadResourcesWith(setupResourceLoader());

        List<RuleSet> rules = loadRulesets(rulesetLoader);

        if (configuration.getSuppressMarker() != null) {
            project.log("Setting suppress marker to be " + configuration.getSuppressMarker(), Project.MSG_VERBOSE);
        }


        @SuppressWarnings("PMD.CloseResource")
        ViolationCounterListener reportSizeListener = new ViolationCounterListener();

        final List<TextFile> files = collectFiles(filesets, project, configuration.isReportShortNames());

        try (GlobalAnalysisListener listener = getListener(reportSizeListener)) {
            PMD.processTextFiles(configuration, rules, files, listener);
        } catch (Exception e) {
            throw new BuildException("Exception while closing data sources", e);
        }

        int problemCount = reportSizeListener.getResult();
        project.log(problemCount + " problems found", Project.MSG_VERBOSE);

        if (failuresPropertyName != null && problemCount > 0) {
            project.setProperty(failuresPropertyName, String.valueOf(problemCount));
            project.log("Setting property " + failuresPropertyName + " to " + problemCount, Project.MSG_VERBOSE);
        }

        if (failOnRuleViolation && problemCount > maxRuleViolations) {
            throw new BuildException("Stopping build since PMD found " + problemCount + " rule violations in the code");
        }
    }

    private List<RuleSet> loadRulesets(RuleSetLoader rulesetLoader) {
        try {
            // This is just used to validate and display rules. Each thread will create its own ruleset
            // Substitute env variables/properties
            String ruleSetString = project.replaceProperties(rulesetPaths);

            List<String> rulesets = Arrays.asList(ruleSetString.split(","));
            List<RuleSet> rulesetList = rulesetLoader.loadFromResources(rulesets);
            if (rulesetList.isEmpty()) {
                throw new BuildException("No rulesets");
            }
            logRulesUsed(rulesetList);
            return rulesetList;
        } catch (RuleSetLoadException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private List<TextFile> collectFiles(List<FileSet> filesets, Project project, boolean reportShortNames) {
        final List<TextFile> files = new ArrayList<>();
        for (FileSet fs : filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            java.nio.file.Path baseDir = ds.getBasedir().toPath();

            for (String srcFile : ds.getIncludedFiles()) {
                java.nio.file.Path filePath = baseDir.resolve(srcFile);
                TextFileBuilder builder = FileUtil.buildNioTextFile(configuration, filePath);
                if (reportShortNames) {
                    builder = builder.withDisplayName(srcFile);
                }
                files.add(builder.build());
            }
        }
        return files;
    }

    private @NonNull GlobalAnalysisListener getListener(ViolationCounterListener reportSizeListener) {
        List<GlobalAnalysisListener> renderers = new ArrayList<>(formatters.size() + 1);
        try {
            renderers.add(makeLogListener());
            renderers.add(reportSizeListener);
            for (Formatter formatter : formatters) {
                project.log("Sending a report to " + formatter, Project.MSG_VERBOSE);
                renderers.add(formatter.newListener(project));
            }
        } catch (IOException e) {
            // close those opened so far
            Exception e2 = IOUtil.closeAll(renderers);
            if (e2 != null) {
                e.addSuppressed(e2);
            }
            throw new BuildException("Exception while initializing renderers", e);
        }

        return GlobalAnalysisListener.tee(renderers);
    }

    private GlobalAnalysisListener makeLogListener() {
        return new GlobalAnalysisListener() {

            @Override
            public FileAnalysisListener startFileAnalysis(TextFile dataSource) {
                project.log("Processing file " + dataSource.getPathId(), Project.MSG_VERBOSE);
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
                configuration.prependClasspath(auxClasspath.toString());
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }

    public void execute() throws BuildException {
        final AntLogHandler antLogHandler = new AntLogHandler(project);
        final ScopedLogHandlersManager logManager = new ScopedLogHandlersManager(antLogHandler.getAntLogLevel(), antLogHandler);
        try {
            doTask();
        } finally {
            logManager.close();
            // only close the classloader, if it is ours. Otherwise we end up with class not found
            // exceptions
            if (configuration.getClassLoader() instanceof ClasspathClassLoader) {
                IOUtil.tryCloseClassLoader(configuration.getClassLoader());
            }
        }
    }

    private void logRulesUsed(List<RuleSet> rulesets) {
        project.log("Using these rulesets: " + rulesetPaths, Project.MSG_VERBOSE);

        for (RuleSet ruleSet : rulesets) {
            for (Rule rule : ruleSet.getRules()) {
                project.log("Using rule " + rule.getName(), Project.MSG_VERBOSE);
            }
        }
    }
}
