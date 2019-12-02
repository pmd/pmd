/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import net.sourceforge.pmd.scm.SCMConfiguration;

import com.beust.jcommander.Parameter;

/**
 * Checks that the compiler printed the specified message to its stdout or stderr during execution.
 */
public class PrintedMessageInvariant extends AbstractInvariant {
    public static final class Configuration extends AbstractConfiguration {
        @Parameter(names = "--printed-message", description = "Message that should be printed by the compiler", required = true)
        private String message;

        @Parameter(names = "--printed-message-charset", description = "Charset of compiler output",
                converter = SCMConfiguration.CharsetConverter.class)
        private Charset charset = Charset.defaultCharset();

        public String getMessage() {
            return message;
        }

        public Charset getCharset() {
            return charset;
        }

        @Override
        public PrintedMessageInvariant createChecker() {
            return new PrintedMessageInvariant(this);
        }
    }

    public static final InvariantConfigurationFactory FACTORY = new AbstractFactory("message") {
        @Override
        public InvariantConfiguration createConfiguration() {
            return new Configuration();
        }
    };

    private final String message;
    private final Charset charset;

    private PrintedMessageInvariant(Configuration configuration) {
        super(configuration);
        message = configuration.message;
        charset = configuration.charset;
    }

    @Override
    protected boolean testSatisfied(ProcessBuilder pb) throws Exception {
        Process process = pb.redirectErrorStream(true).start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    return false;
                }
                if (line.contains(message)) {
                    process.destroy();
                    return true;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Printed: '" + message + "'";
    }
}
