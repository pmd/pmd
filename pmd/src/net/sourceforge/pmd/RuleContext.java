/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.Map;

public class RuleContext {

    private Report report = new Report();
    private File sourceCodeFile;
    private String sourceCodeFilename;
    private SourceType sourceType;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public File getSourceCodeFile() {
        return sourceCodeFile;
    }

    public void setSourceCodeFile(File sourceCodeFile) {
        this.sourceCodeFile = sourceCodeFile;
    }

    public String getSourceCodeFilename() {
        return sourceCodeFilename;
    }

    public void setSourceCodeFilename(String filename) {
        this.sourceCodeFilename = filename;
    }

    public void excludeLines(Map<Integer, String> lines) {
        report.exclude(lines);
    }

    public SourceType getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(SourceType t) {
        this.sourceType = t;
    }
}
