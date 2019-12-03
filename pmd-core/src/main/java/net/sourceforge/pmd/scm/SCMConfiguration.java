/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.nio.charset.Charset;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.scm.invariants.InvariantConfiguration;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategyConfiguration;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Configuration object for Source Code Minimizer
 */
public class SCMConfiguration extends AbstractConfiguration {
    private MinimizationStrategyConfiguration strategyConfiguration;
    private InvariantConfiguration invariantConfiguration;

    @Parameter(names = "--input-file", description = "Original file that should be minimized", required = true)
    private String inputFileName;

    @Parameter(names = "--output-file", description = "Output file (used as a scratch file, too)", required = true)
    private String outputFileName;

    @Parameter(names = "--charset", description = "Charset of the source file to be minimized",
            converter = CharsetConverter.class)
    private Charset sourceCharset = Charset.defaultCharset();

    @Parameter(names = "--language", description = "Source code language",
            required = true, converter = LanguageConverter.class)
    private Language language;

    @Parameter(names = "--language-version", description = "Specific language version")
    private String languageVersion;

    @Parameter(names = "--strategy", description = "Minimization strategy", required = true)
    private String strategy;

    @Parameter(names = "--invariant", description = "Invariant to preserve during minimization")
    private String invariantChecker = "dummy";

    @Parameter(names = "--help", description = "Display help", help = true)
    private boolean help;

    private String errorString;

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public Charset getSourceCharset() {
        return sourceCharset;
    }

    Language getLanguageHandler() {
        return language;
    }

    public String getLanguageVersion() {
        return languageVersion == null ? language.getDefaultLanguageVersion() : languageVersion;
    }

    boolean isHelpRequested() {
        return help;
    }

    String getErrorString() {
        return errorString;
    }

    private static class LanguageConverter implements IStringConverter<Language> {
        @Override
        public Language convert(String value) {
            return MinimizerLanguageFactory.INSTANCE.getLanguage(value);
        }
    }

    /**
     * Process SCM global options.
     */
    private void firstPass(String[] args) {
        JCommander jcommander = new JCommander(this);
        jcommander.setProgramName(SCM.PROGRAM_NAME);
        jcommander.setAcceptUnknownOptions(true);
        jcommander.parse(args);

        strategyConfiguration = language.createStrategyConfiguration(strategy);
        invariantConfiguration = language.createInvariantConfiguration(invariantChecker);

        if (strategyConfiguration == null) {
            throw new ParameterException("Unknown strategy: " + strategy);
        }
        if (invariantConfiguration == null) {
            throw new ParameterException("Unknown invariant: " + invariantChecker);
        }
    }

    /**
     * Process invariant- and strategy-specific options.
     */
    private void secondPass(String[] args) {
        Object[] configs = { this, strategyConfiguration, invariantConfiguration};
        JCommander jcommander = new JCommander(configs);
        jcommander.setProgramName(SCM.PROGRAM_NAME);
        jcommander.parse(args);
    }

    private void componentUsage(Object configuration, StringBuilder sb) {
        JCommander jcommander = new JCommander(configuration);
        jcommander.setProgramName("");
        jcommander.usage(sb);
    }

    String getHelpString() {
        Language parsedLanguage = language;
        language = null;
        StringBuilder sb = new StringBuilder();

        // put global options
        JCommander jcommander = new JCommander(this);
        jcommander.setProgramName(SCM.PROGRAM_NAME);
        jcommander.usage(sb);

        // list available languages
        sb.append("Available languages: ");
        sb.append(MinimizerLanguageFactory.INSTANCE.getSupportedLanguagesWithVersions());
        sb.append('\n');

        // list options of all invariants and strategies available for the specified language, if any
        language = parsedLanguage;
        if (language != null) {
            sb.append("=== Parameters specific to language: ").append(language.getTerseName()).append('\n');
            for (String strategyName: language.getStrategyNames()) {
                sb.append("--- Parameters specific to strategy: ").append(strategyName).append('\n');
                componentUsage(language.createStrategyConfiguration(strategyName), sb);
            }
            for (String invariantName: language.getInvariantNames()) {
                sb.append("--- Parameters specific to invariant: ").append(invariantName).append('\n');
                componentUsage(language.createInvariantConfiguration(invariantName), sb);
            }
        }
        return sb.toString();
    }

    /**
     * Parse the command line arguments into this object
     *
     * @return Whether parsing was successful or not
     */
    boolean parse(String[] args) {
        try {
            firstPass(args);
            secondPass(args);
        } catch (ParameterException ex) {
            errorString = ex.getMessage();
            return false;
        }
        return true;
    }

    MinimizationStrategyConfiguration getStrategyConfig() {
        return strategyConfiguration;
    }

    InvariantConfiguration getInvariantCheckerConfig() {
        return invariantConfiguration;
    }

    public static final class CharsetConverter implements IStringConverter<Charset> {
        @Override
        public Charset convert(String value) {
            return Charset.forName(value);
        }
    }
}
