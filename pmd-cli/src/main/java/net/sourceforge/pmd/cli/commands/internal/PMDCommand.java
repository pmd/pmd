package net.sourceforge.pmd.cli.commands.internal;

import java.net.URI;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.RendererFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.TypeConversionException;

@Command(name = "analyze", aliases = {"analyse", "run" }, showDefaultValues = true,
    description = "The PMD standard source code analyzer")
public class PMDCommand extends AbstractPMDSubcommand {

    private List<String> rulesets;
    
    private URI uri;

    private List<Path> inputPaths;

    private Path fileListPath;

    private Path ignoreListPath;

    private String format;

    private boolean debug;

    // TODO : Actually use an Charset instance?
    private String encoding;
    
    private int threads;

    private boolean benchmark;

    private boolean stress;

    private boolean shortnames;

    private boolean showSuppressed;

    private String suppressMarker;

    private RulePriority minimumPriority;

    private Properties properties;

    private Path reportFile;

    private List<LanguageVersion> languageVersion;

    private Language forceLanguage;

    private String auxClasspath;

    private boolean failOnViolation;

    private boolean noRuleSetCompatibility;

    private Path cacheLocation;

    private boolean noCache;

    @Option(names = { "--rulesets", "-R" },
               description = "Path to a ruleset xml file. "
                             + "The path may reference a resource on the classpath of the application, be a local file system path, or a URL. "
                             + "The option can be repeated, and multiple arguments separated by comma can be provided to a single occurrence of the option.",
               required = true, split = ",")
    public void setRulesets(final List<String> rulesets) {
        this.rulesets = rulesets;
    }

    @Option(names = { "--uri", "-u" },
            description = "Database URI for sources. "
                          + "One of --dir, --file-list or --uri must be provided.")
    public void setUri(final URI uri) {
        this.uri = uri;
    }

    @Option(names = { "--dir", "-d" },
            description = "Path to a source file, or directory containing source files to analyze. "
                          // About the following line:
                          // In PMD 6, this is only the case for files found in directories. If you
                          // specify a file directly, and it is unknown, then the Java parser is used.
                          + "Note that a file is only effectively added if it matches a language known by PMD. "
                          + "Zip and Jar files are also supported, if they are specified directly "
                          + "(archive files found while exploring a directory are not recursively expanded). "
                          + "This option can be repeated, and multiple arguments can be provided to a single occurrence of the option. "
                          + "One of --dir, --file-list or --uri must be provided. ",
            arity = "1..*")
    public void setInputPaths(final List<Path> inputPaths) {
        this.inputPaths = inputPaths;
    }

    @Option(names = { "--file-list" },
            description =
                "Path to a file containing a list of files to analyze, one path per line. "
                + "One of --dir, --file-list or --uri must be provided.")
    public void setFileListPath(final Path fileListPath) {
        this.fileListPath = fileListPath;
    }

    @Option(names = { "--ignore-list" },
            description = "Path to a file containing a list of files to exclude from the analysis, one path per line. "
                          + "This option can be combined with --dir and --file-list.")
    public void setIgnoreListPath(final Path ignoreListPath) {
        this.ignoreListPath = ignoreListPath;
    }

    @Option(names = { "--format", "-f" },
            description = "Report format.%nValid values: ${COMPLETION-CANDIDATES}%n"
                    + "Alternatively, you can provide the fully qualified name of a custom Renderer in the classpath.",
            defaultValue = "text", completionCandidates = PMDSupportedReportFormatsCandidates.class)
    public void setFormat(final String format) {
        this.format = format;
    }

    @Option(names = { "--debug", "--verbose", "-D", "-V" }, description = "Debug mode.")
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Option(names = { "--encoding", "-e" },
            description = "Specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8).",
            defaultValue = "UTF-8")
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @Option(names = { "--benchmark", "-b" },
            description = "Benchmark mode - output a benchmark report upon completion; default to System.err.")
    public void setBenchmark(final boolean benchmark) {
        this.benchmark = benchmark;
    }

    @Option(names = { "--stress", "-S" }, description = "Performs a stress test.")
    public void setStress(final boolean stress) {
        this.stress = stress;
    }

    @Option(names = { "--short-names" }, description = "Prints shortened filenames in the report.")
    public void setShortnames(final boolean shortnames) {
        this.shortnames = shortnames;
    }

    @Option(names = { "--show-suppressed" }, description = "Report should show suppressed rule violations.")
    public void setShowSuppressed(final boolean showSuppressed) {
        this.showSuppressed = showSuppressed;
    }

    @Option(names = { "--suppress-marker" },
            description = "Specifies the string that marks a line which PMD should ignore.",
            defaultValue = "NOPMD")
    public void setSuppressMarker(final String suppressMarker) {
        this.suppressMarker = suppressMarker;
    }
    
    @Option(names = { "--minimum-priority" },
            description = "Rule priority threshold; rules with lower priority than configured here won't be used.%n"
                    + "Valid values (case insensitive): ${COMPLETION-CANDIDATES}",
            defaultValue = "Low")
    public void setMinimumPriority(final RulePriority priority) {
        this.minimumPriority = priority;
    }

    // TODO : Figure out how to surface the supported properties for each report format
    @Option(names = { "--property", "-P" }, description = "Key-value pair defining a property for the report format.")
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

    @Option(names = { "--use-version" }, defaultValue = "java-latest",
            description = "Sepcify the language and version PMD should use.%nValid values: ${COMPLETION-CANDIDATES}%n",
            completionCandidates = PMDLanguageVersionCandidates.class, converter = PMDLanguageVersionConverter.class)
    public void setLanguageVersion(final List<LanguageVersion> languageVersion) {
        // Make sure we only set 1 version per language
        languageVersion.stream().collect(Collectors.groupingBy(LanguageVersion::getLanguage))
        .forEach((l, list) -> {
            if (list.size() > 1) {
                throw new ParameterException(spec.commandLine(), "Can only set one version per language, "
                        + "but for language " + l.getName() + " multiple versions were provided "
                        + list.stream().map(PMDCommand::normalizeName).collect(Collectors.joining("', '", "'", "'")));
            }
        });

        this.languageVersion = languageVersion;
    }

    @Option(names = { "--force-language" },
            description = "Force a language to be used for all input files, irrespective of file names. "
                          + "When using this option, the automatic language selection by extension is disabled, and PMD "
                          + "tries to parse all input files with the given language's parser. "
                          + "Parsing errors are ignored.%nValid values: ${COMPLETION-CANDIDATES}%n",
            completionCandidates = PMDLanguagesCandidates.class, converter = PMDLanguageConverter.class)
    public void setForceLanguage(final Language forceLanguage) {
        this.forceLanguage = forceLanguage;
    }

    @Option(names = { "--aux-classpath" },
            description = "Specifies the classpath for libraries used by the source code. "
                    + "This is used to resolve types in Java source files. The platform specific path delimiter "
                    + "(\":\" on Linux, \";\" on Windows) is used to separate the entries. "
                    + "Alternatively, a single 'file:' URL to a text file containing path elements on consecutive lines "
                    + "can be specified.")
    public void setAuxClasspath(final String auxClasspath) {
        this.auxClasspath = auxClasspath;
    }

    @Option(names = { "--fail-on-violation" },
            description = "By default PMD exits with status 4 if violations are found. Disable this option with '-failOnViolation false' to exit with 0 instead and just write the report.",
            defaultValue = "true")
    public void setFailOnViolation(final boolean failOnViolation) {
        this.failOnViolation = failOnViolation;
    }

    @Option(names = { "--no-ruleset-compatibility" },
            description = "Disable the ruleset compatibility filter. The filter is active by default and tries automatically 'fix' old ruleset files with old rule names")
    public void setNoRuleSetCompatibility(final boolean noRuleSetCompatibility) {
        this.noRuleSetCompatibility = noRuleSetCompatibility;
    }

    @Option(names = { "--cache" },
            description = "Specify the location of the cache file for incremental analysis. "
                    + "This should be the full path to the file, including the desired file name (not just the parent directory). "
                    + "If the file doesn't exist, it will be created on the first run. The file will be overwritten on each run "
                    + "with the most up-to-date rule violations.")
    public void setCacheLocation(final Path cacheLocation) {
        this.cacheLocation = cacheLocation;
    }

    @Option(names = { "--no-cache" }, description = "Explicitly disable incremental analysis. The '-cache' option is ignored if this switch is present in the command line.")
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

    /**
     * Converts these parameters into a configuration.
     *
     * @return A new PMDConfiguration corresponding to these parameters
     *
     * @throws IllegalArgumentException if the parameters are inconsistent or incomplete
     */
    public PMDConfiguration toConfiguration() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths(inputPaths.stream().map(Path::toString).collect(Collectors.toList()));
        configuration.setInputFilePath(fileListPath != null ? fileListPath.toString() : null);
        configuration.setIgnoreFilePath(ignoreListPath != null ? ignoreListPath.toString() : null);
        configuration.setInputUri(uri != null ? uri.toString() : null);
        configuration.setReportFormat(format);
        configuration.setBenchmark(benchmark);
        configuration.setDebug(debug);
        configuration.setMinimumPriority(minimumPriority);
        configuration.setReportFile(reportFile != null ? reportFile.toString() : null);
        configuration.setReportProperties(properties);
        configuration.setReportShortNames(shortnames);
        configuration.setRuleSets(rulesets);
        configuration.setRuleSetFactoryCompatibilityEnabled(!this.noRuleSetCompatibility);
        configuration.setShowSuppressedViolations(showSuppressed);
        configuration.setSourceEncoding(encoding);
        configuration.setStressTest(stress);
        configuration.setSuppressMarker(suppressMarker);
        configuration.setThreads(threads);
        configuration.setFailOnViolation(failOnViolation);
        configuration.setAnalysisCacheLocation(cacheLocation != null ? cacheLocation.toString() : null);
        configuration.setIgnoreIncrementalAnalysis(noCache);

        if (languageVersion != null) {
            configuration.setDefaultLanguageVersions(languageVersion);
        }
        
        // Important: do this after setting default versions, so we can pick them up
        if (forceLanguage != null) {
            final LanguageVersion forcedLangVer = configuration.getLanguageVersionDiscoverer()
                    .getDefaultLanguageVersion(forceLanguage);
            configuration.setForceLanguageVersion(forcedLangVer);
        }

        try {
            configuration.prependAuxClasspath(auxClasspath);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(spec.commandLine(), "Invalid auxiliary classpath: " + e.getMessage(), e);
        }
        return configuration;
    }

    public boolean isIgnoreIncrementalAnalysis() {
        return noCache;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getEncoding() {
        return encoding;
    }

    public Integer getThreads() {
        return threads;
    }

    public boolean isBenchmark() {
        return benchmark;
    }

    public boolean isStress() {
        return stress;
    }

    public boolean isShortnames() {
        return shortnames;
    }

    public boolean isShowSuppressed() {
        return showSuppressed;
    }

    public String getSuppressMarker() {
        return suppressMarker;
    }

    public RulePriority getMinimumPriority() {
        return minimumPriority;
    }

    public Properties getProperties() {
        return properties;
    }

    public Path getReportFile() {
        return reportFile;
    }

    public Language getForceLanguage() {
        return forceLanguage;
    }

    public String getAuxClasspath() {
        return auxClasspath;
    }

    public List<String> getRulesetRefs() {
        return rulesets;
    }

    public List<Path> getInputPaths() {
        return inputPaths;
    }

    public Path getFileListPath() {
        return fileListPath;
    }

    public Path getIgnoreListPath() {
        return ignoreListPath;
    }

    public String getFormat() {
        return format;
    }

    public boolean isFailOnViolation() {
        return failOnViolation;
    }

    /**
     * @return the uri alternative to source directory.
     */
    public URI getUri() {
        return uri;
    }

    @Override
    protected ExecutionResult execute() {
        if ((inputPaths == null || inputPaths.isEmpty()) && uri == null && fileListPath == null) {
            throw new ParameterException(spec.commandLine(),
                    "Please provide a parameter for source root directory (--dir or -d), "
                            + "database URI (--uri or -u), or file list path (--file-list)");
        }
        
        System.out.println("language versions: " + languageVersion);
        System.out.println("threads: " + threads);
        System.out.println("priority: " + minimumPriority);
        System.out.println("properties: " + properties);
        System.out.println("ruleset: " + rulesets);
        System.out.println("format: " + format);
        System.out.println("priority: " + minimumPriority);
        System.out.println("force lang ver: " + toConfiguration().getForceLanguageVersion());
        
        // TODO Auto-generated method stub
        return ExecutionResult.OK;
    }
    
    /**
     * Provider of candidates for valid report formats.
     */
    private static class PMDSupportedReportFormatsCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            return RendererFactory.supportedRenderers().iterator();
        }
    }

    /**
     * Provider of candidates for valid languages.
     * 
     * Beware, the help will report this on runtime, and be accurate to available
     * modules in the classpath, but autocomplete will include all at build time.
     */
    private static class PMDLanguagesCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            return LanguageRegistry.getLanguages().stream().map(PMDCommand::normalizeName).iterator();
        }
    }
    
    /**
     * Maps a String back to a {@code Language}
     * 
     * Effectively reverses the stringification done by {@code PMDLanguagesCandidates}
     */
    private static class PMDLanguageConverter implements ITypeConverter<Language> {

        @Override
        public Language convert(final String value) throws Exception {
            return LanguageRegistry.getLanguages().stream()
                    .filter(l -> normalizeName(l).equals(value)).findFirst()
                    .orElseThrow(() -> new TypeConversionException("Unknown language: " + value));
        }
        
    }

    /**
     * Provider of candidates for valid language-version combinations.
     * 
     * Beware, the help will report this on runtime, and be accurate to available
     * modules in the classpath, but autocomplete will include all at build time.
     */
    private static class PMDLanguageVersionCandidates implements Iterable<String> {
        
        @Override
        public Iterator<String> iterator() {
            // Raw language names / -latest versions, such as "java" or "java-latest"
            final Stream<String> latestLangReferences = LanguageRegistry.getLanguages().stream()
                    .map(PMDCommand::normalizeName).flatMap(name -> Stream.of(name, name + "-latest"));

            // Explicit language-version pairs, such as "java-18" or "apex-54"
            final Stream<String> allLangVersionReferences = LanguageRegistry.getLanguages().stream()
                    .flatMap(l -> l.getVersions().stream())
                    .map(PMDCommand::normalizeName);
            
            // Collect to a TreeSet to ensure alphabetical order
            final TreeSet<String> candidates = Stream.concat(latestLangReferences, allLangVersionReferences)
                    .collect(Collectors.toCollection(TreeSet::new));

            return candidates.iterator();
        }
    }

    /**
     * Maps a String back to a {@code LanguageVersion}
     * 
     * Effectively reverses the stringification done by {@code PMDLanguageVersionCandidates}
     */
    private static class PMDLanguageVersionConverter implements ITypeConverter<LanguageVersion> {

        @Override
        public LanguageVersion convert(final String value) throws Exception {
            // Is it an exact match?
            final Optional<LanguageVersion> langVer = LanguageRegistry.getLanguages().stream()
                    .flatMap(l -> l.getVersions().stream())
                    .filter(lv -> normalizeName(lv).equals(value)).findFirst();

            if (langVer.isPresent()) {
                return langVer.get();
            }

            // This is either a -latest or standalone language name
            final String langName;
            if (value.endsWith("-latest")) {
                langName = value.substring(0, value.length() - "-latest".length());
            } else {
                langName = value;
            }

            return LanguageRegistry.getLanguages().stream()
                    .filter(l -> normalizeName(l).equals(langName))
                    .map(Language::getDefaultVersion).findFirst()
                    .orElseThrow(() -> new TypeConversionException("Unknown language version: " + value));
        }
    }
    
    static String normalizeName(final LanguageVersion langVer) {
        return langVer.getTerseName().replace(' ', '-');
    }

    static String normalizeName(final Language lang) {
        return lang.getTerseName().replace(' ', '-');
    }
}
