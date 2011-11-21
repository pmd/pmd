/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.processor.MonoThreadProcessor;
import net.sourceforge.pmd.processor.MultiThreadProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.Benchmark;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
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
	public static final String VERSION = "@@VERSION@@";
	public static final String SUPPRESS_MARKER = "NOPMD";

	protected final Configuration configuration;

	private final SourceCodeProcessor rulesetsFileProcessor;

	/**
	 * Create a report, filter out any defective rules, and keep a record of them.
	 * 
	 * @param rs
	 * @param ctx
	 * @param fileName
	 * @return
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
	
	
    private static Set<Rule> removeBrokenRules(RuleSets ruleSets) {
    	
    	Set<Rule> brokenRules = new HashSet<Rule>();
    	ruleSets.removeDysfunctionalRules(brokenRules);
	    
	    for (Rule rule : brokenRules) {
	    	 LOG.log(Level.WARNING, "Removed broken rule: " + rule.getName() + "  cause: " + rule.dysfunctionReason());	
	    }
	    
	    return brokenRules;
    }
    
	/**
	 * Create a PMD instance using a default Configuration. Changes to the
	 * configuration may be required.
	 */
	public PMD() {
		this(new Configuration());
	}

	/**
	 * Create a PMD instance using the specified Configuration.
	 * 
	 * @param configuration
	 *            The runtime Configuration of PMD to use.
	 */
	public PMD(Configuration configuration) {
		this.configuration = configuration;
		this.rulesetsFileProcessor = new SourceCodeProcessor(configuration);
	}

	/**
	 * Get the runtime configuration. The configuration can be modified to
	 * affect how PMD behaves.
	 * 
	 * @return The configuration.
	 * @see Configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	public SourceCodeProcessor getSourceCodeProcessor() {
		return rulesetsFileProcessor;
	}

	/** 
	 * This method is the main entry point for command line usage.
	 * 
	 * @param configuration
	 */
	public static void doPMD(Configuration configuration) {

		// Load the RuleSets
		long startLoadRules = System.nanoTime();
		RuleSetFactory ruleSetFactory = RulesetsFactoryUtils
				.getRulesetFactory(configuration);

		RuleSets ruleSets = RulesetsFactoryUtils.getRuleSets(
				configuration.getRuleSets(), ruleSetFactory, startLoadRules);
		if (ruleSets == null)
			return;

		Set<Language> languages = getApplicableLanguages(configuration,
				ruleSets);
		List<DataSource> files = getApplicableFiles(configuration, languages);

		long reportStart = System.nanoTime();
		try {
			
			Renderer renderer = configuration.createRenderer();
			List<Renderer> renderers = new LinkedList<Renderer>();
			renderers.add(renderer);

			renderer.setWriter(IOUtil.createWriter(configuration.getReportFile()));
			renderer.start();

			Benchmark
					.mark(Benchmark.TYPE_REPORTING, System.nanoTime() - reportStart, 0);

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
			LOG.info(CommandLineOptions.usage());
		} finally {
			Benchmark
					.mark(Benchmark.TYPE_REPORTING, System.nanoTime() - reportStart, 0);
		}
	}

	
	/**
	 * Run PMD on a list of files using multiple threads - if more than one is available
	 * 
	 */
	public static void processFiles(final Configuration configuration,
			final RuleSetFactory ruleSetFactory, final List<DataSource> files,
			final RuleContext ctx, final List<Renderer> renderers) {

		PMD.sortFiles(configuration, files);

		/*
		 * Check if multithreaded is supported. ExecutorService can also be
		 * disabled if threadCount is not positive, e.g. using the "-threads 0"
		 * command line option.
		 */
		if ( configuration.getThreads() > 0) {
			new MultiThreadProcessor(configuration).processFiles(ruleSetFactory, files, ctx, renderers);
		} else {
			new MonoThreadProcessor(configuration).processFiles(ruleSetFactory, files, ctx, renderers);
		}
	}

	
	private static void sortFiles(final Configuration configuration, final List<DataSource> files) {
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
	
	public static List<DataSource> getApplicableFiles(
			Configuration configuration, Set<Language> languages) {
		long startFiles = System.nanoTime();
		LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(languages);
		List<DataSource> files = FileUtil.collectFiles(
				configuration.getInputPaths(), fileSelector);
		long endFiles = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_COLLECT_FILES, endFiles - startFiles, 0);
		return files;
	}

	private static Set<Language> getApplicableLanguages(
			Configuration configuration, RuleSets ruleSets) {
		Set<Language> languages = new HashSet<Language>();
		for (Rule rule : ruleSets.getAllRules()) {
			Language language = rule.getLanguage();
			if (languages.contains(language))
				continue;
			if (RuleSet.applies(rule,
					configuration.getLanguageVersionDiscoverer()
							.getDefaultLanguageVersion(language))) {
				languages.add(language);
				LOG.fine("Using " + language.getShortName());
			}
		}
		return languages;
	}
	
	private static final int ERROR_STATUS = 1;

    /**
     * Entry to invoke PMD as command line tool
     * 
     * @param args
     */
    public static void main(String[] args) {
		System.exit(run(args));
    }

    public static int run(String[] args) { 
    	int status = 0;
		long start = System.nanoTime();
		final CommandLineOptions opts = new CommandLineOptions(args);
		final Configuration configuration = opts.getConfiguration();

		final Level logLevel = configuration.isDebug() ? Level.FINER : Level.INFO;
		final Handler logHandler = new ConsoleLogHandler();
		final ScopedLogHandlersManager logHandlerManager = new ScopedLogHandlersManager(logLevel, logHandler);
		final Level oldLogLevel = LOG.getLevel();
		LOG.setLevel(logLevel); //Need to do this, since the static logger has already been initialized at this point
		try {
		    PMD.doPMD(opts.getConfiguration());
		} catch (Exception e) {
			System.out.print(CommandLineOptions.usage());
			System.out.println(e.getMessage());
			status = ERROR_STATUS;
		} finally {
		    logHandlerManager.close();
		    LOG.setLevel(oldLogLevel);
		    if (configuration.isBenchmark()) {
				long end = System.nanoTime();
				Benchmark.mark(Benchmark.TYPE_TOTAL_PMD, end - start, 0);
				System.err.println(Benchmark.report());
		    }
		}
		return status;
    }
}
