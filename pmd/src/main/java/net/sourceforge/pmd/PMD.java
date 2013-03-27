/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.benchmark.TextReport;
import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PMDParameters;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.processor.MonoThreadProcessor;
import net.sourceforge.pmd.processor.MultiThreadProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.SystemUtils;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.ConsoleLogHandler;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

/**
 * This is the main class for interacting with PMD. The primary flow of all Rule
 * process is controlled via interactions with this class. A command line
 * interface is supported, as well as a programmatic API for integrating PMD
 * with other software such as IDEs and Ant.
 */
public class PMD {

	private static final Logger LOG = Logger.getLogger(PMD.class.getName());

	public static final String EOL = System.getProperty("line.separator", "\n");
	public static final String SUPPRESS_MARKER = "NOPMD";

	protected final PMDConfiguration configuration;

	private final SourceCodeProcessor rulesetsFileProcessor;

    public static Parser parserFor(LanguageVersion languageVersion, PMDConfiguration configuration) {
    	
    	// TODO Handle Rules having different parser options.
   	 	LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        ParserOptions options = languageVersionHandler.getDefaultParserOptions();
        if (configuration != null) options.setSuppressMarker(configuration.getSuppressMarker());
        return languageVersionHandler.getParser(options);        
   }
	
	/**
	 * Create a report, filter out any defective rules, and keep a record of them.
	 * 
	 * @param rs
	 * @param ctx
	 * @param fileName
	 * @return Report
	 */
	public static Report setupReport(RuleSets rs, RuleContext ctx, String fileName) {
		
		Set<Rule> brokenRules = removeBrokenRules(rs);
		Report report = Report.createReport(ctx, fileName);
		
		for (Rule rule : brokenRules) {
			report.addConfigError(
				new Report.RuleConfigurationError(rule, rule.dysfunctionReason())
				);
		}

		return report;
	}
	
	
    /**
     * Remove and return the misconfigured rules from the rulesets and
     * log them for good measure.
     * 
     * @param ruleSets RuleSets
     * @return Set<Rule>
     */
    private static Set<Rule> removeBrokenRules(RuleSets ruleSets) {
    	
    	Set<Rule> brokenRules = new HashSet<Rule>();
    	ruleSets.removeDysfunctionalRules(brokenRules);
	    
	    for (Rule rule : brokenRules) {
	    	 LOG.log(Level.WARNING, "Removed misconfigured rule: " + rule.getName() + "  cause: " + rule.dysfunctionReason());	
	    }
	    
	    return brokenRules;
    }
    
	/**
	 * Create a PMD instance using a default Configuration. Changes to the
	 * configuration may be required.
	 */
	public PMD() {
		this(new PMDConfiguration());
	}

	/**
	 * Create a PMD instance using the specified Configuration.
	 * 
	 * @param configuration
	 *            The runtime Configuration of PMD to use.
	 */
	public PMD(PMDConfiguration configuration) {
		this.configuration = configuration;
		this.rulesetsFileProcessor = new SourceCodeProcessor(configuration);
	}

	/**
	 * Get the runtime configuration. The configuration can be modified to
	 * affect how PMD behaves.
	 * 
	 * @return The configuration. 
     * @see PMDConfiguration 
	 */
	public PMDConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 *
	 * @return SourceCodeProcessor
	 */
	public SourceCodeProcessor getSourceCodeProcessor() {
		return rulesetsFileProcessor;
	}

	/** 
	 * This method is the main entry point for command line usage.
	 * 
	 * @param configuration
	 */
	public static void doPMD(PMDConfiguration configuration) {

		// Load the RuleSets
		long startLoadRules = System.nanoTime();
		RuleSetFactory ruleSetFactory = RulesetsFactoryUtils
				.getRulesetFactory(configuration);

		RuleSets ruleSets = RulesetsFactoryUtils.getRuleSets(
				configuration.getRuleSets(), ruleSetFactory, startLoadRules);
		if (ruleSets == null)
			return;

		Set<Language> languages = getApplicableLanguages(configuration, ruleSets);
		List<DataSource> files = getApplicableFiles(configuration, languages);

		long reportStart = System.nanoTime();
		try {			
			Renderer renderer = configuration.createRenderer();
			List<Renderer> renderers = new LinkedList<Renderer>();
			renderers.add(renderer);

			renderer.setWriter(IOUtil.createWriter(configuration.getReportFile()));
			renderer.start();

			Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);

			RuleContext ctx = new RuleContext();

			processFiles(configuration, ruleSetFactory, files, ctx, renderers);

			reportStart = System.nanoTime();
			renderer.end();
			renderer.flush();
		} catch (Exception e) {
			String message = e.getMessage();
			if (message != null) {
				LOG.severe(message);
			} else {
				LOG.log(Level.SEVERE, "Exception during processing", e);
			}
			LOG.log(Level.FINE, "Exception during processing", e);
			LOG.info(PMDCommandLineInterface.buildUsageText());
		} finally {
			Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);
		}
	}

	
	
    public static RuleContext newRuleContext(String sourceCodeFilename, File sourceCodeFile) {

		RuleContext context = new RuleContext();
		context.setSourceCodeFile(sourceCodeFile);
		context.setSourceCodeFilename(sourceCodeFilename);
		context.setReport(new Report());
		return context;
	}
    
    /**
     * A callback that would be implemented by IDEs keeping track of PMD's progress
     * as it evaluates a set of files.
     * 
     * @author Brian Remedios
     */
	public interface ProgressMonitor {
		/**
		 * A status update reporting on current progress. Implementers will
		 * return true if it is to continue, false otherwise.
		 * 
		 * @param total
		 * @param totalDone
		 * @return
		 */
		boolean status(int total, int totalDone);
	}

	/**
	 * An entry point that would typically be used by IDEs intent on providing
	 * ongoing feedback and the ability to terminate it at will.
	 * 
	 * @param configuration
	 * @param ruleSetFactory
	 * @param files
	 * @param ctx
	 * @param monitor
	 */
	public static void processFiles(PMDConfiguration configuration,
			RuleSetFactory ruleSetFactory, Collection<File> files,
			RuleContext ctx, ProgressMonitor monitor) {
		
		// TODO
		// call the main processFiles with just the new monitor and a single logRenderer
	}
	
	/**
	 * Run PMD on a list of files using multiple threads - if more than one is available
	 * 
	 * @param configuration Configuration
	 * @param ruleSetFactory RuleSetFactory
	 * @param files List<DataSource>
	 * @param ctx RuleContext
	 * @param renderers List<Renderer>
	 */
	public static void processFiles(final PMDConfiguration configuration,
			final RuleSetFactory ruleSetFactory, final List<DataSource> files,
			final RuleContext ctx, final List<Renderer> renderers) {

		sortFiles(configuration, files);

		/*
		 * Check if multithreaded support is available. ExecutorService can also be
		 * disabled if threadCount is not positive, e.g. using the "-threads 0"
		 * command line option.
		 */
		if (SystemUtils.MT_SUPPORTED && configuration.getThreads() > 0) {
			new MultiThreadProcessor(configuration).processFiles(ruleSetFactory, files, ctx, renderers);
		} else {
			new MonoThreadProcessor(configuration).processFiles(ruleSetFactory, files, ctx, renderers);
		}
	}
	
	/**
	 *
	 * @param configuration Configuration
	 * @param files List<DataSource>
	 */
	private static void sortFiles(final PMDConfiguration configuration, final List<DataSource> files) {
		if (configuration.isStressTest()) {
			// randomize processing order
			Collections.shuffle(files);
		} else {
			final boolean useShortNames = configuration.isReportShortNames();
			final String inputPaths = configuration.getInputPaths();
			Collections.sort(files, new Comparator<DataSource>() {
				public int compare(DataSource left, DataSource right) {
					String leftString = left.getNiceFileName(useShortNames, inputPaths);
					String rightString = right.getNiceFileName(useShortNames, inputPaths);
					return leftString.compareTo(rightString);
				}
			});
		}
	}
	
	/**
	 *
	 * @param configuration Configuration
	 * @param languages Set<Language>
	 * @return List<DataSource>
	 */
	public static List<DataSource> getApplicableFiles(
			PMDConfiguration configuration, Set<Language> languages) {
		long startFiles = System.nanoTime();
		LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(languages);
		List<DataSource> files = FileUtil.collectFiles(
				configuration.getInputPaths(), fileSelector);
		long endFiles = System.nanoTime();
		Benchmarker.mark(Benchmark.CollectFiles, endFiles - startFiles, 0);
		return files;
	}

	/**
	 *
	 * @param configuration Configuration
	 * @param ruleSets RuleSets
	 * @return Set<Language>
	 */
	private static Set<Language> getApplicableLanguages(PMDConfiguration configuration, RuleSets ruleSets) {
		Set<Language> languages = new HashSet<Language>();
		LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();
		
		for (Rule rule : ruleSets.getAllRules()) {
			Language language = rule.getLanguage();
			if (languages.contains(language))
				continue;
			LanguageVersion version = discoverer.getDefaultLanguageVersion(language);
			if (RuleSet.applies(rule, version)) {
				languages.add(language);
				LOG.fine("Using " + language.getShortName() + 
						" version: " + version.getShortName());
			}
		}
		return languages;
	}

    /**
     * Entry to invoke PMD as command line tool
     *
     * @param args
     */
    public static void main(String[] args) {
    	PMDCommandLineInterface.run(args);
    }
    
    /**
     *
     * @param args String[]
     * @return int
     */
    public static int run(String[] args) { 
    	int status = 0;
		long start = System.nanoTime();
		final PMDParameters params = PMDCommandLineInterface.extractParameters(new PMDParameters(), args, "pmd");
		final PMDConfiguration configuration = PMDParameters.transformParametersIntoConfiguration(params);
	
		final Level logLevel = params.isDebug() ? Level.FINER : Level.INFO;
		final Handler logHandler = new ConsoleLogHandler();
		final ScopedLogHandlersManager logHandlerManager = new ScopedLogHandlersManager(logLevel, logHandler);
		final Level oldLogLevel = LOG.getLevel();
		LOG.setLevel(logLevel); //Need to do this, since the static logger has already been initialized at this point
		try {
		    PMD.doPMD(configuration);
		} catch (Exception e) {
			PMDCommandLineInterface.buildUsageText();
			System.out.println(e.getMessage());
			status = PMDCommandLineInterface.ERROR_STATUS;
		} finally {
		    logHandlerManager.close();
		    LOG.setLevel(oldLogLevel);
		    if (params.isBenchmark()) {
				long end = System.nanoTime();
				Benchmarker.mark(Benchmark.TotalPMD, end - start, 0);
				
				TextReport report = new TextReport();	// TODO get specified report format from config
				report.generate(Benchmarker.values(), System.err);
		    }
		}
		return status;
    }

	public static final String VERSION;
    /**
     * Determines the version from maven's generated pom.properties file.
     */
    static {
    	String pmdVersion = null;
    	InputStream stream = PMD.class.getResourceAsStream("/META-INF/maven/net.sourceforge.pmd/pmd/pom.properties");
    	if (stream != null) {
    		try {
    			Properties properties = new Properties();
    			properties.load(stream);
    			pmdVersion = properties.getProperty("version");
        	} catch (IOException e) {
        		LOG.log(Level.FINE, "Couldn't determine version of PMD", e);
        	}
		}
    	if (pmdVersion == null) {
    		pmdVersion = "unknown";
    	}
    	VERSION = pmdVersion;
    }
}
