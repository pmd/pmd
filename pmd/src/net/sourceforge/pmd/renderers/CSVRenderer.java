/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Renderer to comma separated format.
 */
public class CSVRenderer extends OnTheFlyRenderer {

    public static final String NAME = "csv";

    private int violationCount = 1;

    public CSVRenderer(Properties properties) {
	super(NAME, "Comma-separated values tabular format.", properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
	StringBuffer buf = new StringBuffer(300);
	quoteAndCommify(buf, "Problem");
	quoteAndCommify(buf, "Package");
	quoteAndCommify(buf, "File");
	quoteAndCommify(buf, "Priority");
	quoteAndCommify(buf, "Line");
	quoteAndCommify(buf, "Description");
	quoteAndCommify(buf, "Rule set");
	quote(buf, "Rule");
	buf.append(PMD.EOL);
	getWriter().write(buf.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
	StringBuffer buf = new StringBuffer(300);
	Writer writer = getWriter();

	RuleViolation rv;
	while (violations.hasNext()) {
	    buf.setLength(0);
	    rv = violations.next();
	    quoteAndCommify(buf, Integer.toString(violationCount));
	    quoteAndCommify(buf, rv.getPackageName());
	    quoteAndCommify(buf, rv.getFilename());
	    quoteAndCommify(buf, Integer.toString(rv.getRule().getPriority().getPriority()));
	    quoteAndCommify(buf, Integer.toString(rv.getBeginLine()));
	    quoteAndCommify(buf, StringUtil.replaceString(rv.getDescription(), '\"', "'"));
	    quoteAndCommify(buf, rv.getRule().getRuleSetName());
	    quote(buf, rv.getRule().getName());
	    buf.append(PMD.EOL);
	    writer.write(buf.toString());
	    violationCount++;
	}
    }

    private void quote(StringBuffer buffer, String s) {
	buffer.append('"').append(s).append('"');
    }

    private void quoteAndCommify(StringBuffer buffer, String s) {
	quote(buffer, s);
	buffer.append(',');
    }
}
