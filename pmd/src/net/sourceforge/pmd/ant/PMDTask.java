/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PMDTask extends Task {

    private Path classpath;
    private List formatters = new ArrayList();
    private List filesets = new ArrayList();
    private boolean shortFilenames;
    private boolean printToConsole;
    private String ruleSetFiles;
    private String encoding = System.getProperty("file.encoding");
    private boolean failOnError;
    private boolean failOnRuleViolation;
    private boolean targetJDK13;
    private String failuresPropertyName;

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    public void setShortFilenames(boolean value) {
        this.shortFilenames = value;
    }

    public void setTargetJDK13(boolean value) {
        this.targetJDK13 = value;
    }

    public void setFailOnError(boolean fail) {
        this.failOnError = fail;
    }

    public void setFailOnRuleViolation(boolean fail) {
        this.failOnRuleViolation = fail;
    }

    public void setPrintToConsole(boolean printToConsole) {
        this.printToConsole = printToConsole;
    }

    public void setRuleSetFiles(String ruleSetFiles) {
        this.ruleSetFiles = ruleSetFiles;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setFailuresPropertyName(String failuresPropertyName) {
        this.failuresPropertyName = failuresPropertyName;
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    public void addFormatter(Formatter f) {
        formatters.add(f);
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public Path getClasspath() {
        return classpath;
    }

    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        createLongClasspath().setRefid(r);
    }

    public void execute() throws BuildException {
        validate();

        RuleSet rules;
        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            if (classpath == null) {
                log("Using the normal ClassLoader", Project.MSG_VERBOSE);
                rules = ruleSetFactory.createRuleSet(ruleSetFiles);
            } else {
                log("Using the AntClassLoader", Project.MSG_VERBOSE);
                rules = ruleSetFactory.createRuleSet(ruleSetFiles, new AntClassLoader(getProject(), classpath));
            }
        } catch (RuleSetNotFoundException e) {
            throw new BuildException(e.getMessage());
        }

        logRulesUsed(rules);

        PMD pmd;
        if (targetJDK13) {
            pmd = new PMD(new TargetJDK1_3());
        } else {
            pmd = new PMD();
        }

        RuleContext ctx = new RuleContext();
        ctx.setReport(new Report());
        for (Iterator i = filesets.iterator(); i.hasNext();) {
            FileSet fs = (FileSet) i.next();
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] srcFiles = ds.getIncludedFiles();
            for (int j = 0; j < srcFiles.length; j++) {
                File file = new File(ds.getBasedir() + System.getProperty("file.separator") + srcFiles[j]);
                log("Processing file " + file.getAbsoluteFile().toString(), Project.MSG_VERBOSE);
                ctx.setSourceCodeFilename(shortFilenames ? srcFiles[j] : file.getAbsolutePath());
                try {
                    pmd.processFile(new FileInputStream(file), encoding, rules, ctx);
                } catch (FileNotFoundException fnfe) {
                    if (failOnError) {
                        throw new BuildException(fnfe);
                    }
                } catch (PMDException pmde) {
                    log(pmde.toString(), Project.MSG_VERBOSE);
                    if (pmde.getReason() != null && pmde.getReason().getMessage() != null) {
                        log(pmde.getReason().getMessage(), Project.MSG_VERBOSE);
                    }
                    if (failOnError) {
                        throw new BuildException(pmde);
                    }
                    ctx.getReport().addError(new Report.ProcessingError(pmde.getMessage(), ctx.getSourceCodeFilename()));
                }
            }
        }

        log(ctx.getReport().size() + " problems found", Project.MSG_VERBOSE);

        if (!ctx.getReport().isEmpty()) {
            for (Iterator i = formatters.iterator(); i.hasNext();) {
                Formatter formatter = (Formatter) i.next();
                log("Sending a report to " + formatter, Project.MSG_VERBOSE);
                String buffer = formatter.getRenderer().render(ctx.getReport()) + EOL;
                try {
                    Writer writer = formatter.getToFileWriter(getProject().getBaseDir().toString());
                    writer.write(buffer, 0, buffer.length());
                    writer.close();
                } catch (IOException ioe) {
                    throw new BuildException(ioe.getMessage());
                }
            }

            if (failuresPropertyName != null && ctx.getReport().size() > 0) {
                getProject().setProperty(failuresPropertyName, String.valueOf(ctx.getReport().size()));
                log("Setting property " + failuresPropertyName + " to " + String.valueOf(ctx.getReport().size()), Project.MSG_VERBOSE);
            }

            if (printToConsole) {
                Renderer r = new TextRenderer();
                log(r.render(ctx.getReport()), Project.MSG_INFO);
            }

            if (failOnRuleViolation) {
                throw new BuildException("Stopping build since PMD found " + ctx.getReport().size() + " rule violations in the code");
            }
        }
    }

    private void logRulesUsed(RuleSet rules) {
        log("Using these rulesets: " + ruleSetFiles, Project.MSG_VERBOSE);
        for (Iterator i = rules.getRules().iterator();i.hasNext();) {
            Rule rule = (Rule)i.next();
            log("Using rule " + rule.getName(), Project.MSG_VERBOSE);
        }
    }

    private void validate() throws BuildException {
        if (formatters.isEmpty() && !printToConsole) {
            throw new BuildException("No formatter specified; and printToConsole was false");
        }

        for (Iterator i = formatters.iterator(); i.hasNext();) {
            Formatter f = (Formatter) i.next();
            if (f.isToFileNull()) {
                throw new BuildException("Formatter toFile attribute is required");
            }
        }

        if (ruleSetFiles == null) {
            throw new BuildException("No rulesets specified");
        }
    }

    private Path createLongClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

}
