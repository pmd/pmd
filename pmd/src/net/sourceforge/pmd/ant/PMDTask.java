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
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;
import net.sourceforge.pmd.SourceType;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PMDTask extends Task {

    private Path classpath;
    private List formatters = new ArrayList();
    private List filesets = new ArrayList();
    private int minPriority = Rule.LOWEST_PRIORITY;
    private boolean shortFilenames;
    private String ruleSetFiles;
    private String encoding = System.getProperty("file.encoding");
    private boolean failOnError;
    private boolean failOnRuleViolation;
    private String targetJDK = "1.4";
    private String failuresPropertyName;
    private String excludeMarker;
    private final Collection nestedRules = new ArrayList();

    public void setShortFilenames(boolean value) {
        this.shortFilenames = value;
    }

    public void setTargetJDK(String value) {
        this.targetJDK = value;
    }

    public void setExcludeMarker(String value) {
        this.excludeMarker = value;
    }

    public void setFailOnError(boolean fail) {
        this.failOnError = fail;
    }

    public void setFailOnRuleViolation(boolean fail) {
        this.failOnRuleViolation = fail;
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

    public void setMinimumPriority(int minPriority) {
        this.minPriority = minPriority;
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

        ruleSetFiles = new SimpleRuleSetNameMapper(ruleSetFiles).getRuleSets();
        RuleSets rules;
        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            ruleSetFactory.setMinimumPriority(minPriority);
            if (classpath == null) {
                log("Using the normal ClassLoader", Project.MSG_VERBOSE);
                rules = ruleSetFactory.createRuleSets(ruleSetFiles);
            } else {
                log("Using the AntClassLoader", Project.MSG_VERBOSE);
                rules = ruleSetFactory.createRuleSets(ruleSetFiles, new AntClassLoader(getProject(), classpath));
            }
        } catch (RuleSetNotFoundException e) {
            throw new BuildException(e.getMessage());
        }
        logRulesUsed(rules);

        PMD pmd;
        if (targetJDK.equals("1.3")) {
            log("Targeting Java language version 1.3", Project.MSG_VERBOSE);
            pmd = new PMD();
            pmd.setJavaVersion(SourceType.JAVA_13);
        } else if (targetJDK.equals("1.5")) {
            log("Targeting Java language version 1.5", Project.MSG_VERBOSE);
            pmd = new PMD();
            pmd.setJavaVersion(SourceType.JAVA_15);
        } else if (targetJDK.equals("1.6")) {
            log("Targeting Java language version 1.6", Project.MSG_VERBOSE);
            pmd = new PMD();
            pmd.setJavaVersion(SourceType.JAVA_16);
        } else if(targetJDK.equals("jsp")){
            log("Targeting JSP", Project.MSG_VERBOSE);
            pmd = new PMD();
            pmd.setJavaVersion(SourceType.JSP);
        } else {
            log("Targeting Java language version 1.4", Project.MSG_VERBOSE);
            pmd = new PMD();
        }

        if (excludeMarker != null) {
            log("Setting exclude marker to be " + excludeMarker, Project.MSG_VERBOSE);
            pmd.setExcludeMarker(excludeMarker);
        }

        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        report.start();
        for (Iterator i = filesets.iterator(); i.hasNext();) {
            FileSet fs = (FileSet) i.next();
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] srcFiles = ds.getIncludedFiles();
            for (int j = 0; j < srcFiles.length; j++) {
                File file = new File(ds.getBasedir() + System.getProperty("file.separator") + srcFiles[j]);
                log("Processing file " + file.getAbsoluteFile().toString(), Project.MSG_VERBOSE);
                ctx.setSourceCodeFilename(shortFilenames ? srcFiles[j] : file.getAbsolutePath());
                try {
                    pmd.processFile(new BufferedInputStream(new FileInputStream(file)), encoding, rules, ctx);
                } catch (FileNotFoundException fnfe) {
                    if (failOnError) {
                        throw new BuildException(fnfe);
                    }
                } catch (PMDException pmde) {
                    log(pmde.toString(), Project.MSG_VERBOSE);
                    if (pmde.getReason() != null) {
                        StringWriter strWriter = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(strWriter);
                        pmde.getReason().printStackTrace(printWriter);
                        log(strWriter.toString(), Project.MSG_VERBOSE);
                    }
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
        report.end();

        log(ctx.getReport().size() + " problems found", Project.MSG_VERBOSE);

        for (Iterator i = formatters.iterator(); i.hasNext();) {
            Formatter formatter = (Formatter) i.next();
            log("Sending a report to " + formatter, Project.MSG_VERBOSE);
            formatter.outputReport(ctx.getReport(), getProject().getBaseDir().toString());
        }

        if (failuresPropertyName != null && ctx.getReport().size() > 0) {
            getProject().setProperty(failuresPropertyName, String.valueOf(ctx.getReport().size()));
            log("Setting property " + failuresPropertyName + " to " + ctx.getReport().size(), Project.MSG_VERBOSE);
        }

        if (failOnRuleViolation && ctx.getReport().size() > 0) {
            throw new BuildException("Stopping build since PMD found " + ctx.getReport().size() + " rule violations in the code");
        }
    }

    private void logRulesUsed(net.sourceforge.pmd.RuleSets rules) {
        log("Using these rulesets: " + ruleSetFiles, Project.MSG_VERBOSE);

        RuleSet[] ruleSets = rules.getAllRuleSets();
        for (int j = 0; j < ruleSets.length; j++) {
            RuleSet ruleSet = ruleSets[j];

            for (Iterator i = ruleSet.getRules().iterator(); i.hasNext();) {
                Rule rule = (Rule) i.next();
                log("Using rule " + rule.getName(), Project.MSG_VERBOSE);
            }
        }
    }

    private void validate() throws BuildException {
        // TODO - check for empty Formatters List here?
        for (Iterator i = formatters.iterator(); i.hasNext();) {
            Formatter f = (Formatter) i.next();
            if (f.isNoOutputSupplied()) {
                throw new BuildException("toFile or toConsole needs to be specified in Formatter");
            }
        }

        if (ruleSetFiles == null) {
            if (nestedRules.isEmpty()) {
                throw new BuildException("No rulesets specified");
            }
            ruleSetFiles = getNestedRuleSetFiles();
        }

        if (!targetJDK.equals("1.3") && !targetJDK.equals("1.4") && !targetJDK.equals("1.5") && !targetJDK.equals("1.6") && !targetJDK.equals("jsp")) {
            throw new BuildException("The targetjdk attribute, if used, must be set to either '1.3', '1.4', '1.5', '1.6' or 'jsp'");
        }
    }

    private String getNestedRuleSetFiles() {
        final StringBuffer sb = new StringBuffer();
        for (Iterator it = nestedRules.iterator(); it.hasNext();) {
            RuleSetWrapper rs = (RuleSetWrapper) it.next();
            sb.append(rs.getFile());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    private Path createLongClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

    public void addRuleset(RuleSetWrapper r) {
        nestedRules.add(r);
    }

}

