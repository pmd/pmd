/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

import java.io.BufferedReader;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

import com.beust.jcommander.Parameter;

/**
 * Abstract implementation for invariant checkers that run some external compiler process
 */
public abstract class AbstractInvariant implements Invariant {
    protected abstract static class AbstractConfiguration implements InvariantConfiguration {
        @Parameter(names = "--no-require-parseable", description = "Try to test invariant even if cannot parse trimmed source")
        private boolean dontRequireParseableInput;

        @Parameter(names = "--command-line",
                description = "Command line for running a compiler on a source to be minimized",
                required = true)
        private String compilerCommandLine;
    }

    protected abstract static class AbstractFactory implements InvariantConfigurationFactory {
        String name;

        AbstractFactory(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private InvariantOperations ops;
    private boolean parseBeforeChecking;
    private String[] commandArgs;

    private static String[] parseArgs(String commandLine) {
        return commandLine.split(" "); // TODO improve
    }

    protected AbstractInvariant(AbstractConfiguration configuration) {
        parseBeforeChecking = !configuration.dontRequireParseableInput;
        commandArgs = parseArgs(configuration.compilerCommandLine);
    }

    @Override
    public void initialize(InvariantOperations ops, Node rootNode) {
        this.ops = ops;
    }

    protected abstract boolean testSatisfied(ProcessBuilder pb) throws Exception;

    @Override
    public boolean checkIsSatisfied() throws Exception {
        if (parseBeforeChecking) {
            try (BufferedReader reader = ops.getScratchReader()) {
                try {
                    Node root = ops.getParser().parse("", reader);
                    if (root == null) {
                        return false;
                    }
                } catch (ParseException ex) {
                    return false;
                }
            }
        }
        return testSatisfied(new ProcessBuilder().command(commandArgs));
    }
}
