/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.pmd.RuleViolation;

/**
 * Renderer to GNU Emacs parsable format.
 */
public class EmacsRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "emacs";

    protected static final String EOL = System.getProperty("line.separator", "\n");

    public EmacsRenderer() {
        super(NAME, "GNU Emacs integration.");
    }

    @Override
    public String defaultFileExtension() {
        return "emacs";
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        StringBuilder buf = new StringBuilder();
        while (violations.hasNext()) {
            RuleViolation rv = violations.next();
            buf.setLength(0);
            buf.append(determineFileName(rv.getFilename()));
            buf.append(':').append(Integer.toString(rv.getBeginLine()));
            buf.append(": ").append(rv.getDescription()).append(EOL);
            writer.write(buf.toString());
        }
    }
}
