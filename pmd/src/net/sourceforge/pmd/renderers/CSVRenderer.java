/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

public class CSVRenderer implements Renderer {
    public String render(Report report) {
        StringBuffer buf = new StringBuffer(quoteAndCommify("Problem"));
        buf.append(quoteAndCommify("File"));
        buf.append(quoteAndCommify("Line"));
        buf.append(quoteAndCommify("Priority"));
        buf.append(quoteAndCommify("Description"));
        buf.append(quoteAndCommify("Rule set"));
        buf.append(quote("Rule"));
        buf.append(PMD.EOL);

        int violationCount = 1;
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(quoteAndCommify(Integer.toString(violationCount)));
            buf.append(quoteAndCommify(rv.getFilename()));
            buf.append(quoteAndCommify(Integer.toString(rv.getRule().getPriority())));
            buf.append(quoteAndCommify(Integer.toString(rv.getLine())));
            buf.append(quoteAndCommify(StringUtil.replaceString(rv.getDescription(), '\"', "'")));
            buf.append(quoteAndCommify(rv.getRule().getRuleSetName()));
            buf.append(quote(rv.getRule().getName()));
            buf.append(PMD.EOL);
            violationCount++;
        }
        return buf.toString();
    }

    private String quote(String d) {
        return "\"" + d + "\"";
    }

    private String quoteAndCommify(String d) {
        return quote(d) + ",";
    }

}
