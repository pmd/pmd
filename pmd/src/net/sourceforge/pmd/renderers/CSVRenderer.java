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
        buf.append(quote("Description"));
        buf.append(PMD.EOL);

        int violationCount = 1;
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(quoteAndCommify(Integer.toString(violationCount)));
            buf.append(quoteAndCommify(rv.getFilename()));
            buf.append(quoteAndCommify(Integer.toString(rv.getLine())));
            buf.append(quote(StringUtil.replaceString(rv.getDescription(), '\"', "'")));
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
