/*
 * User: tom
 * Date: Jun 26, 2002
 * Time: 4:30:03 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.reports.Report;

public class RuleContext {

    private Report report;
    private String filename;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


}
