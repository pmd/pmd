/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

/**
 * Renderer to simple text format.
 */
public class TextRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "text";

    public TextRenderer() {
        super(NAME, "Text format.");
    }

    @Override
    public String defaultFileExtension() {
        return "txt";
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        StringBuilder buf = new StringBuilder();

        while (violations.hasNext()) {
            buf.setLength(0);
            RuleViolation rv = violations.next();
            buf.append(determineFileName(rv.getFilename()));
            buf.append(':').append(Integer.toString(rv.getBeginLine()));
            buf.append(":\t").append(rv.getDescription()).append(PMD.EOL);
            writer.write(buf.toString());
        }
    }

    @Override
    public void end() throws IOException {
        StringBuilder buf = new StringBuilder(500);

        for (Report.ProcessingError error : errors) {
            buf.setLength(0);
            buf.append(determineFileName(error.getFile()));
            buf.append("\t-\t").append(error.getMsg()).append(PMD.EOL);
            writer.write(buf.toString());
        }

        for (Report.SuppressedViolation excluded : suppressed) {
            buf.setLength(0);
            buf.append(excluded.getRuleViolation().getRule().getName());
            buf.append(" rule violation suppressed by ");
            buf.append(excluded.suppressedByNOPMD() ? "//NOPMD" : "Annotation");
            buf.append(" in ").append(determineFileName(excluded.getRuleViolation().getFilename())).append(PMD.EOL);
            writer.write(buf.toString());
        }

        for (Report.ConfigurationError error : configErrors) {
            buf.setLength(0);
            buf.append(error.rule().getName());
            buf.append("\t-\t").append(error.issue()).append(PMD.EOL);
            writer.write(buf.toString());
        }
    }

}
