/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.StringUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class CSVRenderer extends OnTheFlyRenderer {

    private int violationCount = 1;

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

    public void renderFileViolations(Iterator<IRuleViolation> violations) throws IOException {
        StringBuffer buf = new StringBuffer(300);
        Writer writer = getWriter();

		IRuleViolation rv;
        while (violations.hasNext()) {
            buf.setLength(0);
            rv = violations.next();
            quoteAndCommify(buf, Integer.toString(violationCount));
            quoteAndCommify(buf, rv.getPackageName());
            quoteAndCommify(buf, rv.getFilename());
            quoteAndCommify(buf, Integer.toString(rv.getRule().getPriority()));
            quoteAndCommify(buf, Integer.toString(rv.getBeginLine()));
            quoteAndCommify(buf, StringUtil.replaceString(rv.getDescription(), '\"', "'"));
            quoteAndCommify(buf, rv.getRule().getRuleSetName());
            quote(buf, rv.getRule().getName());
            buf.append(PMD.EOL);
            writer.write(buf.toString());
            violationCount++;
        }
	}

    public void end() throws IOException {
    }

    private void quote(StringBuffer sb, String d) {
        sb.append('"').append(d).append('"');
    }

    private void quoteAndCommify(StringBuffer sb, String d) {
    	quote(sb, d);
        sb.append(',');
    }
}
