/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant.internal;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
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

        SourceLanguage version = task.getSourceLanguage();
        if (version != null) {
            LanguageVersion languageVersion = LanguageRegistry
                    .findLanguageVersionByTerseName(version.getName() + " " + version.getVersion());
            if (languageVersion == null) {
                throw new BuildException("The following language is not supported:" + version + ".");
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
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.getRulesetFactory(configuration);
        try {
            // This is just used to validate and display rules. Each thread will create its own ruleset
            String ruleSets = configuration.getRuleSets();
            if (StringUtil.isNotEmpty(ruleSets)) {
                // Substitute env variables/properties
                configuration.setRuleSets(project.replaceProperties(ruleSets));
            }
            RuleSets rules = ruleSetFactory.createRuleSets(configuration.getRuleSets());
            logRulesUsed(rules);
        } catch (RuleSetNotFoundException e) {
            throw new BuildException(e.getMessage(), e);
        }

        if (configuration.getSuppressMarker() != null) {
            project.log("Setting suppress marker to be " + configuration.getSuppressMarker(), Project.MSG_VERBOSE);
        }

        // Start the Formatters
        for (Formatter formatter : formatters) {
            project.log("Sending a report to " + formatter, Project.MSG_VERBOSE);
            formatter.start(project.getBaseDir().toString());
        }

        // log("Setting Language Version " + languageVersion.getShortName(),
        // Project.MSG_VERBOSE);

        // TODO Do we really need all this in a loop over each FileSet? Seems
        // like a lot of redundancy
        RuleContext ctx = new RuleContext();
        Report errorReport = new Report();
        final AtomicInteger reportSize = new AtomicInteger();
        final String separator = System.getProperty("file.separator");

        for (FileSet fs : filesets) {
            List<DataSource> files = new LinkedList<>();
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            String[] srcFiles = ds.getIncludedFiles();
            for (String srcFile : srcFiles) {
                File file = new File(ds.getBasedir() + separator + srcFile);
                files.add(new FileDataSource(file));
            }

            final String inputPaths = ds.getBasedir().getPath();
            configuration.setInputPaths(inputPaths);

            Renderer logRenderer = new AbstractRenderer("log", "Logging renderer") {
                @Override
                public void start() {
                    // Nothing to do
                }

                @Override
                public void startFileAnalysis(DataSource dataSource) {
                    project.log("Processing file " + dataSource.getNiceFileName(false, inputPaths),
                            Project.MSG_VERBOSE);
                }

                @Override
                public void renderFileReport(Report r) {
                    int size = r.size();
                    if (size > 0) {
                        reportSize.addAndGet(size);
                    }
                }

                @Override
                public void end() {
                    // Nothing to do
                }

                @Override
                public String defaultFileExtension() {
                    return null;
                } // not relevant
            };
            List<Renderer> renderers = new ArrayList<>(formatters.size() + 1);
            renderers.add(logRenderer);
            for (Formatter formatter : formatters) {
                renderers.add(formatter.getRenderer());
            }
            try {
                PMD.processFiles(configuration, ruleSetFactory, files, ctx, renderers);
            } catch (RuntimeException pmde) {
                handleError(ctx, errorReport, pmde);
            }
        }

        int problemCount = reportSize.get();
        project.log(problemCount + " problems found", Project.MSG_VERBOSE);

        for (Formatter formatter : formatters) {
            formatter.end(errorReport);
        }

        if (failuresPropertyName != null && problemCount > 0) {
            project.setProperty(failuresPropertyName, String.valueOf(problemCount));
            project.log("Setting property " + failuresPropertyName + " to " + problemCount, Project.MSG_VERBOSE);
        }

        if (failOnRuleViolation && problemCount > maxRuleViolations) {
            throw new BuildException("Stopping build since PMD found " + problemCount + " rule violations in the code");
        }
    }

    private void handleError(RuleContext ctx, Report errorReport, RuntimeException pmde) {

        pmde.printStackTrace();
        project.log(pmde.toString(), Project.MSG_VERBOSE);

        Throwable cause = pmde.getCause();

        if (cause != null) {
            StringWriter strWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strWriter);
            cause.printStackTrace(printWriter);
            project.log(strWriter.toString(), Project.MSG_VERBOSE);
            IOUtils.closeQuietly(printWriter);

            if (StringUtil.isNotEmpty(cause.getMessage())) {
                project.log(cause.getMessage(), Project.MSG_VERBOSE);
            }
        }

        if (failOnError) {
            throw new BuildException(pmde);
        }
        errorReport.addError(new Report.ProcessingError(pmde.getMessage(), ctx.getSourceCodeFilename()));
    }

    private void setupClassLoader() {
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
        boolean parentFirst = true;
        configuration.setClassLoader(
                new AntClassLoader(Thread.currentThread().getContextClassLoader(), project, classpath, parentFirst));

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
        final Handler antLogHandler = new AntLogHandler(project);
        final ScopedLogHandlersManager logManager = new ScopedLogHandlersManager(Level.FINEST, antLogHandler);
        try {
            doTask();
        } finally {
            tryClose(configuration.getClassLoader());
            logManager.close();
            IOUtil.tryCloseClassLoader(configuration.getClassLoader());
        }
    }

    private static void tryClose(ClassLoader classLoader) {
        if (classLoader instanceof Closeable) {
            try {
                ((Closeable) classLoader).close();
            } catch (IOException ignore) {
                // do nothing.
            }
        }
    }

    private void logRulesUsed(RuleSets rules) {
        project.log("Using these rulesets: " + configuration.getRuleSets(), Project.MSG_VERBOSE);

        RuleSet[] ruleSets = rules.getAllRuleSets();
        for (RuleSet ruleSet : ruleSets) {
            for (Rule rule : ruleSet.getRules()) {
                project.log("Using rule " + rule.getName(), Project.MSG_VERBOSE);
            }
        }
    }
}
