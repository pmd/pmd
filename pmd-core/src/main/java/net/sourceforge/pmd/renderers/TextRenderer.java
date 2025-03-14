/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * Renderer to simple text format.
 */
public class TextRenderer extends AbstractIncrementingRenderer {

    private static final char SMALL_SEPARATOR = ':';
    private static final String MEDIUM_SEPARATOR = ":\t";
    private static final String LARGE_SEPARATOR = "\t-\t";

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
            buf.append(determineFileName(rv.getFileId()));
            buf.append(SMALL_SEPARATOR).append(rv.getBeginLine());
            buf.append(MEDIUM_SEPARATOR).append(rv.getRule().getName());
            buf.append(MEDIUM_SEPARATOR).append(rv.getDescription());
            writer.println(buf);
        }
    }

    @Override
    public void end() throws IOException {
        StringBuilder buf = new StringBuilder(500);

        for (Report.ProcessingError error : errors) {
            buf.setLength(0);
            buf.append(determineFileName(error.getFileId()));
            buf.append(LARGE_SEPARATOR).append(error.getMsg());
            writer.println(buf);
        }

        for (Report.SuppressedViolation excluded : suppressed) {
            buf.setLength(0);
            buf.append(excluded.getRuleViolation().getRule().getName())
               .append(" rule violation suppressed by ")
               .append(excluded.getSuppressor().getId())
               .append(" in ")
                .append(determineFileName(excluded.getRuleViolation().getFileId()));
            writer.println(buf);
        }

        for (Report.ConfigurationError error : configErrors) {
            buf.setLength(0);
            buf.append(error.rule().getName());
            buf.append(LARGE_SEPARATOR).append(error.issue());
            writer.println(buf);
        }
    }

}
