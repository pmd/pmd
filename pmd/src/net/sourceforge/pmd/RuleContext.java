/*
 * User: tom
 * Date: Jun 26, 2002
 * Time: 4:30:03 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.symboltable.SymbolFacade;

public class RuleContext {

    private Report report;
    private String sourceCodeFilename;
    private SymbolFacade builder;

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
}
