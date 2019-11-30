/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

import com.beust.jcommander.Parameter;

/**
 * Abstract implementation for invariant checkers that run some external compiler process
 */
public abstract class AbstractInvariant implements Invariant {
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

    private String[] commandArgs;

    private static String[] parseArgs(String commandLine) {
        return commandLine.split(" "); // TODO improve
    }

    protected AbstractInvariant(AbstractConfiguration configuration) {
        commandArgs = parseArgs(configuration.compilerCommandLine);
    }

    ProcessBuilder getProcessBuilder() {
        return new ProcessBuilder().command(commandArgs);
    }
}
