/*
 * User: tom
 * Date: Jun 26, 2002
 * Time: 4:30:03 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.symboltable.SymbolTable;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public void setSymbolTableBuilder(SymbolFacade table) {
        this.builder = table;
    }

    public SymbolFacade getSymbolTableBuilder() {
        return this.builder;
    }


}
