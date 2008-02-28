/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;

import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.FileDataSource;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.ScopedLogHandlersManager;
import net.sourceforge.pmd.util.AntLogHandler;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class PMDTask extends Task {

    private Path classpath;
    private List<Formatter> formatters = new ArrayList<Formatter>();
    private List<FileSet> filesets = new ArrayList<FileSet>();
    private int minPriority = Rule.LOWEST_PRIORITY;
    private boolean shortFilenames;
    private String ruleSetFiles;
    private String encoding = System.getProperty("file.encoding");
    private boolean failOnError;
    private boolean failOnRuleViolation;
    private String targetJDK = "1.5";
    private String failuresPropertyName;
    private String excludeMarker = PMD.EXCLUDE_MARKER;
    private int cpus = Runtime.getRuntime().availableProcessors();
    private final Collection<RuleSetWrapper> nestedRules = new ArrayList<RuleSetWrapper>();

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

    public void setCpus(int cpus) {
        this.cpus = cpus;
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
    
    private void doTask(){
        ruleSetFiles = new SimpleRuleSetNameMapper(ruleSetFiles).getRuleSets();

        RuleSetFactory ruleSetFactory = new RuleSetFactory() {
            public RuleSets createRuleSets(String ruleSetFileNames) throws RuleSetNotFoundException {
                if (classpath == null) {
                    return super.createRuleSets(ruleSetFiles);
                } else {
                    return createRuleSets(ruleSetFiles, new AntClassLoader(getProject(), classpath));

                }
            }
        };
        for (Formatter formatter: formatters) {
            log("Sending a report to " + formatter, Project.MSG_VERBOSE);
            formatter.start(getProject().getBaseDir().toString());
        }

        try {
            // This is just used to validate and display rules. Each thread will create its own ruleset
            RuleSets rules;
            ruleSetFactory.setMinimumPriority(minPriority);
            if (classpath == null) {
                log("Using the normal ClassLoader", Project.MSG_VERBOSE);
                rules = ruleSetFactory.createRuleSets(ruleSetFiles);
            } else {
                log("Using the AntClassLoader", Project.MSG_VERBOSE);
                rules = ruleSetFactory.createRuleSets(ruleSetFiles, new AntClassLoader(getProject(), classpath));
            }
            logRulesUsed(rules);
        } catch (RuleSetNotFoundException e) {
            throw new BuildException(e.getMessage());
        }

        SourceType sourceType;
        if (targetJDK.equals("1.3")) {
            log("Targeting Java language version 1.3", Project.MSG_VERBOSE);
            sourceType = SourceType.JAVA_13;
        } else if (targetJDK.equals("1.5")) {
            log("Targeting Java language version 1.5", Project.MSG_VERBOSE);
            sourceType = SourceType.JAVA_15;
        } else if (targetJDK.equals("1.6")) {
            log("Targeting Java language version 1.6", Project.MSG_VERBOSE);
            sourceType = SourceType.JAVA_16;
        } else if (targetJDK.equals("1.7")) {
            log("Targeting Java language version 1.7", Project.MSG_VERBOSE);
            sourceType = SourceType.JAVA_17;
        } else if(targetJDK.equals("jsp")){
            log("Targeting JSP", Project.MSG_VERBOSE);
            sourceType = SourceType.JSP;
        } else {
            log("Targeting Java language version 1.4", Project.MSG_VERBOSE);
            sourceType = SourceType.JAVA_14;
        }

        if (excludeMarker != null) {
            log("Setting exclude marker to be " + excludeMarker, Project.MSG_VERBOSE);
        }

        RuleContext ctx = new RuleContext();
        Report errorReport = new Report();
        final AtomicInteger reportSize = new AtomicInteger();
        for (FileSet fs: filesets) {
            List<DataSource> files = new LinkedList<DataSource>();
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] srcFiles = ds.getIncludedFiles();
            for (int j = 0; j < srcFiles.length; j++) {
                File file = new File(ds.getBasedir() + System.getProperty("file.separator") + srcFiles[j]);
                files.add(new FileDataSource(file));
            }

            final String inputPath = ds.getBasedir().getPath();

            Renderer logRenderer = new AbstractRenderer() {
                public void start() {}

                public void startFileAnalysis(DataSource dataSource) {
                    log("Processing file " + dataSource.getNiceFileName(false, inputPath), Project.MSG_VERBOSE);
                }

                public void renderFileReport(Report r) {
                    int size = r.size();
                    if (size > 0) {
                        reportSize.addAndGet(size);
                    }
                }

                public void end() {}

                public void render(Writer writer, Report r) {}
            };
            List<Renderer> renderers = new LinkedList<Renderer>();
            renderers.add(logRenderer);
            for (Formatter formatter: formatters) {
                renderers.add(formatter.getRenderer());
            }
            try {
                PMD.processFiles(cpus, ruleSetFactory, sourceType, files, ctx,
                    renderers, ruleSetFiles,
                    shortFilenames, inputPath,
                    encoding, excludeMarker, getClass().getClassLoader());
            } catch (RuntimeException pmde) {
                pmde.printStackTrace();
                log(pmde.toString(), Project.MSG_VERBOSE);
                if (pmde.getCause() != null) {
                    StringWriter strWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(strWriter);
                    pmde.getCause().printStackTrace(printWriter);
                    log(strWriter.toString(), Project.MSG_VERBOSE);
                }
                if (pmde.getCause() != null && pmde.getCause().getMessage() != null) {
                    log(pmde.getCause().getMessage(), Project.MSG_VERBOSE);
                }
                if (failOnError) {
                    throw new BuildException(pmde);
                }
                errorReport.addError(new Report.ProcessingError(pmde.getMessage(), ctx.getSourceCodeFilename()));
            }
        }

        int problemCount = reportSize.get();
        log(problemCount + " problems found", Project.MSG_VERBOSE);

        for (Formatter formatter: formatters) {
            formatter.end(errorReport);
        }

        if (failuresPropertyName != null && problemCount > 0) {
            getProject().setProperty(failuresPropertyName, String.valueOf(problemCount));
            log("Setting property " + failuresPropertyName + " to " + problemCount, Project.MSG_VERBOSE);
        }

        if (failOnRuleViolation && problemCount > 0) {
            throw new BuildException("Stopping build since PMD found " + problemCount + " rule violations in the code");
        }
    }
    
    public void execute() throws BuildException {
        validate();
        final Handler antLogHandler = new AntLogHandler(this);
        final ScopedLogHandlersManager logManager = new ScopedLogHandlersManager(Level.FINEST,antLogHandler);
        try{
            doTask();
        }finally{
            logManager.close();
        }
    }

    private void logRulesUsed(RuleSets rules) {
        log("Using these rulesets: " + ruleSetFiles, Project.MSG_VERBOSE);

        RuleSet[] ruleSets = rules.getAllRuleSets();
        for (int j = 0; j < ruleSets.length; j++) {
            RuleSet ruleSet = ruleSets[j];

            for (Rule rule: ruleSet.getRules()) {
                log("Using rule " + rule.getName(), Project.MSG_VERBOSE);
            }
        }
    }

    private void validate() throws BuildException {
        // TODO - check for empty Formatters List here?
        for (Formatter f: formatters) {
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

        if (!targetJDK.equals("1.3") && !targetJDK.equals("1.4") && !targetJDK.equals("1.5") && !targetJDK.equals("1.6") && !targetJDK.equals("1.7") && !targetJDK.equals("jsp")) {
            throw new BuildException("The targetjdk attribute, if used, must be set to either '1.3', '1.4', '1.5', '1.6', '1.7' or 'jsp'");
        }
    }

    private String getNestedRuleSetFiles() {
        final StringBuffer sb = new StringBuffer();
        for (Iterator<RuleSetWrapper> it = nestedRules.iterator(); it.hasNext();) {
            RuleSetWrapper rs = it.next();
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
