/*
 * User: tom
 * Date: Jun 26, 2002
 * Time: 4:30:03 PM
 */
package net.sourceforge.pmd;

public class RuleContext {

    private Report report;
    private String sourceCodeFilename;
    private String packageName;
    private String className;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getSourceCodeFilename() {
        return sourceCodeFilename;
    }

    public void setSourceCodeFilename(String filename) {
        this.sourceCodeFilename = filename;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
