/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.benchmark.TextTimingReportRenderer;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimingReport;
import net.sourceforge.pmd.benchmark.TimingReportRenderer;
import net.sourceforge.pmd.cli.commands.typesupport.internal.PmdLanguageTypeSupport;
import net.sourceforge.pmd.cli.commands.typesupport.internal.PmdLanguageVersionTypeSupport;
import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.reporting.ReportStats;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

@Command(name = "check", showDefaultValues = true,
    description = "The PMD standard source code analyzer")
public class PmdCommand extends AbstractAnalysisPmdSubcommand {

    static {
        final Properties emptyProps = new Properties();
        final StringBuilder reportPropertiesHelp = new StringBuilder();
        final String lineSeparator = System.lineSeparator();
        
        for (final String rendererName : RendererFactory.supportedRenderers()) {
            final Renderer renderer = RendererFactory.createRenderer(rendererName, emptyProps);
            
            if (!renderer.getPropertyDescriptors().isEmpty()) {
                reportPropertiesHelp.append(rendererName + ":" + lineSeparator);
                for (final PropertyDescriptor<?> property : renderer.getPropertyDescriptors()) {
                    reportPropertiesHelp.append("  ").append(property.name()).append(" - ")
                        .append(property.description()).append(lineSeparator);
                    final Object deflt = property.defaultValue();
                    if (deflt != null && !"".equals(deflt)) {
                        reportPropertiesHelp.append("    Default: ").append(deflt)
                            .append(lineSeparator);
                    }
                }
            }
        }
        
        // System Properties are the easier way to inject dynamically computed values into the help of an option
        System.setProperty("pmd-cli.pmd.report.properties.help", reportPropertiesHelp.toString());
    }

    private List<String> rulesets;
    
    private Path ignoreListPath;

    private String format;

    private int threads;

    private boolean benchmark;

    private boolean shortnames;

    private boolean showSuppressed;

    private String suppressMarker;

    private RulePriority minimumPriority;

    private Properties properties = new Properties();

    private Path reportFile;

    private List<LanguageVersion> languageVersion;

    private Language forceLanguage;

    private String auxClasspath;

    private boolean noRuleSetCompatibility;

    private Path cacheLocation;

    private boolean noCache;

    private boolean showProgressBar;

    @Option(names = { "--rulesets", "-R" },
               description = "Path to a ruleset xml file. "
                             + "The path may reference a resource on the classpath of the application, be a local file system path, or a URL. "
                             + "The option can be repeated, and multiple arguments separated by comma can be provided to a single occurrence of the option.",
               required = true, split = ",", arity = "1..*")
    public void setRulesets(final List<String> rulesets) {
        this.rulesets = rulesets;
    }

    @Option(names = "--ignore-list",
            description = "Path to a file containing a list of files to exclude from the analysis, one path per line. "
                          + "This option can be combined with --dir, --file-list and --uri.")
    public void setIgnoreListPath(final Path ignoreListPath) {
        this.ignoreListPath = ignoreListPath;
    }

    @Option(names = { "--format", "-f" },
            description = "Report format.%nValid values: ${COMPLETION-CANDIDATES}%n"
                    + "Alternatively, you can provide the fully qualified name of a custom Renderer in the classpath.",
            defaultValue = "text", completionCandidates = PmdSupportedReportFormatsCandidates.class)
    public void setFormat(final String format) {
        this.format = format;
    }

    @Option(names = { "--benchmark", "-b" },
            description = "Benchmark mode - output a benchmark report upon completion; default to System.err.")
    public void setBenchmark(final boolean benchmark) {
        this.benchmark = benchmark;
    }

    @Option(names = "--short-names", description = "Prints shortened filenames in the report.")
    public void setShortnames(final boolean shortnames) {
        this.shortnames = shortnames;
    }

    @Option(names = "--show-suppressed", description = "Report should show suppressed rule violations.")
    public void setShowSuppressed(final boolean showSuppressed) {
        this.showSuppressed = showSuppressed;
    }

    @Option(names = "--suppress-marker",
            description = "Specifies the string that marks a line which PMD should ignore.",
            defaultValue = "NOPMD")
    public void setSuppressMarker(final String suppressMarker) {
        this.suppressMarker = suppressMarker;
    }

    @Option(names = "--minimum-priority",
            description = "Rule priority threshold; rules with lower priority than configured here won't be used.%n"
                    + "Valid values (case insensitive): ${COMPLETION-CANDIDATES}",
            defaultValue = "Low")
    public void setMinimumPriority(final RulePriority priority) {
        this.minimumPriority = priority;
    }

    @Option(names = { "--property", "-P" }, description = "Key-value pair defining a property for the report format.%n"
                + "Supported values for each report format:%n${sys:pmd-cli.pmd.report.properties.help}",
            completionCandidates = PmdReportPropertiesCandidates.class)
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    @Option(names = { "--report-file", "-r" },
            description = "Path to a file to which report output is written. "
                + "The file is created if it does not exist. "
                + "If this option is not specified, the report is rendered to standard output.")
    public void setReportFile(final Path reportFile) {
        this.reportFile = reportFile;
    }

    @Option(names = "--use-version",
            description = "The language version PMD should use when parsing source code.%nValid values: ${COMPLETION-CANDIDATES}",
            completionCandidates = PmdLanguageVersionTypeSupport.class, converter = PmdLanguageVersionTypeSupport.class)
    public void setLanguageVersion(final List<LanguageVersion> languageVersion) {
        // Make sure we only set 1 version per language
        languageVersion.stream().collect(Collectors.groupingBy(LanguageVersion::getLanguage))
            .forEach((l, list) -> {
                if (list.size() > 1) {
                    throw new ParameterException(spec.commandLine(), "Can only set one version per language, "
                            + "but for language " + l.getName() + " multiple versions were provided "
                            + list.stream().map(LanguageVersion::getTerseName).collect(Collectors.joining("', '", "'", "'")));
                }
            });

        this.languageVersion = languageVersion;
    }

    @Option(names = "--force-language",
            description = "Force a language to be used for all input files, irrespective of file names. "
                          + "When using this option, the automatic language selection by extension is disabled, and PMD "
                          + "tries to parse all input files with the given language's parser. "
                          + "Parsing errors are ignored.%nValid values: ${COMPLETION-CANDIDATES}",
            completionCandidates = PmdLanguageTypeSupport.class, converter = PmdLanguageTypeSupport.class)
    public void setForceLanguage(final Language forceLanguage) {
        this.forceLanguage = forceLanguage;
    }

    @Option(names = "--aux-classpath",
            description = "Specifies the classpath for libraries used by the source code. "
                    + "This is used to resolve types in Java source files. The platform specific path delimiter "
                    + "(\":\" on Linux, \";\" on Windows) is used to separate the entries. "
                    + "Alternatively, a single 'file:' URL to a text file containing path elements on consecutive lines "
                    + "can be specified.")
    public void setAuxClasspath(final String auxClasspath) {
        this.auxClasspath = auxClasspath;
    }

    @Option(names = "--no-ruleset-compatibility",
            description = "Disable the ruleset compatibility filter. The filter is active by default and tries automatically 'fix' old ruleset files with old rule names")
    public void setNoRuleSetCompatibility(final boolean noRuleSetCompatibility) {
        this.noRuleSetCompatibility = noRuleSetCompatibility;
    }

    @Option(names = "--cache",
            description = "Specify the location of the cache file for incremental analysis. "
                    + "This should be the full path to the file, including the desired file name (not just the parent directory). "
                    + "If the file doesn't exist, it will be created on the first run. The file will be overwritten on each run "
                    + "with the most up-to-date rule violations.")
    public void setCacheLocation(final Path cacheLocation) {
        this.cacheLocation = cacheLocation;
    }

    @Option(names = "--no-cache", description = "Explicitly disable incremental analysis. The '-cache' option is ignored if this switch is present in the command line.")
    public void setNoCache(final boolean noCache) {
        this.noCache = noCache;
    }

    @Option(names = { "--threads", "-t" }, description = "Sets the number of threads used by PMD.",
            defaultValue = "1")
    public void setThreads(final int threads) {
        if (threads < 0) {
            throw new ParameterException(spec.commandLine(), "Thread count should be a positive number or zero, found " + threads + " instead.");
        }
        
        this.threads = threads;
    }

    @Option(names = "--no-progress", negatable = true, defaultValue = "true",
            description = "Enables / disables progress bar indicator of live analysis progress.")
    public void setShowProgressBar(final boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }

    /**
     * Converts these parameters into a configuration.
     *
     * @return A new PMDConfiguration corresponding to these parameters
     *
     * @throws ParameterException if the parameters are inconsistent or incomplete
     */
    public PMDConfiguration toConfiguration() {
        final PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPathList(inputPaths);
        configuration.setInputFilePath(fileListPath);
        configuration.setIgnoreFilePath(ignoreListPath);
        configuration.setInputUri(uri);
        configuration.setReportFormat(format);
        configuration.setDebug(debug);
        configuration.setSourceEncoding(encoding.getEncoding().name());
        configuration.setMinimumPriority(minimumPriority);
        configuration.setReportFile(reportFile);
        configuration.setReportProperties(properties);
        configuration.setReportShortNames(shortnames);
        configuration.setRuleSets(rulesets);
        configuration.setRuleSetFactoryCompatibilityEnabled(!this.noRuleSetCompatibility);
        configuration.setShowSuppressedViolations(showSuppressed);
        configuration.setSuppressMarker(suppressMarker);
        configuration.setThreads(threads);
        configuration.setFailOnViolation(failOnViolation);
        configuration.setAnalysisCacheLocation(cacheLocation != null ? cacheLocation.toString() : null);
        configuration.setIgnoreIncrementalAnalysis(noCache);
        configuration.setProgressBar(showProgressBar);

        if (languageVersion != null) {
            configuration.setDefaultLanguageVersions(languageVersion);
        }
        
        // Important: do this after setting default versions, so we can pick them up
        if (forceLanguage != null) {
            final LanguageVersion forcedLangVer = configuration.getLanguageVersionDiscoverer()
                    .getDefaultLanguageVersion(forceLanguage);
            configuration.setForceLanguageVersion(forcedLangVer);
        }

        // Setup CLI message reporter
        configuration.setReporter(new SimpleMessageReporter(LoggerFactory.getLogger(PmdCommand.class)));

        try {
            configuration.prependAuxClasspath(auxClasspath);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(spec.commandLine(), "Invalid auxiliary classpath: " + e.getMessage(), e);
        }
        return configuration;
    }

    @Override
    protected ExecutionResult execute() {
        if (benchmark) {
            TimeTracker.startGlobalTracking();
        }

        final PMDConfiguration configuration = toConfiguration();
        final MessageReporter pmdReporter = configuration.getReporter();

        try {
            PmdAnalysis pmd = null;
            try {
                try {
                    pmd = PmdAnalysis.create(configuration);
                } catch (final Exception e) {
                    pmdReporter.errorEx("Could not initialize analysis", e);
                    return ExecutionResult.ERROR;
                }

                pmdReporter.log(Level.DEBUG, "Current classpath:\n{0}", System.getProperty("java.class.path"));
                final ReportStats stats = pmd.runAndReturnStats();
                if (pmdReporter.numErrors() > 0) {
                    // processing errors are ignored
                    return ExecutionResult.ERROR;
                } else if (stats.getNumViolations() > 0 && configuration.isFailOnViolation()) {
                    return ExecutionResult.VIOLATIONS_FOUND;
                } else {
                    return ExecutionResult.OK;
                }
            } finally {
                if (pmd != null) {
                    pmd.close();
                }
            }

        } catch (final Exception e) {
            pmdReporter.errorEx("Exception while running PMD.", e);
            printErrorDetected(pmdReporter, 1);
            return ExecutionResult.ERROR;
        } finally {
            finishBenchmarker(pmdReporter);
        }
    }

    private void printErrorDetected(MessageReporter reporter, int errors) {
        String msg = LogMessages.errorDetectedMessage(errors, "pmd");
        // note: using error level here increments the error count of the reporter,
        // which we don't want.
        reporter.info(StringUtil.quoteMessageFormat(msg));
    }

    private void finishBenchmarker(final MessageReporter pmdReporter) {
        if (benchmark) {
            final TimingReport timingReport = TimeTracker.stopGlobalTracking();

            // TODO get specified report format from config
            final TimingReportRenderer renderer = new TextTimingReportRenderer();

            try {
                // No try-with-resources, do not want to close STDERR
                @SuppressWarnings("PMD.CloseResource")
                final Writer writer = new OutputStreamWriter(System.err);
                renderer.render(timingReport, writer);
            } catch (final IOException e) {
                pmdReporter.errorEx("Error producing benchmark report", e);
            }
        }
    }

    /**
     * Provider of candidates for valid report formats.
     */
    private static final class PmdSupportedReportFormatsCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            return RendererFactory.supportedRenderers().iterator();
        }
    }

    /**
     * Provider of candidates for valid report properties.
     * 
     * Check the help for which ones are supported by each report format and possible values.
     */
    private static final class PmdReportPropertiesCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            final List<String> propertyNames = new ArrayList<>();
            final Properties emptyProps = new Properties();
            for (final String rendererName : RendererFactory.supportedRenderers()) {
                final Renderer renderer = RendererFactory.createRenderer(rendererName, emptyProps);
                
                for (final PropertyDescriptor<?> property : renderer.getPropertyDescriptors()) {
                    propertyNames.add(property.name());
                }
            }
            return propertyNames.iterator();
        }
    }
}
