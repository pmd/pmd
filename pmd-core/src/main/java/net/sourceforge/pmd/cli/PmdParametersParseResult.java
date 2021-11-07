/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.pmd.PMDConfiguration;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Result of parsing a bunch of CLI arguments. Parsing may fail with an
 * exception, or succeed and produce a {@link PMDConfiguration}. If the
 * {@code --help} argument is mentioned, no configuration is produced.
 */
public final class PmdParametersParseResult {

    private final PMDParameters result;
    private final ParameterException error;
    private final Map<String, String> deprecatedOptionsUsed;

    PmdParametersParseResult(PMDParameters result,
                             Map<String, String> deprecatedOptionsUsed) {
        this.result = Objects.requireNonNull(result);
        this.deprecatedOptionsUsed = deprecatedOptionsUsed;
        this.error = null;
    }

    PmdParametersParseResult(ParameterException error, Map<String, String> deprecatedOptionsUsed) {
        this.result = null;
        this.error = Objects.requireNonNull(error);
        this.deprecatedOptionsUsed = deprecatedOptionsUsed;
    }

    /** Returns true if parsing failed. */
    public boolean isError() {
        return result == null;
    }

    /**
     * Returns whether parsing just requested the {@code --help} text.
     * In this case no configuration is produced.
     */
    public boolean isHelp() {
        return !isError() && result.isHelp();
    }

    /**
     * Returns whether parsing just requested the {@code --version} text.
     * In this case no configuration is produced.
     */
    public boolean isVersion() {
        return !isError() && result.isVersion();
    }

    /**
     * Returns the error if parsing failed. Parsing may fail if required
     * parameters are not provided, or if some parameters don't pass validation.
     * Otherwise returns null.
     */
    public ParameterException getError() {
        return error;
    }

    /**
     * Returns a map of deprecated CLI options used by the command that
     * created this instance. Each key is a deprecated option that was used,
     * and the value is a suggested replacement (a piece of English text).
     */
    public Map<String, String> getDeprecatedOptionsUsed() {
        return deprecatedOptionsUsed;
    }

    /**
     * Returns the resulting configuration if parsing succeeded and not {@link #isHelp().
     * Otherwise returns null.
     */
    public PMDConfiguration toConfiguration() {
        return result != null && !isHelp() && !isVersion() ? result.toConfiguration() : null;
    }

    /**
     * Parse an array of CLI parameters and returns a result (which may be failed).
     * Use this instead of {@link PMDCommandLineInterface#extractParameters(PMDParameters, String[], String)},
     * because that one may terminate the VM.
     *
     * @param args Array of parameters
     *
     * @return A parse result
     *
     * @throws NullPointerException If the parameter array is null
     */
    public static PmdParametersParseResult extractParameters(String... args) {
        Objects.requireNonNull(args, "Null parameter array");
        PMDParameters result = new PMDParameters();
        JCommander jcommander = new JCommander(result);
        jcommander.setProgramName("pmd");

        try {
            jcommander.parse(args);
            return new PmdParametersParseResult(result, filterDeprecatedOptions(args));
        } catch (ParameterException e) {
            return new PmdParametersParseResult(e, filterDeprecatedOptions(args));
        }
    }

    private static Map<String, String> filterDeprecatedOptions(String... args) {
        Map<String, String> argSet = new HashMap<>(SUGGESTED_REPLACEMENT);
        argSet.keySet().retainAll(new HashSet<>(Arrays.asList(args)));
        return Collections.unmodifiableMap(argSet);
    }

    /** Map of deprecated option to suggested replacement. */
    private static final Map<String, String> SUGGESTED_REPLACEMENT;

    static {
        Map<String, String> m = new HashMap<>();

        m.put("-rulesets", "--rulesets (or -R)");
        m.put("-uri", "--uri");
        m.put("-dir", "--dir (or -d)");
        m.put("-filelist", "--file-list");
        m.put("-ignorelist", "--ignore-list");
        m.put("-format", "--format (or -f)");
        m.put("-debug", "--debug");
        m.put("-verbose", "--verbose");
        m.put("-help", "--help");
        m.put("-encoding", "--encoding");
        m.put("-threads", "--threads");
        m.put("-benchmark", "--benchmark");
        m.put("-stress", "--stress");
        m.put("-shortnames", "--short-names");
        m.put("-showsuppressed", "--show-suppressed");
        m.put("-suppressmarker", "--suppress-marker");
        m.put("-minimumpriority", "--minimum-priority");
        m.put("-property", "--property");
        m.put("-reportfile", "--report-file");
        m.put("-force-language", "--force-language");
        m.put("-auxclasspath", "--aux-classpath");
        m.put("-failOnViolation", "--fail-on-violation");
        m.put("--failOnViolation", "--fail-on-violation");
        m.put("-norulesetcompatibility", "--no-ruleset-compatibility");
        m.put("-cache", "--cache");
        m.put("-no-cache", "--no-cache");
        SUGGESTED_REPLACEMENT = Collections.unmodifiableMap(m);
    }
}
