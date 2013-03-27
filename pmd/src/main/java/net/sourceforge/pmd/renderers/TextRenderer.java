/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
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

    
    public String defaultFileExtension() { return "txt"; }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
		Writer writer = getWriter();
		StringBuilder buf = new StringBuilder();
	
		while (violations.hasNext()) {
		    buf.setLength(0);
		    RuleViolation rv = violations.next();
		    buf.append(rv.getFilename());
		    buf.append(':').append(Integer.toString(rv.getBeginLine()));
		    buf.append(":\t").append(rv.getDescription()).append(PMD.EOL);
		    writer.write(buf.toString());
		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() throws IOException {
		Writer writer = getWriter();
		StringBuilder buf = new StringBuilder(500);
		if (!errors.isEmpty()) {
	
		    for (Report.ProcessingError error : errors) {
			buf.setLength(0);
			buf.append(error.getFile());
			buf.append("\t-\t").append(error.getMsg()).append(PMD.EOL);
			writer.write(buf.toString());
		    }
		}
	
		for (Report.SuppressedViolation excluded : suppressed) {
		    buf.setLength(0);
		    buf.append(excluded.getRuleViolation().getRule().getName());
		    buf.append(" rule violation suppressed by ");
		    buf.append(excluded.suppressedByNOPMD() ? "//NOPMD" : "Annotation");
		    buf.append(" in ").append(excluded.getRuleViolation().getFilename()).append(PMD.EOL);
		    writer.write(buf.toString());
		}
    }

}
