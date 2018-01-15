/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;

public class PMDParameters {

    @Parameter(names = { "-rulesets", "-R" }, description = "Comma separated list of ruleset names to use.",
            required = true)
    private String rulesets;

    @Parameter(names = { "-uri", "-u" }, description = "Database URI for sources.", required = false)
    private String uri;

    @Parameter(names = { "-dir", "-d" }, description = "Root directory for sources.", required = false)
    private String sourceDir;

    @Parameter(names = { "-filelist" }, description = "Path to a file containing a list of files to analyze.",
            required = false)
    private String fileListPath;

    @Parameter(names = { "-format", "-f" }, description = "Report format type.")
    private String format = "text"; // Enhance to support other usage

    @Parameter(names = { "-debug", "-verbose", "-D", "-V" }, description = "Debug mode.")
    private boolean debug = false;

    @Parameter(names = { "-help", "-h", "-H" }, description = "Display help on usage.", help = true)
    private boolean help = false;

    @Parameter(names = { "-encoding", "-e" },
            description = "Specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8).")
    private String encoding = "UTF-8";

    @Parameter(names = { "-threads", "-t" }, description = "Sets the number of threads used by PMD.",
            validateWith = PositiveInteger.class)
    private Integer threads = 1;

    @Parameter(names = { "-benchmark", "-b" },
            description = "Benchmark mode - output a benchmark report upon completion; default to System.err.")
    private boolean benchmark = false;

    @Parameter(names = { "-stress", "-S" }, description = "Performs a stress test.")
    private boolean stress = false;

    @Parameter(names = "-shortnames", description = "Prints shortened filenames in the report.")
    private boolean shortnames = false;

    @Parameter(names = "-showsuppressed", description = "Report should show suppressed rule violations.")
    private boolean showsuppressed = false;

    @Parameter(names = "-suppressmarker",
            description = "Specifies the string that marks the a line which PMD should ignore; default is NOPMD.")
    private String suppressmarker = "NOPMD";

    @Parameter(names = { "-minimumpriority", "-min" },
            description = "Rule priority threshold; rules with lower priority than configured here won't be used. Default is '5' which is the lowest priority.",
            converter = RulePriorityConverter.class)
    private RulePriority minimumPriority = RulePriority.LOW;

    @Parameter(names = { "-property", "-P" }, description = "{name}={value}: Define a property for the report format.",
            converter = PropertyConverter.class)
    private List<Properties> properties = new ArrayList<>();

    @Parameter(names = { "-reportfile", "-r" }, description = "Sends report output to a file; default to System.out.")
    private String reportfile = null;

    @Parameter(names = { "-version", "-v" }, description = "Specify version of a language PMD should use.")
    private String version = null;

    @Parameter(names = { "-language", "-l" }, description = "Specify a language PMD should use.")
    private String language = null;

    @Parameter(names = "-auxclasspath",
            description = "Specifies the classpath for libraries used by the source code. This is used by the type resolution. Alternatively, a 'file://' URL to a text file containing path elements on consecutive lines can be specified.")
    private String auxclasspath;

    @Parameter(names = { "-failOnViolation", "--failOnViolation" }, arity = 1,
            description = "By default PMD exits with status 4 if violations are found. Disable this option with '-failOnViolation false' to exit with 0 instead and just write the report.")
    private boolean failOnViolation = true;

    @Parameter(names = "-norulesetcompatibility",
            description = "Disable the ruleset compatibility filter. The filter is active by default and tries automatically 'fix' old ruleset files with old rule names")
    private boolean noRuleSetCompatibility = false;

    @Parameter(names = "-cache", description = "Specify the location of the cache file for incremental analysis.")
    private String cacheLocation = null;

    //    @Parameter(names = "-fix", description = "Attempt to automatically fix rule violations found on source files.")
    private boolean autoFixes = false;

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
    public static class RulePriorityConverter implements IStringConverter<RulePriority> {

        public int validate(String value) throws ParameterException {
            int minPriorityValue = Integer.parseInt(value);
            if (minPriorityValue < 1 || minPriorityValue > 5) {
                throw new ParameterException(
                        "Priority values can only be integer value, between 1 and 5," + value + " is not valid");
            }
            return minPriorityValue;
        }

        @Override
        public RulePriority convert(String value) {
            return RulePriority.valueOf(validate(value));
        }
    }

    public static PMDConfiguration transformParametersIntoConfiguration(PMDParameters params) {
        if (null == params.getSourceDir() && null == params.getUri() && null == params.getFileListPath()) {
            throw new IllegalArgumentException(
                    "Please provide a parameter for source root directory (-dir or -d), database URI (-uri or -u), or file list path (-filelist).");
        }
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths(params.getSourceDir());
        configuration.setInputFilePath(params.getFileListPath());
        configuration.setInputUri(params.getUri());
        configuration.setReportFormat(params.getFormat());
        configuration.setBenchmark(params.isBenchmark());
        configuration.setDebug(params.isDebug());
        configuration.setMinimumPriority(params.getMinimumPriority());
        configuration.setReportFile(params.getReportfile());
        configuration.setReportProperties(params.getProperties());
        configuration.setReportShortNames(params.isShortnames());
        configuration.setRuleSets(params.getRulesets());
        configuration.setRuleSetFactoryCompatibilityEnabled(!params.noRuleSetCompatibility);
        configuration.setShowSuppressedViolations(params.isShowsuppressed());
        configuration.setSourceEncoding(params.getEncoding());
        configuration.setStressTest(params.isStress());
        configuration.setSuppressMarker(params.getSuppressmarker());
        configuration.setThreads(params.getThreads());
        configuration.setFailOnViolation(params.isFailOnViolation());
        configuration.setAnalysisCacheLocation(params.cacheLocation);
        configuration.setAutoFixes(params.isAutoFixes());

        LanguageVersion languageVersion = LanguageRegistry
                .findLanguageVersionByTerseName(params.getLanguage() + " " + params.getVersion());
        if (languageVersion != null) {
            configuration.getLanguageVersionDiscoverer().setDefaultLanguageVersion(languageVersion);
        }
        try {
            configuration.prependClasspath(params.getAuxclasspath());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid auxiliary classpath: " + e.getMessage(), e);
        }
        return configuration;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isHelp() {
        return help;
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

    public boolean isShowsuppressed() {
        return showsuppressed;
    }

    public String getSuppressmarker() {
        return suppressmarker;
    }

    public RulePriority getMinimumPriority() {
        return minimumPriority;
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

    public String getVersion() {
        if (version != null) {
            return version;
        }
        return LanguageRegistry.findLanguageByTerseName(getLanguage()).getDefaultVersion().getVersion();
    }

    public String getLanguage() {
        return language != null ? language : LanguageRegistry.getDefaultLanguage().getTerseName();
    }

    public String getAuxclasspath() {
        return auxclasspath;
    }

    public String getRulesets() {
        return rulesets;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public String getFileListPath() {
        return fileListPath;
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

    public boolean isAutoFixes() {
        return autoFixes;
    }
}
