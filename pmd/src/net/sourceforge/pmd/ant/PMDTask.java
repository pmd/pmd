package net.sourceforge.pmd.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import java.util.*;
import java.io.*;

import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.JavaParserVisitor;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.reports.Report;
import net.sourceforge.pmd.RuleContext;

public class PMDTask extends Task {

    private List filesets  = new ArrayList();
    private String reportFile;
    private boolean verbose;
    private String ruleSetFile;
    private String format;
    private boolean failOnError;

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setRuleSetFile(String ruleSetFile) {
        this.ruleSetFile = ruleSetFile;
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }
    
    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void execute() throws BuildException {
        if (reportFile == null) {
            throw new BuildException("No report file specified");
        }

        if (format == null || (!format.equals("xml") && !format.equals("html"))) {
            throw new BuildException("Report format must be either 'xml', or 'html'; you specified " + format);
        }

        PMD pmd = new PMD();
        Report report = new Report(format);
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);

        for (Iterator i = filesets.iterator(); i.hasNext();) {
            FileSet fs = (FileSet) i.next();
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            String[] srcFiles = ds.getIncludedFiles();
            for (int j=0; j<srcFiles.length; j++) {
                try {
                    File file = new File(ds.getBasedir() + System.getProperty("file.separator") + srcFiles[j]);
                    if (verbose) System.out.println(file.getAbsoluteFile());
                    ctx.setFilename(file.getAbsolutePath());
                    pmd.processFile(file, ruleSetFile, ctx);
                } catch (FileNotFoundException fnfe) {
                    throw new BuildException(fnfe);
                }
            }
        }

        StringBuffer buf = new StringBuffer();
        if (!report.isEmpty()) {
            buf.append(ctx.getReport().render());
            buf.append(System.getProperty("line.separator"));
            if (failOnError) {
                throw new BuildException("Report contains problems");
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
