/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;

/**
 * @deprecated Internal API. Use {@link PMD#runPmd(String[])} or {@link PMD#main(String[])}
 */
@Deprecated
@InternalApi
public class PMDParameters {

    static final String RELATIVIZE_PATHS_WITH = "--relativize-paths-with";
    @Parameter(names = { "--rulesets", "-rulesets", "-R" },
               description = "Path to a ruleset xml file. "
                             + "The path may reference a resource on the classpath of the application, be a local file system path, or a URL. "
                             + "The option can be repeated, and multiple arguments can be provided to a single occurrence of the option.",
               required = true,
               variableArity = true)
    private List<String> rulesets;

    @Parameter(names = { "--uri", "-uri", "-u" },
               description = "Database URI for sources. "
                             + "One of --dir, --file-list or --uri must be provided. "
    )
    private String uri;

    @Parameter(names = { "--dir", "-dir", "-d" },
               description = "Path to a source file, or directory containing source files to analyze. "
                             // About the following line:
                             // In PMD 6, this is only the case for files found in directories. If you
                             // specify a file directly, and it is unknown, then the Java parser is used.
                             + "Note that a file is only effectively added if it matches a language known by PMD. "
                             + "Zip and Jar files are also supported, if they are specified directly "
                             + "(archive files found while exploring a directory are not recursively expanded). "
                             + "This option can be repeated, and multiple arguments can be provided to a single occurrence of the option. "
                             + "One of --dir, --file-list or --uri must be provided. ",
               variableArity = true)
    private List<String> inputPaths = new ArrayList<>();

    @Parameter(names = { "--file-list", "-filelist" },
               description =
                   "Path to a file containing a list of files to analyze, one path per line. "
                   + "One of --dir, --file-list or --uri must be provided. "
    )
    private String fileListPath;

    @Parameter(names = { "--ignore-list", "-ignorelist" },
               description = "Path to a file containing a list of files to exclude from the analysis, one path per line. "
                             + "This option can be combined with --dir and --file-list. "
    )
    private String ignoreListPath;

    @Parameter(names = { "--format", "-format", "-f" }, description = "Report format type.")
    private String format = "text"; // Enhance to support other usage

    @Parameter(names = { "--debug", "--verbose", "-debug", "-verbose", "-D", "-V" }, description = "Debug mode.")
    private boolean debug = false;

    @Parameter(names = { "--help", "-help", "-h", "-H" }, description = "Display help on usage.", help = true)
    private boolean help = false;

    @Parameter(names = { "--encoding", "-encoding", "-e" },
            description = "Specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8).")
    private String encoding = "UTF-8";

    @Parameter(names = { "--threads", "-threads", "-t" }, description = "Sets the number of threads used by PMD.",
            validateWith = PositiveInteger.class)
    private int threads = 1; // see also default in PMDTask (Ant)

    @Parameter(names = { "--benchmark", "-benchmark", "-b" },
            description = "Benchmark mode - output a benchmark report upon completion; default to System.err.")
    private boolean benchmark = false;

    @Parameter(names = { "--stress", "-stress", "-S" }, description = "Performs a stress test.")
    private boolean stress = false;

    @Parameter(names = { "--show-suppressed", "-showsuppressed" }, description = "Report should show suppressed rule violations.")
    private boolean showsuppressed = false;

    @Parameter(names = { "--suppress-marker", "-suppressmarker" },
            description = "Specifies the string that marks a line which PMD should ignore; default is NOPMD.")
    private String suppressmarker = "NOPMD";

    @Parameter(names = { "--minimum-priority", "-minimumpriority", "-min" },
            description = "Rule priority threshold; rules with lower priority than configured here won't be used. "
                    + "Valid values are integers between 1 and 5 (inclusive), with 5 being the lowest priority.",
            validateValueWith = RulePriorityValidator.class)
    private int minimumPriority = RulePriority.LOW.getPriority();

    @Parameter(names = { "--property", "-property", "-P" }, description = "{name}={value}: Define a property for the report format.",
            converter = PropertyConverter.class)
    private List<Properties> properties = new ArrayList<>();

    @Parameter(names = { "--report-file", "-reportfile", "-r" },
               description = "Path to a file to which report output is written. "
                   + "The file is created if it does not exist. "
                   + "If this option is not specified, the report is rendered to standard output.")
    private String reportfile = null;

    @Parameter(names = { RELATIVIZE_PATHS_WITH, "-z" },
               variableArity = true,
               description = "Path relative to which directories are rendered in the report. "
                             + "This option allows shortening directories in the report; "
                             + "without it, paths are rendered as mentioned in the source directory (option \"--dir\"). "
                             + "The option can be repeated, in which case the shortest relative path will be used. "
                             + "If the root path is mentioned (e.g. \"/\" or \"C:\\\"), then the paths will be rendered as absolute.",
               validateValueWith = PathToRelativizeRootValidator.class,
               converter = StringToPathConverter.class)
    private List<Path> relativizePathRoot = new ArrayList<>();

    @Parameter(names = { "-version", "-v" }, description = "Specify version of a language PMD should use.")
    private String version = null;

    @Parameter(names = "--version", description = "Display current version of PMD and exit without performing any analysis.", help = true)
    private boolean currentVersion = false;

    @Parameter(names = { "-language", "-l" }, description = "Specify a language PMD should use.")
    private String language = null;

    @Parameter(names = { "--force-language", "-force-language" },
               description = "Force a language to be used for all input files, irrespective of file names. "
                             + "When using this option, the automatic language selection by extension is disabled, and PMD "
                             + "tries to parse all input files with the given language's parser. "
                             + "Parsing errors are ignored."
               )
    private String forceLanguage = null;

    @Parameter(names = { "--aux-classpath", "-auxclasspath" },
            description = "Specifies the classpath for libraries used by the source code. "
                    + "This is used to resolve types in Java source files. The platform specific path delimiter "
                    + "(\":\" on Linux, \";\" on Windows) is used to separate the entries. "
                    + "Alternatively, a single 'file:' URL to a text file containing path elements on consecutive lines "
                    + "can be specified.")
    private String auxclasspath;

    @Parameter(names = { "--fail-on-violation", "--failOnViolation", "-failOnViolation"}, arity = 1,
            description = "By default PMD exits with status 4 if violations are found. Disable this option with '-failOnViolation false' to exit with 0 instead and just write the report.")
    private boolean failOnViolation = true;

    @Parameter(names = { "--no-ruleset-compatibility", "-norulesetcompatibility" },
            description = "Disable the ruleset compatibility filter. The filter is active by default and tries automatically 'fix' old ruleset files with old rule names")
    private boolean noRuleSetCompatibility = false;

    @Parameter(names = { "--cache", "-cache" }, arity = 1,
            description = "Specify the location of the cache file for incremental analysis. "
                    + "This should be the full path to the file, including the desired file name (not just the parent directory). "
                    + "If the file doesn't exist, it will be created on the first run. The file will be overwritten on each run "
                    + "with the most up-to-date rule violations.")
    private String cacheLocation = null;

    @Parameter(names = { "--no-cache", "-no-cache" }, description = "Explicitly disable incremental analysis. The '-cache' option is ignored if this switch is present in the command line.")
    private boolean noCache = false;

    @Parameter(names = "--use-version", description = "The language version PMD should use when parsing source code in the language-version format, ie: 'java-1.8'")
    private List<String> languageVersions = new ArrayList<>();

    // this has to be a public static class, so that JCommander can use it!
    public static class PropertyConverter implements IStringConverter<Properties> {

        private static final char SEPARATOR = '=';

        @Override
        public Properties convert(String value) {
            int indexOfSeparator = value.indexOf(SEPARATOR);
            if (indexOfSeparator < 0) {
                throw new ParameterException(
                        "Property name must be separated with an = sign from it value: name=value.");
            }
            String propertyName = value.substring(0, indexOfSeparator);
            String propertyValue = value.substring(indexOfSeparator + 1);
            Properties properties = new Properties();
            properties.put(propertyName, propertyValue);
            return properties;
        }
    }


    // this has to be a public static class, so that JCommander can use it!
    public static class RulePriorityValidator implements IValueValidator<Integer> {

        @Override
        public void validate(String name, Integer value) throws ParameterException {
            if (value < 1 || value > 5) {
                throw new ParameterException("Priority values can only be integer value, between 1 and 5," + value + " is not valid");
            }
        }
    }

    public static class PathToRelativizeRootValidator implements IValueValidator<List<Path>> {
        @Override
        public void validate(String name, List<Path> value) throws ParameterException {
            for (Path p : value) {
                if (Files.isRegularFile(p)) {
                    throw new ParameterException("Expected a directory path for option " + name + ", found a file: " + p);
                }
            }
        }
    }

    public static class StringToPathConverter implements IStringConverter<Path> {
        @Override
        public Path convert(String value) {
            return Paths.get(value);
        }
    }

    /**
     * Converts these parameters into a configuration.
     *
     * @return A new PMDConfiguration corresponding to these parameters
     *
     * @throws IllegalArgumentException if the parameters are inconsistent or incomplete
     */
    public PMDConfiguration toConfiguration() {
        return toConfiguration(LanguageRegistry.PMD);
    }

    /**
     * Converts these parameters into a configuration. The given language
     * registry is used to resolve references to languages in the parameters.
     *
     * @return A new PMDConfiguration corresponding to these parameters
     *
     * @throws IllegalArgumentException if the parameters are inconsistent or incomplete
     */
    public @NonNull PMDConfiguration toConfiguration(LanguageRegistry registry) {
        if (null == this.getSourceDir() && null == this.getUri() && null == this.getFileListPath()) {
            throw new IllegalArgumentException(
                    "Please provide a parameter for source root directory (-dir or -d), database URI (-uri or -u), or file list path (-filelist).");
        }
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths(this.getInputPaths().stream().collect(Collectors.joining(",")));
        configuration.setInputFilePath(this.getFileListPath());
        configuration.setIgnoreFilePath(this.getIgnoreListPath());
        configuration.setInputUri(this.getUri());
        configuration.setReportFormat(this.getFormat());
        configuration.setBenchmark(this.isBenchmark());
        configuration.setDebug(this.isDebug());
        configuration.addRelativizeRoots(this.relativizePathRoot);
        configuration.setMinimumPriority(this.getMinimumPriority());
        configuration.setReportFile(this.getReportfile());
        configuration.setReportProperties(this.getProperties());
        configuration.setRuleSets(Arrays.asList(this.getRulesets().split(",")));
        configuration.setRuleSetFactoryCompatibilityEnabled(!this.noRuleSetCompatibility);
        configuration.setShowSuppressedViolations(this.isShowsuppressed());
        configuration.setSourceEncoding(this.getEncoding());
        configuration.setStressTest(this.isStress());
        configuration.setSuppressMarker(this.getSuppressmarker());
        configuration.setThreads(this.getThreads());
        configuration.setFailOnViolation(this.isFailOnViolation());
        configuration.setAnalysisCacheLocation(this.cacheLocation);
        configuration.setIgnoreIncrementalAnalysis(this.isIgnoreIncrementalAnalysis());

        LanguageVersion forceLangVersion = getForceLangVersion(registry);
        if (forceLangVersion != null) {
            configuration.setForceLanguageVersion(forceLangVersion);
        }

        LanguageVersion languageVersion = getLangVersion(registry);
        if (languageVersion != null) {
            configuration.getLanguageVersionDiscoverer().setDefaultLanguageVersion(languageVersion);
        }

        for (String langVerStr : this.getLanguageVersions()) {
            int dashPos = langVerStr.indexOf('-');
            if (dashPos == -1) {
                throw new IllegalArgumentException("Invalid language version: " + langVerStr);
            }
            String langStr = langVerStr.substring(0, dashPos);
            String verStr = langVerStr.substring(dashPos + 1);
            Language lang = LanguageRegistry.findLanguageByTerseName(langStr);
            LanguageVersion langVer = null;
            if (lang != null) {
                langVer = lang.getVersion(verStr);
            }
            if (lang == null || langVer == null) {
                throw new IllegalArgumentException("Invalid language version: " + langVerStr);
            }
            configuration.getLanguageVersionDiscoverer().setDefaultLanguageVersion(langVer);
        }

        try {
            configuration.prependAuxClasspath(this.getAuxclasspath());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid auxiliary classpath: " + e.getMessage(), e);
        }
        return configuration;
    }


    public boolean isIgnoreIncrementalAnalysis() {
        return noCache;
    }


    public boolean isDebug() {
        return debug;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isVersion() {
        return currentVersion;
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

    public boolean isShowsuppressed() {
        return showsuppressed;
    }

    public String getSuppressmarker() {
        return suppressmarker;
    }

    public RulePriority getMinimumPriority() {
        return RulePriority.valueOf(minimumPriority);
    }

    public Properties getProperties() {
        Properties result = new Properties();
        for (Properties p : properties) {
            result.putAll(p);
        }
        return result;
    }

    public String getReportfile() {
        return reportfile;
    }

    private @Nullable LanguageVersion getLangVersion(LanguageRegistry registry) {
        if (language != null) {
            Language lang = registry.getLanguageById(language);
            if (lang != null) {
                return version != null ? lang.getVersion(version)
                                       : lang.getDefaultVersion();
            }
        }
        return null;
    }

    public @Nullable String getLanguage() {
        return language;
    }

    private @Nullable LanguageVersion getForceLangVersion(LanguageRegistry registry) {
        Language lang = forceLanguage != null ? registry.getLanguageById(forceLanguage) : null;
        return lang != null ? lang.getDefaultVersion() : null;
    }

    public List<String> getLanguageVersions() {
        return languageVersions;
    }

    public String getForceLanguage() {
        return forceLanguage != null ? forceLanguage : "";
    }

    public String getAuxclasspath() {
        return auxclasspath;
    }

    @Deprecated
    public String getRulesets() {
        return StringUtils.join(rulesets, ",");
    }

    public List<String> getRulesetRefs() {
        return rulesets;
    }

    public List<String> getInputPaths() {
        return inputPaths;
    }

    @Deprecated
    public String getSourceDir() {
        return StringUtils.join(inputPaths, ",");
    }

    public String getFileListPath() {
        return fileListPath;
    }

    public String getIgnoreListPath() {
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
    public String getUri() {
        return uri;
    }

    /**
     * @param uri
     *            the uri specifying the source directory.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

}
