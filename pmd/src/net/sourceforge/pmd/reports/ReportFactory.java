/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:14:31 PM
 */
package net.sourceforge.pmd.reports;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class ReportFactory {

    private Set types = new HashSet();

    public ReportFactory() {
        types.add("xml");
        types.add("html");
    }

    public boolean contains(String candidate) {
        return types.contains(candidate);
    }

    public String getConcatenatedString() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = types.iterator(); i.hasNext();) {
            if (buf.length() != 0) {
                buf.append(",");
            }
            buf.append("'" + i.next() + "'");
        }
        return buf.toString();
    }

    public Report createReport(String reportType) {
        if (!types.contains(reportType)) {
            throw new RuntimeException("Unknown report type " + reportType);
        }
        if (reportType.equals("xml")) {
            return new XMLReport();
        }
        return new HTMLReport();
    }
}
