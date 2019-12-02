/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

import com.beust.jcommander.Parameter;

/**
 * Checks that compiler exits with code from the specified range.
 */
public class ExitCodeInvariant extends AbstractInvariant {
    public static final class Configuration extends AbstractConfiguration {
        @Parameter(names = "--min-return", description = "Minimum exit code value (inclusive)")
        private int min = 1;

        @Parameter(names = "--max-return", description = "Maximum exit code value (inclusive)")
        private int max = Integer.MAX_VALUE;

        @Parameter(names = "--exact-return", description = "Compiler should exit with this specific exit value only (implies min == max)")
        private int exact = -1;

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public int getExact() {
            return exact;
        }

        @Override
        public Invariant createChecker() {
            return new ExitCodeInvariant(this);
        }
    }

    public static final InvariantConfigurationFactory FACTORY = new AbstractFactory("exitcode") {
        @Override
        public InvariantConfiguration createConfiguration() {
            return new Configuration();
        }
    };

    private final int min;
    private final int max;

    private ExitCodeInvariant(Configuration configuration) {
        super(configuration);

        if (configuration.exact >= 0) {
            min = configuration.exact;
            max = configuration.exact;
        } else {
            min = configuration.min;
            max = configuration.max;
        }
    }

    @Override
    protected boolean testSatisfied(ProcessBuilder pb) throws Exception {
        Process process = pb.start();

        int returnCode = process.waitFor();

        return min <= returnCode && returnCode <= max;
    }

    @Override
    public String toString() {
        if (min == max) {
            return "Exits with code = " + min;
        } else if (max == Integer.MAX_VALUE) {
            return "Exits with code >= " + min;
        } else {
            return "Exits with code from " + min + " to " + max + ", inclusive";
        }
    }
}
