package com.infoether.pmd;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import java.util.*;
import java.io.*;

import com.infoether.pmd.ast.JavaParser;
import com.infoether.pmd.ast.JavaParserVisitor;
import com.infoether.pmd.ast.ParseException;
import com.infoether.pmd.ast.ASTCompilationUnit;

public class PMDTask extends Task {

    private List filesets  = new ArrayList();
    private String reportFile;
    private boolean verbose;
    private String ruleSetType;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setRuleSetType(String ruleSetType) {
        this.ruleSetType = ruleSetType;
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }
    
    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }
    
    public void execute() throws BuildException {
        if (reportFile == null) {
            throw new BuildException("No report file specified");
        }
        if (ruleSetType == null) {
            throw new BuildException("No rule set type specified");
        }
        StringBuffer buf = new StringBuffer();
        for (Iterator i = filesets.iterator(); i.hasNext();) {
            FileSet fs = (FileSet) i.next();
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            String[] srcFiles = ds.getIncludedFiles();
            for (int j=0; j<srcFiles.length; j++) {
                File file = new File(ds.getBasedir() + System.getProperty("file.separator") + srcFiles[j]);
                if (verbose) System.out.println(file.getAbsoluteFile());
                PMD pmd = new PMD();
                Report report = pmd.processFile(file, ruleSetType);
                if (!report.empty()) {
                    buf.append(report.renderToText());
                    buf.append(System.getProperty("line.separator"));
                }
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(reportFile)));
            writer.write(buf.toString(), 0, buf.length());
            writer.close();
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage());
        }
    }
}
