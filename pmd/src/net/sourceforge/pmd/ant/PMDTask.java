package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
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
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PMDTask extends Task {

    public static class Formatter {
        private Renderer renderer;
        private String toFile;
        public void setType(String type) {
            if (type.equals("xml")) {
                renderer = new XMLRenderer();
            } else if (type.equals("html")) {
                renderer = new HTMLRenderer();
            } else {
                throw new BuildException("Formatter type must be either 'xml' or 'html'; you specified " + type);
            }
        }
        public void setToFile(String toFile) {this.toFile = toFile;}
        public Renderer getRenderer() {return renderer;}
        public String getToFile() {return toFile;}
    }

    public static class Database {
	private String url = "";
	private String user = "";
	private String driver = "";
	private String password = "";
	private int projectId = 0;


	public void setUrl(String url) {
	    this.url = url;
	}

	public String getUrl() {
	    return url;
	}

	public void setUser(String user) {
	    this.user = user;
	}
	
	public String getUser() {
	    return user;
	}

	public void setPassword(String password) {
	    this.password = password;
	}
	
	public String getPassword() {
	    return password;
	}

	public void setProjectId(int projectId) {
	    this.projectId = projectId;
	}
	
	public int getProjectId() {
	    return projectId;
	}

	public void setDriver(String driver) {
	    this.driver = driver;
	}

	public String getDriver() {
	    return driver;
	}
    }

    private List formatters = new ArrayList();
    private List filesets  = new ArrayList();
    private List databases = new ArrayList();

    private boolean shortFilenames;
    private boolean verbose;
    private boolean printToConsole;
    private String ruleSetFiles;
    private boolean failOnError;

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    public void setShortFilenames(boolean value) {
        this.shortFilenames = value;
    }

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
    
    public void addFormatter(Formatter f) {
        formatters.add(f);
    }

    public void addDatabase(Database d) {
	databases.add(d);
    }

    public void execute() throws BuildException {
        if (formatters.isEmpty()) {
            throw new BuildException("No formatter specified");
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
                    printIfVerbose (file.getAbsoluteFile().toString());

                    String fileName = file.getAbsolutePath();
                    if (shortFilenames) {
                        fileName = srcFiles[j];
                    }
                    ctx.setSourceCodeFilename(fileName);

                    pmd.processFile(new FileInputStream(file), rules, ctx);
                } catch (FileNotFoundException fnfe) {
                    throw new BuildException(fnfe);
                } catch (PMDException pmde) {
                    throw new BuildException(pmde);
                }
            }
        }

        if (!ctx.getReport().isEmpty()) {
            for (Iterator i = formatters.iterator(); i.hasNext();) {
                Formatter formatter = (Formatter)i.next();
                String buffer = formatter.getRenderer().render(ctx.getReport()) + EOL;
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(project.getBaseDir() + System.getProperty("file.separator") + formatter.getToFile())));
                    writer.write(buffer, 0, buffer.length());
                    writer.close();
                } catch (IOException ioe) {
                    throw new BuildException(ioe.getMessage());
                }
            }
        }

        if (!ctx.getReport().isEmpty() && printToConsole) {
            Renderer r = new TextRenderer();
            System.out.println(r.render(report));
        }

        if (failOnError && !ctx.getReport().isEmpty()) {
            throw new BuildException("Stopping build since PMD found problems in the code");
        }
    }

    private void printIfVerbose(String in) {
        if (verbose) System.out.println(in);
    }
}
