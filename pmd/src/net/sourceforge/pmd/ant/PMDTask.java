package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PMDTask extends Task {

    private List filesets  = new ArrayList();
    private String reportFile;
    private boolean verbose;
    private boolean printToConsole;
    private String ruleSetFiles;
    private String format;
    private boolean failOnError;

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setPrintToConsole(boolean printToConsole) {
        this.printToConsole = printToConsole;
    }

    public void setRuleSetFiles(String ruleSetFiles) {
        this.ruleSetFiles = ruleSetFiles;
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
            throw new BuildException("Renderer format must be either 'xml' or 'html'; you specified " + format);
        }

        RuleSet rules = null;
        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            rules = ruleSetFactory.createRuleSet(ruleSetFiles);
        } catch (RuleSetNotFoundException rsnfe) {
            throw new BuildException(rsnfe.getMessage());
        }

        PMD pmd = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);

        for (Iterator i = filesets.iterator(); i.hasNext();) {
            FileSet fs = (FileSet) i.next();
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            String[] srcFiles = ds.getIncludedFiles();
            for (int j=0; j<srcFiles.length; j++) {
                try {
                    File file = new File(ds.getBasedir() + System.getProperty("file.separator") + srcFiles[j]);
                    if (verbose) System.out.println(file.getAbsoluteFile());

                    ctx.setSourceCodeFilename(file.getAbsolutePath());

                    pmd.processFile(new FileInputStream(file), rules, ctx);
                } catch (FileNotFoundException fnfe) {
                    throw new BuildException(fnfe);
                }
            }
        }

        StringBuffer buf = new StringBuffer();
        if (!ctx.getReport().isEmpty()) {
            Renderer rend = null;
            if (format.equals("xml")) {
                rend = new XMLRenderer();
						} else {
                rend = new HTMLRenderer();
            }
            buf.append(rend.render(ctx.getReport()));
            buf.append(EOL);
						if (printToConsole) {
							Renderer r = new TextRenderer();
							System.out.println(r.render(report));
						}
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(reportFile)));
            writer.write(buf.toString(), 0, buf.length());
            writer.close();
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage());
        }

        if (failOnError && !ctx.getReport().isEmpty()) {
            throw new BuildException("Stopping build since PMD found problems in the code");
        }
    }
}
