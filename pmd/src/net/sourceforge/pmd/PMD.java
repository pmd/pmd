/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ParseException;
import net.sourceforge.pmd.lang.xpath.Initializer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.Benchmark;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.ConsoleLogHandler;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

/**
 * This is the main class for interacting with PMD.  The primary flow of
 * all Rule process is controlled via interactions with this class.  A command
 * line interface is supported, as well as a programmatic API for integrating
 * PMD with other software such as IDEs and Ant.
 */
public class PMD {

    private static final Logger LOG = Logger.getLogger(PMD.class.getName());

    public static final String EOL = System.getProperty("line.separator", "\n");
    public static final String VERSION = "@@VERSION@@";
    public static final String SUPPRESS_MARKER = "NOPMD";

    /**
     * Do we have proper permissions to use multithreading?
     */
    // FUTURE Move this into the SystemUtils
    private static final boolean MT_SUPPORTED;

    static {
		boolean error = false;
		try {
		    /*
		     * ant task ran from Eclipse with jdk 1.5.0 raises an AccessControlException
		     * when shutdown is called. Standalone pmd or ant from command line are fine.
		     *
		     * With jdk 1.6.0, ant task from Eclipse also works.
		     */
		    ExecutorService executor = Executors.newFixedThreadPool(1);
		    executor.shutdown();
		} catch (RuntimeException e) {
		    error = true;
		}
		MT_SUPPORTED = !error;
    }

    protected final Configuration configuration;

    /**
     * Create a PMD instance using a default Configuration.  Changes to the
     * configuration may be required.
     */
    public PMD() {
    	this(new Configuration());
    }

    /**
     * Create a PMD instance using the specified Configuration.
     * @param configuration The runtime Configuration of PMD to use.
     */
    public PMD(Configuration configuration) {
    	this.configuration = configuration;
    }

    /**
     * Get the runtime configuration.  The configuration can be modified
     * to affect how PMD behaves.
     * @return The configuration.
     * @see Configuration
     */
    public Configuration getConfiguration() {
    	return configuration;
    }

    /**
     * Processes the input stream against a rule set using the given input encoding.
     *
     * @param inputStream The InputStream to analyze.
     * @param ruleSets The collection of rules to process against the file.
     * @param ctx The context in which PMD is operating.
     * @throws PMDException if the input encoding is unsupported, the input stream could
     *                      not be parsed, or other error is encountered.
     * @see #processFile(Reader, RuleSets, RuleContext)
     */
    public void processFile(InputStream inputStream, RuleSets ruleSets, RuleContext ctx) throws PMDException {
		try {
		    processFile(new InputStreamReader(inputStream, configuration.getSourceEncoding()), ruleSets, ctx);
		} catch (UnsupportedEncodingException uee) {
		    throw new PMDException("Unsupported encoding exception: " + uee.getMessage());
		}
    }

    /**
     * Processes the input stream against a rule set using the given input encoding.
     * If the LanguageVersion is <code>null</code>  on the RuleContext, it will
     * be automatically determined.  Any code which wishes to process files for
     * different Languages, will need to be sure to either properly set the
     * Language on the RuleContext, or set it to <code>null</code> first.
     *
     * @see RuleContext#setLanguageVersion(LanguageVersion)
     * @see Configuration#getLanguageVersionOfFile(String)
     *
     * @param reader The Reader to analyze.
     * @param ruleSets The collection of rules to process against the file.
     * @param ctx The context in which PMD is operating.
     * @throws PMDException if the input encoding is unsupported, the input stream could
     *                      not be parsed, or other error is encountered.
     */
    public void processFile(Reader reader, RuleSets ruleSets, RuleContext ctx) throws PMDException {
		// If LanguageVersion of the source file is not known, make a determination
		if (ctx.getLanguageVersion() == null) {
		    LanguageVersion languageVersion = configuration.getLanguageVersionOfFile(ctx.getSourceCodeFilename());
		    ctx.setLanguageVersion(languageVersion);
		}

		// make sure custom XPath functions are initialized
		Initializer.initialize();

		try {
		    // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
			 if (ruleSets.applies(ctx.getSourceCodeFile())) {
				LanguageVersion languageVersion = ctx.getLanguageVersion();
				LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
				// TODO Handle Rules having different parser options.
				ParserOptions parserOptions = languageVersionHandler.getDefaultParserOptions();
				parserOptions.setSuppressMarker(configuration.getSuppressMarker());
				Parser parser = languageVersionHandler.getParser(parserOptions);
				long start = System.nanoTime();
				Node rootNode = parser.parse(ctx.getSourceCodeFilename(), reader);
				ctx.getReport().suppress(parser.getSuppressMap());
				long end = System.nanoTime();
				Benchmark.mark(Benchmark.TYPE_PARSER, end - start, 0);
				start = System.nanoTime();
				languageVersionHandler.getSymbolFacade().start(rootNode);
				end = System.nanoTime();
				Benchmark.mark(Benchmark.TYPE_SYMBOL_TABLE, end - start, 0);

				Language language = languageVersion.getLanguage();

				if (ruleSets.usesDFA(language)) {
				    start = System.nanoTime();
				    languageVersionHandler.getDataFlowFacade().start(rootNode);
				    end = System.nanoTime();
				    Benchmark.mark(Benchmark.TYPE_DFA, end - start, 0);
				}

				if (ruleSets.usesTypeResolution(language)) {
				    start = System.nanoTime();
				    languageVersionHandler.getTypeResolutionFacade(configuration.getClassLoader()).start(rootNode);
				    end = System.nanoTime();
				    Benchmark.mark(Benchmark.TYPE_TYPE_RESOLUTION, end - start, 0);
				}

				List<Node> acus = new ArrayList<Node>();
				acus.add(rootNode);

				ruleSets.apply(acus, ctx, language);
			    }
		} catch (ParseException pe) {
		    throw new PMDException("Error while parsing " + ctx.getSourceCodeFilename(), pe);
		} catch (Exception e) {
		    throw new PMDException("Error while processing " + ctx.getSourceCodeFilename(), e);
		} finally {
		    IOUtil.closeQuietly(reader);
		    }
    }

    private static RuleSets getRuleSets(Configuration configuration, RuleSetFactory factory, long loadRuleStart) {
    	RuleSets ruleSets = null;

    	try {
    		ruleSets = factory.createRuleSets(configuration.getRuleSets());
		    factory.setWarnDeprecated(false);
		    printRuleNamesInDebug(ruleSets);
		    long endLoadRules = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_LOAD_RULES, endLoadRules - loadRuleStart, 0);
		} catch (RuleSetNotFoundException rsnfe) {
		    LOG.log(Level.SEVERE, "Ruleset not found", rsnfe);
		    System.out.println(CommandLineOptions.usage());
		}
		return ruleSets;
    }

    // This method is the main entry point for command line usage.
    private static void doPMD(Configuration configuration) {

		// Load the RuleSets
		long startLoadRules = System.nanoTime();
		RuleSetFactory ruleSetFactory = getRulesetFactory(configuration);

		RuleSets ruleSets = getRuleSets(configuration, ruleSetFactory, startLoadRules);
		if (ruleSets == null) return;

		Set<Language> languages = getApplicableLanguages(configuration, ruleSets);
		List<DataSource> files = getApplicableFiles(configuration, languages);

		long reportEnd;
		Renderer renderer;
		Writer w = null;

		long reportStart = System.nanoTime();
		try {
		    renderer = configuration.createRenderer();
		    List<Renderer> renderers = new LinkedList<Renderer>();
		    renderers.add(renderer);
		    String reportFile = configuration.getReportFile();
		    w = StringUtil.isEmpty(reportFile) ?
	    		new OutputStreamWriter(System.out) :
				new BufferedWriter(new FileWriter(reportFile));

		    renderer.setWriter(w);
		    renderer.start();

		    reportEnd = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_REPORTING, reportEnd - reportStart, 0);

		    RuleContext ctx = new RuleContext();

		    processFiles(configuration, ruleSetFactory, files, ctx, renderers);

		    reportStart = System.nanoTime();
		    renderer.end();
		    w.flush();
		} catch (Exception e) {
		    String message = e.getMessage();
		    if (message != null) {
			LOG.severe(message);
		    } else {
			LOG.log(Level.SEVERE, "Exception during processing", e);
		    }

		    LOG.log(Level.FINE, "Exception during processing", e); //Only displayed when debug logging is on

		    LOG.info(CommandLineOptions.usage());
		} finally {
		    IOUtil.closeQuietly(w);
		    reportEnd = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_REPORTING, reportEnd - reportStart, 0);
		}
    }

	private static RuleSetFactory getRulesetFactory(Configuration configuration) {
		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		ruleSetFactory.setMinimumPriority(configuration.getMinimumPriority());
		ruleSetFactory.setWarnDeprecated(true);
		return ruleSetFactory;
	}

	private static List<DataSource> getApplicableFiles(Configuration configuration, Set<Language> languages) {
		long startFiles = System.nanoTime();
		LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(languages);
		List<DataSource> files = FileUtil.collectFiles(configuration.getInputPaths(), fileSelector);
		long endFiles = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_COLLECT_FILES, endFiles - startFiles, 0);
		return files;
	}

	// Determine applicable Languages
	private static Set<Language> getApplicableLanguages(Configuration configuration, RuleSets ruleSets) {
		Set<Language> languages = new HashSet<Language>();
		for (Rule rule : ruleSets.getAllRules()) {
		    Language language = rule.getLanguage();
		    if (languages.contains(language)) continue;
			if (RuleSet.applies(rule, configuration.getLanguageVersionDiscoverer().getDefaultLanguageVersion(
				language))) {
			    languages.add(language);
			    LOG.fine("Using " + language.getShortName());
			}
		}
		return languages;
	}

    public static void main(String[] args) {
		long start = System.nanoTime();
		final CommandLineOptions opts = new CommandLineOptions(args);
		final Configuration configuration = opts.getConfiguration();

		final Level logLevel = configuration.isDebug() ? Level.FINER : Level.INFO;
		final Handler logHandler = new ConsoleLogHandler();
		final ScopedLogHandlersManager logHandlerManager = new ScopedLogHandlersManager(logLevel, logHandler);
		final Level oldLogLevel = LOG.getLevel();
		LOG.setLevel(logLevel); //Need to do this, since the static logger has already been initialized at this point
		try {
		    doPMD(opts.getConfiguration());
		} finally {
		    logHandlerManager.close();
		    LOG.setLevel(oldLogLevel);
		    if (configuration.isBenchmark()) {
				long end = System.nanoTime();
				Benchmark.mark(Benchmark.TYPE_TOTAL_PMD, end - start, 0);
				System.err.println(Benchmark.report());
		    }
		}
    }

    private static class PmdRunnable extends PMD implements Callable<Report> {
		private final ExecutorService executor;
		private final DataSource dataSource;
		private final String fileName;
		private final List<Renderer> renderers;

		public PmdRunnable(ExecutorService executor, Configuration configuration, DataSource dataSource,
			String fileName, List<Renderer> renderers) {
		    super(configuration);
		    this.executor = executor;
		    this.dataSource = dataSource;
		    this.fileName = fileName;
		    this.renderers = renderers;
		}

		public Report call() {
		    PmdThread thread = (PmdThread) Thread.currentThread();

		    RuleContext ctx = thread.getRuleContext();
		    RuleSets rs = thread.getRuleSets(configuration.getRuleSets());

		    Report report = new Report();
		    ctx.setReport(report);

		    ctx.setSourceCodeFilename(fileName);
		    ctx.setSourceCodeFile(new File(fileName));
		    if (LOG.isLoggable(Level.FINE)) {
		    	LOG.fine("Processing " + ctx.getSourceCodeFilename());
		    }
		    for (Renderer r : renderers) {
		    	r.startFileAnalysis(dataSource);
		    }

		    try {
				InputStream stream = new BufferedInputStream(dataSource.getInputStream());
				ctx.setLanguageVersion(null);
				processFile(stream, rs, ctx);
		    } catch (PMDException pmde) {
		    	LOG.log(Level.FINE, "Error while processing file", pmde.getCause());

		    	report.addError(new Report.ProcessingError(pmde.getMessage(), fileName));
		    } catch (IOException ioe) {
		    	// unexpected exception: log and stop executor service
		    	LOG.log(Level.FINE, "IOException during processing", ioe);

		    	report.addError(new Report.ProcessingError(ioe.getMessage(), fileName));

		    	executor.shutdownNow();
		    } catch (RuntimeException re) {
		    	// unexpected exception: log and stop executor service
		    	LOG.log(Level.FINE, "RuntimeException during processing", re);

		    	report.addError(new Report.ProcessingError(re.getMessage(), fileName));

		    	executor.shutdownNow();
		    }
		    return report;
		}
    }

    private static class PmdThreadFactory implements ThreadFactory {

	private final RuleSetFactory ruleSetFactory;
	private final RuleContext ctx;
	private final AtomicInteger counter = new AtomicInteger();

	public PmdThreadFactory(RuleSetFactory ruleSetFactory, RuleContext ctx) {
	    this.ruleSetFactory = ruleSetFactory;
	    this.ctx = ctx;
	}

	public Thread newThread(Runnable r) {
	    PmdThread t = new PmdThread(counter.incrementAndGet(), r, ruleSetFactory, ctx);
	    threadList.add(t);
	    return t;
	}

	public List<PmdThread> threadList = Collections.synchronizedList(new LinkedList<PmdThread>());

    }

    private static class PmdThread extends Thread {

	public PmdThread(int id, Runnable r, RuleSetFactory ruleSetFactory, RuleContext ctx) {
	    super(r, "PmdThread " + id);
	    this.id = id;
	    context = new RuleContext(ctx);
	    this.ruleSetFactory = ruleSetFactory;
	}

	private int id;
	private RuleContext context;
	private RuleSets rulesets;
	private RuleSetFactory ruleSetFactory;

	public RuleContext getRuleContext() {
	    return context;
	}

	public RuleSets getRuleSets(String rsList) {
	    if (rulesets == null) {
		try {
		    rulesets = ruleSetFactory.createRuleSets(rsList);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    return rulesets;
	}

	@Override
	public String toString() {
	    return "PmdThread " + id;
	}
    }

    /**
     * Run PMD on a list of files using multiple threads.
     */
    public static void processFiles(final Configuration configuration, final RuleSetFactory ruleSetFactory,
	    final List<DataSource> files, final RuleContext ctx, final List<Renderer> renderers) {

	final boolean reportShortNames = configuration.isReportShortNames();
	final String inputPaths = configuration.getInputPaths();

	/*
	 * Check if multithreaded is supported.
	 * ExecutorService can also be disabled if threadCount is not positive, e.g. using the
	 * "-threads 0" command line option.
	 */
	boolean useMT = MT_SUPPORTED && configuration.getThreads() > 0;

	if (configuration.isStressTest()) {
	    // randomize processing order
	    Collections.shuffle(files);
	} else {
	    Collections.sort(files, new Comparator<DataSource>() {
		public int compare(DataSource d1, DataSource d2) {
		    String s1 = d1.getNiceFileName(reportShortNames, inputPaths);
		    String s2 = d2.getNiceFileName(reportShortNames, inputPaths);
		    return s1.compareTo(s2);
		}
	    });
	}

	if (useMT) {
	    RuleSets rs = null;
	    try {
		rs = ruleSetFactory.createRuleSets(configuration.getRuleSets());
	    } catch (RuleSetNotFoundException rsnfe) {
		// should not happen: parent already created a ruleset
	    }
	    rs.start(ctx);

	    PmdThreadFactory factory = new PmdThreadFactory(ruleSetFactory, ctx);
	    ExecutorService executor = Executors.newFixedThreadPool(configuration.getThreads(), factory);
	    List<Future<Report>> tasks = new LinkedList<Future<Report>>();

	    for (DataSource dataSource : files) {
		String niceFileName = dataSource.getNiceFileName(reportShortNames, inputPaths);
		PmdRunnable r = new PmdRunnable(executor, configuration, dataSource, niceFileName, renderers);
		Future<Report> future = executor.submit(r);
		tasks.add(future);
	    }
	    executor.shutdown();

	    while (!tasks.isEmpty()) {
		Future<Report> future = tasks.remove(0);
		Report report = null;
		try {
		    report = future.get();
		} catch (InterruptedException ie) {
		    Thread.currentThread().interrupt();
		    future.cancel(true);
		} catch (ExecutionException ee) {
		    Throwable t = ee.getCause();
		    if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		    } else if (t instanceof Error) {
			throw (Error) t;
		    } else {
			throw new IllegalStateException("PmdRunnable exception", t);
		    }
		}

		try {
		    long start = System.nanoTime();
		    for (Renderer r : renderers) {
			r.renderFileReport(report);
		    }
		    long end = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
		} catch (IOException ioe) {
		}
	    }

	    try {
		rs.end(ctx);
		long start = System.nanoTime();
		for (Renderer r : renderers) {
		    r.renderFileReport(ctx.getReport());
		}
		long end = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
	    } catch (IOException ioe) {
	    }

	} else {
	    // single threaded execution
	    PMD pmd = new PMD(configuration);

	    RuleSets rs = null;
	    try {
		rs = ruleSetFactory.createRuleSets(configuration.getRuleSets());
	    } catch (RuleSetNotFoundException rsnfe) {
		// should not happen: parent already created a ruleset
	    }
	    for (DataSource dataSource : files) {
		String niceFileName = dataSource.getNiceFileName(reportShortNames, inputPaths);

		Report report = new Report();
		ctx.setReport(report);

		ctx.setSourceCodeFilename(niceFileName);
		ctx.setSourceCodeFile(new File(niceFileName));
		if (LOG.isLoggable(Level.FINE)) {
		    LOG.fine("Processing " + ctx.getSourceCodeFilename());
		}
		rs.start(ctx);

		for (Renderer r : renderers) {
		    r.startFileAnalysis(dataSource);
		}

		try {
		    InputStream stream = new BufferedInputStream(dataSource.getInputStream());
		    ctx.setLanguageVersion(null);
		    pmd.processFile(stream, rs, ctx);
		} catch (PMDException pmde) {
		    LOG.log(Level.FINE, "Error while processing file", pmde.getCause());

		    report.addError(new Report.ProcessingError(pmde.getMessage(), niceFileName));
		} catch (IOException ioe) {
		    // unexpected exception: log and stop executor service
		    LOG.log(Level.FINE, "Unable to read source file", ioe);

		    report.addError(new Report.ProcessingError(ioe.getMessage(), niceFileName));
		} catch (RuntimeException re) {
		    // unexpected exception: log and stop executor service
		    LOG.log(Level.FINE, "RuntimeException while processing file", re);

		    report.addError(new Report.ProcessingError(re.getMessage(), niceFileName));
		}

		rs.end(ctx);

		try {
		    long start = System.nanoTime();
		    for (Renderer r : renderers) {
			r.renderFileReport(report);
		    }
		    long end = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
		} catch (IOException ioe) {
		}
	    }
	}
    }

    /**
     * If in debug modus, print the names of the rules.
     *
     * @param rulesets     the RuleSets to print
     */
    private static void printRuleNamesInDebug(RuleSets rulesets) {
	if (LOG.isLoggable(Level.FINER)) {
	    for (Rule r : rulesets.getAllRules()) {
		LOG.finer("Loaded rule " + r.getName());
	    }
	}
    }
}
