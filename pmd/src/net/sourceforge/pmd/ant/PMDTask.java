package net.sourceforge.pmd.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.*;

import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.JavaParserVisitor;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.*;

public class PMDTask extends Task {

    private List filesets  = new ArrayList();
    private String reportFile;
    private boolean verbose;
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
            rules = createRuleSets();
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

    private RuleSet createRuleSets() throws RuleSetNotFoundException {
        RuleSetFactory ruleSetFactory = new RuleSetFactory();

        if (ruleSetFiles.indexOf(',') == -1) {
           return ruleSetFactory.createRuleSet(ruleSetFiles);
        }

        RuleSet ruleSet = new RuleSet();
        for (StringTokenizer st = new StringTokenizer(ruleSetFiles, ","); st.hasMoreTokens();) {
            String ruleSetName = st.nextToken();
            RuleSet tmpRuleSet = ruleSetFactory.createRuleSet(ruleSetName);
            if (verbose) System.out.println("Adding " + tmpRuleSet.size() + " rules from ruleset " + ruleSetName);
            ruleSet.addRuleSet(tmpRuleSet);
        }
        return ruleSet;
    }

}
