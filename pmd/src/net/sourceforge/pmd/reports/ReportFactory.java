/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:14:31 PM
 */
package net.sourceforge.pmd.reports;

public class ReportFactory {
    public Report createReport(String reportType) {
        if (reportType.equals("xml")) {
            return new XMLReport();
        } else if (reportType.equals("html")) {
            return new HTMLReport();
        }
        throw new RuntimeException("Unknown report type " + reportType);
    }
}
