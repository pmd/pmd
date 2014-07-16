/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import java.io.IOException;
import java.util.Properties;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;

public class PMDParameters {

    @Parameter(names = { "-rulesets", "-R" }, description = "comma separated list of rulesets name to use", required = true)
    private String rulesets;

    @Parameter(names = { "-uri", "-u" }, description = "Database URI for sources", required = false)
    private String uri;

    @Parameter(names = { "-dir", "-d" }, description = "root directory for sources", required = false)
    private String sourceDir;

    @Parameter(names = { "-format", "-f" }, description = "report format type")
    private String format = "text"; // Enhance to support other usage

    @Parameter(names = { "-debug", "-verbose", "-D", "-V" }, description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = { "-help", "-h", "-H" }, description = "Display help on usage", help = true)
    private boolean help = false;

    @Parameter(names = { "-encoding", "-e" }, description = "specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8)")
    private String encoding = "UTF-8";

    @Parameter(names = { "-threads", "-t" }, description = "set the number of threads used by PMD", validateWith = PositiveInteger.class)
    private Integer threads = 1;

    @Parameter(names = { "-benchmark", "-b" }, description = "Benchmark mode - output a benchmark report upon completion; default to System.err")
    private boolean benchmark = false;

    @Parameter(names = { "-stress", "-S" }, description = "performs a stress test")
    private boolean stress = false;

    @Parameter(names = "-shortnames", description = "prints shortened filenames in the report")
    private boolean shortnames = false;

    @Parameter(names = "-showsuppressed", description = "report should show suppressed rule violations")
    private boolean showsuppressed = false;

    @Parameter(names = "-suppressmarker", description = "specifies the String that marks the a line which PMD should ignore; default is NOPMD")
    private String suppressmarker = "NOPMD";

    @Parameter(names = { "-minimumpriority", "-min" }, description = "rule priority threshold; rules with lower priority than they will not be used", converter = RulePriorityConverter.class)
    private RulePriority minimumPriority = RulePriority.LOW;

    @Parameter(names = { "-property", "-P" }, description = "{name}={value}: define a property for the report", converter = PropertyConverter.class)
    private Properties properties = new Properties();

    @Parameter(names = { "-reportfile", "-r" }, description = "send report output to a file; default to System.out")
    private String reportfile = null;

    @Parameter(names = { "-version", "-v" }, description = "specify version of a language PMD should use")
    private String version = Language.getDefaultLanguage().getDefaultVersion().getVersion();

    @Parameter(names = { "-language", "-l" }, description = "specify a language PMD should use")
    private String language = Language.getDefaultLanguage().getTerseName();

    @Parameter(names = "-auxclasspath", description = "specifies the classpath for libraries used by the source code. This is used by the type resolution. Alternatively, a 'file://' URL to a text file containing path elements on consecutive lines can be specified.")
    private String auxclasspath;

    // this has to be a public static class, so that JCommander can use it!
    public static class PropertyConverter implements IStringConverter<Properties> {

        private static final char separator = '=';

        public Properties convert(String value) {
            Properties properties = new Properties();
            int indexOfSeparator = value.indexOf(separator);
            if (indexOfSeparator < 0)
                throw new ParameterException(
                        "Property name must be separated with an = sign from it value: name=value.");
            String propertyName = value.substring(0, indexOfSeparator);
            String propertyValue = value.substring(indexOfSeparator + 1);
            properties.put(propertyName, propertyValue);
            return properties;
        }
    }

    // this has to be a public static class, so that JCommander can use it!
    public static class RulePriorityConverter implements IStringConverter<RulePriority> {

        public int validate(String value) throws ParameterException {
            int minPriorityValue = Integer.parseInt(value);
            if (minPriorityValue < 0 || minPriorityValue > 5)
                throw new ParameterException("Priority values can only be integer value, between 0 and 5," + value
                        + " is not valid");
            return minPriorityValue;
        }

        public RulePriority convert(String value) {
            return RulePriority.valueOf(validate(value));
        }
    }

    public static PMDConfiguration transformParametersIntoConfiguration(PMDParameters params) {
        if (null == params.getSourceDir() && null == params.getUri()) {
            throw new IllegalArgumentException(
                    "Please provide either source root directory (-dir or -d) or database URI (-uri or -u) parameter");
        }
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths(params.getSourceDir());
        configuration.setInputUri(params.getUri());
        configuration.setReportFormat(params.getFormat());
        configuration.setBenchmark(params.isBenchmark());
        configuration.setDebug(params.isDebug());
        configuration.setMinimumPriority(params.getMinimumPriority());
        configuration.setReportFile(params.getReportfile());
        configuration.setReportProperties(params.getProperties());
        configuration.setReportShortNames(params.isShortnames());
        configuration.setRuleSets(params.getRulesets());
        configuration.setShowSuppressedViolations(params.isShowsuppressed());
        configuration.setSourceEncoding(params.getEncoding());
        configuration.setStressTest(params.isStress());
        configuration.setSuppressMarker(params.getSuppressmarker());
        configuration.setThreads(params.getThreads());
        for (LanguageVersion language : LanguageVersion.findVersionsForLanguageTerseName(params.getLanguage())) {

            LanguageVersion languageVersion = language.getLanguage().getVersion(params.getVersion());
            if (languageVersion == null) {
                languageVersion = language.getLanguage().getDefaultVersion();
            }
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
        return properties;
    }

    public String getReportfile() {
        return reportfile;
    }

    public String getVersion() {
        return version;
    }

    public String getLanguage() {
        return language;
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

    public String getFormat() {
        return format;
    }

    /**
     * @return the uri alternative to source directory.
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri specifying the source directory.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

}
