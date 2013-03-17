/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import net.sourceforge.pmd.util.log.AntLogHandler;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

import org.apache.commons.io.IOUtils;
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
	private Path auxClasspath;
	private final List<Formatter> formatters = new ArrayList<Formatter>();
	private final List<FileSet> filesets = new ArrayList<FileSet>();
	private final PMDConfiguration configuration = new PMDConfiguration();
	private boolean failOnError;
	private boolean failOnRuleViolation;
	private int maxRuleViolations = 0;
	private String failuresPropertyName;
	private final Collection<RuleSetWrapper> nestedRules = new ArrayList<RuleSetWrapper>();

	public void setShortFilenames(boolean reportShortNames) {
		configuration.setReportShortNames(reportShortNames);
	}

	public void setSuppressMarker(String suppressMarker) {
		configuration.setSuppressMarker(suppressMarker);
	}

	public void setFailOnError(boolean fail) {
		this.failOnError = fail;
	}

	public void setFailOnRuleViolation(boolean fail) {
		this.failOnRuleViolation = fail;
	}

	public void setMaxRuleViolations(int max) {
		if (max >= 0) {
			this.maxRuleViolations = max;
			this.failOnRuleViolation = true;
		}
	}

	public void setRuleSetFiles(String ruleSets) {
		configuration.setRuleSets(ruleSets);
	}

	public void setEncoding(String sourceEncoding) {
		configuration.setSourceEncoding(sourceEncoding);
	}

	public void setThreads(int threads) {
		configuration.setThreads(threads);
	}

	public void setFailuresPropertyName(String failuresPropertyName) {
		this.failuresPropertyName = failuresPropertyName;
	}

	public void setMinimumPriority(int minPriority) {
		configuration.setMinimumPriority(RulePriority.valueOf(minPriority));
	}

	public void addFileset(FileSet set) {
		filesets.add(set);
	}

	public void addFormatter(Formatter f) {
		formatters.add(f);
	}

	public void addConfiguredSourceLanguage(SourceLanguage version) {
		LanguageVersion languageVersion = LanguageVersion.findVersionsForLanguageTerseName(version.getName(), version.getVersion());
		if (languageVersion == null) {
			throw new BuildException("The following language is not supported:" + version + ".");
		}
		configuration.setDefaultLanguageVersion(languageVersion);
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
		createClasspath().setRefid(r);
	}

	public void setAuxClasspath(Path auxClasspath) {
		this.auxClasspath = auxClasspath;
	}

	public Path getAuxClasspath() {
		return auxClasspath;
	}

	public Path createAuxClasspath() {
		if (auxClasspath == null) {
			auxClasspath = new Path(getProject());
		}
		return auxClasspath.createPath();
	}

	public void setAuxClasspathRef(Reference r) {
		createAuxClasspath().setRefid(r);
	}

	private void doTask() {
		setupClassLoader();

		// Setup RuleSetFactory and validate RuleSets
		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		ruleSetFactory.setClassLoader(configuration.getClassLoader());
		try {
			// This is just used to validate and display rules. Each thread will create its own ruleset
			ruleSetFactory.setMinimumPriority(configuration.getMinimumPriority());
			ruleSetFactory.setWarnDeprecated(true);
			String ruleSets = configuration.getRuleSets();
			if (StringUtil.isNotEmpty(ruleSets)) {
				// Substitute env variables/properties
				configuration.setRuleSets(getProject().replaceProperties(ruleSets));
			}
			RuleSets rules = ruleSetFactory.createRuleSets(configuration.getRuleSets());
			ruleSetFactory.setWarnDeprecated(false);
			logRulesUsed(rules);
		} catch (RuleSetNotFoundException e) {
			throw new BuildException(e.getMessage(), e);
		}

		if (configuration.getSuppressMarker() != null) {
			log("Setting suppress marker to be " + configuration.getSuppressMarker(), Project.MSG_VERBOSE);
		}

		// Start the Formatters
		for (Formatter formatter : formatters) {
			log("Sending a report to " + formatter, Project.MSG_VERBOSE);
			formatter.start(getProject().getBaseDir().toString());
		}

		//log("Setting Language Version " + languageVersion.getShortName(), Project.MSG_VERBOSE);

		// TODO Do we really need all this in a loop over each FileSet?  Seems like a lot of redundancy
		RuleContext ctx = new RuleContext();
		Report errorReport = new Report();
		final AtomicInteger reportSize = new AtomicInteger();
		final String separator = System.getProperty("file.separator");

		for (FileSet fs : filesets) {
			List<DataSource> files = new LinkedList<DataSource>();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] srcFiles = ds.getIncludedFiles();
			for (String srcFile : srcFiles) {
				File file = new File(ds.getBasedir() + separator + srcFile);
				files.add(new FileDataSource(file));
			}

			final String inputPaths = ds.getBasedir().getPath();
			configuration.setInputPaths(inputPaths);

			Renderer logRenderer = new AbstractRenderer("log", "Logging renderer") {
				public void start() {
					// Nothing to do
				}

				public void startFileAnalysis(DataSource dataSource) {
					log("Processing file " + dataSource.getNiceFileName(false, inputPaths), Project.MSG_VERBOSE);
				}

				public void renderFileReport(Report r) {
					int size = r.size();
					if (size > 0) {
						reportSize.addAndGet(size);
					}
				}

				public void end() {
					// Nothing to do
				}

				public String defaultFileExtension() { return null;	}	// not relevant
			};
			List<Renderer> renderers = new LinkedList<Renderer>();
			renderers.add(logRenderer);
			for (Formatter formatter : formatters) {
				renderers.add(formatter.getRenderer());
			}
			try {
				PMD.processFiles(configuration, ruleSetFactory, files, ctx, renderers);
			} catch (RuntimeException pmde) {
				handleError(ctx, errorReport, pmde);
			}
		}

		int problemCount = reportSize.get();
		log(problemCount + " problems found", Project.MSG_VERBOSE);

		for (Formatter formatter : formatters) {
			formatter.end(errorReport);
		}

		if (failuresPropertyName != null && problemCount > 0) {
			getProject().setProperty(failuresPropertyName, String.valueOf(problemCount));
			log("Setting property " + failuresPropertyName + " to " + problemCount, Project.MSG_VERBOSE);
		}

		if (failOnRuleViolation && problemCount > maxRuleViolations) {
			throw new BuildException("Stopping build since PMD found " + problemCount + " rule violations in the code");
		}
	}

	private void handleError(RuleContext ctx, Report errorReport, RuntimeException pmde) {
		
		pmde.printStackTrace();
		log(pmde.toString(), Project.MSG_VERBOSE);
		
		Throwable cause = pmde.getCause();
		
		if (cause != null) {
			StringWriter strWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(strWriter);
			cause.printStackTrace(printWriter);
			log(strWriter.toString(), Project.MSG_VERBOSE);
			IOUtils.closeQuietly(printWriter);
			
			if (StringUtil.isNotEmpty(cause.getMessage())) {
				log(cause.getMessage(), Project.MSG_VERBOSE);
			}
		}
		
		if (failOnError) {
			throw new BuildException(pmde);
		}
		errorReport.addError(new Report.ProcessingError(pmde.getMessage(), ctx.getSourceCodeFilename()));
	}

	private void setupClassLoader() {

		if (classpath == null) {
			log("Using the normal ClassLoader", Project.MSG_VERBOSE);
		} else {
			log("Using the AntClassLoader", Project.MSG_VERBOSE);
			// must be true, otherwise you'll get ClassCastExceptions as classes are loaded twice
			// and exist in multiple class loaders
			boolean parentFirst = true;
			configuration.setClassLoader(
			        new AntClassLoader(Thread.currentThread().getContextClassLoader(), getProject(),
			                classpath, parentFirst));
		}
		try {
			/*
			 * 'basedir' is added to the path to make sure that relative paths
			 * such as "<ruleset>resources/custom_ruleset.xml</ruleset>" still
			 * work when ant is invoked from a different directory using "-f"
			 */
			configuration.prependClasspath(getProject().getBaseDir().toString());
			if (auxClasspath != null) {
				log("Using auxclasspath: " + auxClasspath, Project.MSG_VERBOSE);
				configuration.prependClasspath(auxClasspath.toString());
			}
		} catch (IOException ioe) {
			throw new BuildException(ioe.getMessage(), ioe);
		}
	}

	@Override
	public void execute() throws BuildException {
		validate();
		final Handler antLogHandler = new AntLogHandler(this);
		final ScopedLogHandlersManager logManager = new ScopedLogHandlersManager(Level.FINEST, antLogHandler);
		try {
			doTask();
		} finally {
			logManager.close();
		}
	}

	private void logRulesUsed(RuleSets rules) {
		log("Using these rulesets: " + configuration.getRuleSets(), Project.MSG_VERBOSE);

		RuleSet[] ruleSets = rules.getAllRuleSets();
		for (RuleSet ruleSet : ruleSets) {
			for (Rule rule : ruleSet.getRules()) {
				log("Using rule " + rule.getName(), Project.MSG_VERBOSE);
			}
		}
	}

	private void validate() throws BuildException {
		if (formatters.isEmpty()) {
			Formatter defaultFormatter = new Formatter();
			defaultFormatter.setType("text");
			defaultFormatter.setToConsole(true);
			formatters.add(defaultFormatter);
		} else {
			for (Formatter f : formatters) {
				if (f.isNoOutputSupplied()) {
					throw new BuildException("toFile or toConsole needs to be specified in Formatter");
				}
			}
		}

		if (configuration.getRuleSets() == null) {
			if (nestedRules.isEmpty()) {
				throw new BuildException("No rulesets specified");
			}
			configuration.setRuleSets(getNestedRuleSetFiles());
		}
	}

	private String getNestedRuleSetFiles() {
		final StringBuilder sb = new StringBuilder();
		for (Iterator<RuleSetWrapper> it = nestedRules.iterator(); it.hasNext();) {
			RuleSetWrapper rs = it.next();
			sb.append(rs.getFile());
			if (it.hasNext()) {
				sb.append(',');
			}
		}
		return sb.toString();
	}

	public void addRuleset(RuleSetWrapper r) {
		nestedRules.add(r);
	}

}
