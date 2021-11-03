/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

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

    PmdParametersParseResult(PMDParameters result) {
        this.result = Objects.requireNonNull(result);
        this.error = null;
    }

    PmdParametersParseResult(ParameterException error) {
        this.result = null;
        this.error = Objects.requireNonNull(error);
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
        return result.isVersion();
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
     * Returns the resulting configuration if parsing succeeded and not {@link #isHelp().
     * Otherwise returns null.
     */
    public PMDConfiguration toConfiguration() {
        return result != null && !isHelp() ? result.toConfiguration() : null;
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
            return new PmdParametersParseResult(result);
        } catch (ParameterException e) {
            return new PmdParametersParseResult(e);
        }
    }
}
