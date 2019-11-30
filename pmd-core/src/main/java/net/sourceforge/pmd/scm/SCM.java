/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

public final class SCM {
    private SCM() {
    }

    static final String PROGRAM_NAME = "scm";

    public static void main(String[] args) throws Exception {
        SCMConfiguration configuration = new SCMConfiguration();
        boolean success = configuration.parse(args);
        if (!success || configuration.isHelpRequested()) {
            System.out.println(configuration.getHelpString());
        }
        if (!success) {
            System.err.println(configuration.getErrorString());
            System.exit(1);
        }
        if (configuration.isHelpRequested()) {
            System.exit(0);
        }

        SourceCodeMinimizer minimizer = new SourceCodeMinimizer(configuration);
        minimizer.runMinimization();
    }
}
