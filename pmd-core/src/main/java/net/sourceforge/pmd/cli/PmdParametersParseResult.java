/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

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
     * Returns the resulting configuration if parsing succeeded and neither {@link #isHelp()} nor {@link #isVersion()} is requested.
     * Otherwise returns null.
     */
    public @Nullable PMDConfiguration toConfiguration() {
        return isValidParameterSet() ? result.toConfiguration() : null;
    }

    private boolean isValidParameterSet() {
        return result != null && !isHelp() && !isVersion();
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
            parseAndValidate(jcommander, result, args);
            return new PmdParametersParseResult(result, filterDeprecatedOptions(args));
        } catch (ParameterException e) {
            return new PmdParametersParseResult(e, filterDeprecatedOptions(args));
        }
    }

    private static void parseAndValidate(JCommander jcommander, PMDParameters result, String[] args) {
        jcommander.parse(args);
        if (result.isHelp() || result.isVersion()) {
            return;
        }
        // jcommander has no special support for global parameter validation like this
        // For consistency we report this with a ParameterException
        if (result.getInputPaths().isEmpty()
            && null == result.getUri()
            && null == result.getFileListPath()) {
            throw new ParameterException(
                "Please provide a parameter for source root directory (--dir or -d), database URI (--uri or -u), or file list path (--file-list).");
        }

    }

    private static Map<String, String> filterDeprecatedOptions(String... args) {
        Map<String, String> argSet = new LinkedHashMap<>(SUGGESTED_REPLACEMENT);
        argSet.keySet().retainAll(new HashSet<>(Arrays.asList(args)));
        return Collections.unmodifiableMap(argSet);
    }

    /** Map of deprecated option to suggested replacement. */
    private static final Map<String, String> SUGGESTED_REPLACEMENT;


    static {
        Map<String, String> m = new LinkedHashMap<>();

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
        m.put("-shortnames", PMDParameters.RELATIVIZE_PATHS_WITH);
        m.put("--short-names", PMDParameters.RELATIVIZE_PATHS_WITH);
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
        m.put("-v", "--use-version"); // In PMD 7, -v will enable verbose mode
        m.put("-V", "--verbose"); // In PMD 7, -V will show the tool version
        m.put("-min", "--minimum-priority");
        m.put("-version", "--use-version");
        m.put("-language", "--use-version");
        m.put("-l", "--use-version");

        SUGGESTED_REPLACEMENT = Collections.unmodifiableMap(m);
    }
}
