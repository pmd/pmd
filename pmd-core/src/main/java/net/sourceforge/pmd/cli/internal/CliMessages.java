/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

/**
 * @author Clément Fournier
 */
public final class CliMessages {

    private CliMessages() {
        // utility class
    }

    public static String errorDetectedMessage(int errors, String program) {
        String anError = errors == 1 ? "An error" : errors + " errors";
        return anError + " occurred while executing " + program + ".\n"
            + "Run with --debug to see a stack-trace.\n"
            + "If you think this is a bug in " + program
            + ", please report this issue at https://github.com/pmd/pmd/issues/new/choose\n"
            + "If you do so, please include a stack-trace, the code sample\n"
            + " causing the issue, and details about your run configuration.";
    }

    public static String runWithHelpFlagMessage() {
        return "Run with --help for command line help.";
    }
}
