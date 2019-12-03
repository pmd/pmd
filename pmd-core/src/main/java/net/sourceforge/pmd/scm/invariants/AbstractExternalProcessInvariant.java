/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

import java.io.BufferedReader;

import org.apache.commons.lang3.SystemUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

import com.beust.jcommander.Parameter;

/**
 * Abstract implementation of invariant checkers that run some external compiler process.
 */
public abstract class AbstractExternalProcessInvariant implements Invariant {
    protected abstract static class AbstractConfiguration implements InvariantConfiguration {
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
    private String[] commandArgs;

    private static String[] createCommandLine(AbstractConfiguration configuration) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new String[] { "cmd.exe", "/C", configuration.compilerCommandLine };
        } else {
            return new String[] { "/bin/sh", "-c", configuration.compilerCommandLine };
        }
    }

    protected AbstractExternalProcessInvariant(AbstractConfiguration configuration) {
        commandArgs = createCommandLine(configuration);
    }

    @Override
    public void initialize(InvariantOperations ops, Node rootNode) {
        this.ops = ops;
    }

    protected abstract boolean testSatisfied(ProcessBuilder pb) throws Exception;

    @Override
    public boolean checkIsSatisfied() throws Exception {
        try (BufferedReader reader = ops.getScratchReader()) {
            try {
                Node root = ops.getCurrentParser().parse("", reader);
                if (root == null) {
                    return false;
                }
            } catch (ParseException ex) {
                return false;
            }
        }
        return testSatisfied(new ProcessBuilder().command(commandArgs));
    }
}
