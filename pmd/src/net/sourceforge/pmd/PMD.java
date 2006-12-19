/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter;
import net.sourceforge.pmd.parsers.Parser;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandler;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandlerBroker;

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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import edu.emory.mathcs.backport.java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PMD {
    public static final String EOL = System.getProperty("line.separator", "\n");
    public static final String VERSION = "3.9";
    public static final String EXCLUDE_MARKER = "NOPMD";


    private String excludeMarker = EXCLUDE_MARKER;
    private SourceTypeDiscoverer sourceTypeDiscoverer = new SourceTypeDiscoverer();

    public PMD() {}

    /**
     * Processes the file read by the reader agains the rule set.
     *
     * @param reader   input stream reader
     * @param ruleSets set of rules to process against the file
     * @param ctx      context in which PMD is operating. This contains the Renderer and
     *                 whatnot
     * @throws PMDException if the input could not be parsed or processed
     */
    public void processFile(Reader reader, RuleSets ruleSets, RuleContext ctx)
            throws PMDException {
        SourceType sourceType = getSourceTypeOfFile(ctx.getSourceCodeFilename());

        processFile(reader, ruleSets, ctx, sourceType);
    }

    /**
     * Processes the file read by the reader against the rule set.
     *
     * @param reader     input stream reader
     * @param ruleSets   set of rules to process against the file
     * @param ctx        context in which PMD is operating. This contains the Renderer and
     *                   whatnot
     * @param sourceType the SourceType of the source
     * @throws PMDException if the input could not be parsed or processed
     */
    public void processFile(Reader reader, RuleSets ruleSets, RuleContext ctx,
                            SourceType sourceType) throws PMDException {
        try {
            SourceTypeHandler sourceTypeHandler = SourceTypeHandlerBroker.getVisitorsFactoryForSourceType(sourceType);
            ctx.setSourceType(sourceType);
            Parser parser = sourceTypeHandler.getParser();
            parser.setExcludeMarker(excludeMarker);
            Object rootNode = parser.parse(reader);
            ctx.excludeLines(parser.getExcludeMap());
            Thread.yield();
            sourceTypeHandler.getSymbolFacade().start(rootNode);

            Language language = SourceTypeToRuleLanguageMapper.getMappedLanguage(sourceType);

            if (ruleSets.usesDFA(language)) {
                sourceTypeHandler.getDataFlowFacade().start(rootNode);
            }

            if (ruleSets.usesTypeResolution(language)) {
                sourceTypeHandler.getTypeResolutionFacade().start(rootNode);
            }

            List acus = new ArrayList();
            acus.add(rootNode);

            ruleSets.apply(acus, ctx, language);
        } catch (ParseException pe) {
            throw new PMDException("Error while parsing "
                    + ctx.getSourceCodeFilename(), pe);
        } catch (Exception e) {
            throw new PMDException("Error while processing "
                    + ctx.getSourceCodeFilename(), e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new PMDException("Error while closing "
                        + ctx.getSourceCodeFilename(), e);
            }
        }
    }

    /**
     * Get the SourceType of the source file with given name. This depends on the fileName
     * extension, and the java version.
     * <p/>
     * For compatibility with older code that does not always pass in a correct filename,
     * unrecognized files are assumed to be java files.
     *
     * @param fileName Name of the file, can be absolute, or simple.
     * @return the SourceType
     */
    private SourceType getSourceTypeOfFile(String fileName) {
        SourceType sourceType = sourceTypeDiscoverer.getSourceTypeOfFile(fileName);
        if (sourceType == null) {
            // For compatibility with older code that does not always pass in
            // a correct filename.
            sourceType = sourceTypeDiscoverer.getSourceTypeOfJavaFiles();
        }
        return sourceType;
    }

    /**
     * Processes the file read by the reader agains the rule set.
     *
     * @param reader  input stream reader
     * @param ruleSet set of rules to process against the file
     * @param ctx     context in which PMD is operating. This contains the Renderer and
     *                whatnot
     * @throws PMDException if the input could not be parsed or processed
     */
    public void processFile(Reader reader, RuleSet ruleSet, RuleContext ctx)
            throws PMDException {
        processFile(reader, new RuleSets(ruleSet), ctx);
    }

    /**
     * Processes the input stream agains a rule set using the given input encoding.
     *
     * @param fileContents an input stream to analyze
     * @param encoding     input stream's encoding
     * @param ruleSet      set of rules to process against the file
     * @param ctx          context in which PMD is operating. This contains the Report and whatnot
     * @throws PMDException if the input encoding is unsupported or the input stream could
     *                      not be parsed
     * @see #processFile(Reader, RuleSet, RuleContext)
     */
    public void processFile(InputStream fileContents, String encoding,
                            RuleSet ruleSet, RuleContext ctx) throws PMDException {
        try {
            processFile(new InputStreamReader(fileContents, encoding), ruleSet, ctx);
        } catch (UnsupportedEncodingException uee) {
            throw new PMDException("Unsupported encoding exception: "
                    + uee.getMessage());
        }
    }

    /**
     * Processes the input stream agains a rule set using the given input encoding.
     *
     * @param fileContents an input stream to analyze
     * @param encoding     input stream's encoding
     * @param ruleSets     set of rules to process against the file
     * @param ctx          context in which PMD is operating. This contains the Report and whatnot
     * @throws PMDException if the input encoding is unsupported or the input stream could
     *                      not be parsed
     * @see #processFile(Reader, RuleSet, RuleContext)
     */
    public void processFile(InputStream fileContents, String encoding,
                            RuleSets ruleSets, RuleContext ctx) throws PMDException {
        try {
            processFile(new InputStreamReader(fileContents, encoding), ruleSets, ctx);
        } catch (UnsupportedEncodingException uee) {
            throw new PMDException("Unsupported encoding exception: "
                    + uee.getMessage());
        }
    }

    /**
     * Processes the input stream against a rule set assuming the platform character set.
     *
     * @param fileContents input stream to check
     * @param ruleSet      the set of rules to process against the source code
     * @param ctx          the context in which PMD is operating. This contains the Report and
     *                     whatnot
     * @throws PMDException if the input encoding is unsupported or the input input stream
     *                      could not be parsed
     * @see #processFile(InputStream, String, RuleSet, RuleContext)
     */
    public void processFile(InputStream fileContents, RuleSet ruleSet,
                            RuleContext ctx) throws PMDException {
        processFile(fileContents, System.getProperty("file.encoding"), ruleSet, ctx);
    }

    public void setExcludeMarker(String marker) {
        this.excludeMarker = marker;
    }

    /**
     * Set the SourceType to be used for ".java" files.
     *
     * @param javaVersion the SourceType that indicates the java version
     */
    public void setJavaVersion(SourceType javaVersion) {
        sourceTypeDiscoverer.setSourceTypeOfJavaFiles(javaVersion);
    }

    public static void main(String[] args) {
        CommandLineOptions opts = new CommandLineOptions(args);

        SourceFileSelector fileSelector = new SourceFileSelector();

        fileSelector.setSelectJavaFiles(opts.isCheckJavaFiles());
        fileSelector.setSelectJspFiles(opts.isCheckJspFiles());

        List files;
        if (opts.containsCommaSeparatedFileList()) {
            files = collectFromCommaDelimitedString(opts.getInputPath(),
                    fileSelector);
        } else {
            files = collectFilesFromOneName(opts.getInputPath(), fileSelector);
        }

        SourceType sourceType;
        if (opts.getTargetJDK().equals("1.3")) {
            if (opts.debugEnabled())
                System.out.println("In JDK 1.3 mode");
            sourceType = SourceType.JAVA_13;
        } else if (opts.getTargetJDK().equals("1.5")) {
            if (opts.debugEnabled())
                System.out.println("In JDK 1.5 mode");
            sourceType = SourceType.JAVA_15;
        } else if (opts.getTargetJDK().equals("1.6")) {
            if (opts.debugEnabled())
                System.out.println("In JDK 1.6 mode");
            sourceType = SourceType.JAVA_16;
        } else {
            if (opts.debugEnabled())
                System.out.println("In JDK 1.4 mode");
            sourceType = SourceType.JAVA_14;
        }

        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        report.start();

        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            ruleSetFactory.setMinimumPriority(opts.getMinPriority());

            RuleSets rulesets = ruleSetFactory.createRuleSets(opts.getRulesets());
            printRuleNamesInDebug(opts.debugEnabled(), rulesets);

            processFiles(opts.getCpus(), ruleSetFactory, sourceType, files, ctx,
                    opts.getRulesets(), opts.debugEnabled(), opts.shortNamesEnabled(),
                    opts.getInputPath(), opts.getEncoding(), opts.getExcludeMarker());
        } catch (RuleSetNotFoundException rsnfe) {
            System.out.println(opts.usage());
            rsnfe.printStackTrace();
        }
        report.end();

        Writer w = null;
        try {
            Renderer r = opts.createRenderer();
            if (opts.getReportFile() != null) {
                w = new BufferedWriter(new FileWriter(opts.getReportFile()));
            } else {
                w = new OutputStreamWriter(System.out);
            }
            r.render(w, ctx.getReport());
            w.write(EOL);
            w.flush();
            if (opts.getReportFile() != null) {
                w.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(opts.usage());
            if (opts.debugEnabled()) {
                e.printStackTrace();
            }
        } finally {
            if (opts.getReportFile() != null && w != null) {
                try {
                    w.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private static class PmdRunnable extends PMD implements Runnable {
        private final ExecutorService executor;
        private final DataSource dataSource;
        private final String fileName;
        private final boolean debugEnabled;
        private final String encoding;
        private final String rulesets;

        public PmdRunnable(ExecutorService executor, DataSource dataSource, String fileName, SourceType sourceType,
                boolean debugEnabled, String encoding, String rulesets, String excludeMarker) {
            this.executor = executor;
            this.dataSource = dataSource;
            this.fileName = fileName;
            this.debugEnabled = debugEnabled;
            this.encoding = encoding;
            this.rulesets = rulesets;
            
            setJavaVersion(sourceType);
            setExcludeMarker(excludeMarker);
        }

        public void run() {
            PmdThread thread = (PmdThread) Thread.currentThread();

            RuleContext ctx = thread.getRuleContext();
            RuleSets rs = thread.getRuleSets(rulesets);

            ctx.setSourceCodeFilename(fileName);
            if (debugEnabled) {
                System.out.println("Processing " + ctx.getSourceCodeFilename());
            }

            try {
                InputStream stream = new BufferedInputStream(dataSource.getInputStream());
                processFile(stream, encoding, rs, ctx);
            } catch (PMDException pmde) {
                if (debugEnabled) {
                    pmde.getReason().printStackTrace();
                }
                ctx.getReport().addError(
                        new Report.ProcessingError(pmde.getMessage(),
                        fileName));
            } catch (Throwable t) {
                // unexepected exception: log and stop executor service
                if (debugEnabled) {
                    t.printStackTrace();
                }
                ctx.getReport().addError(
                        new Report.ProcessingError(t.getMessage(),
                        fileName));

                executor.shutdownNow();
            }
        }

    }

    private static class PmdThreadFactory implements ThreadFactory {

        public PmdThreadFactory(RuleSetFactory ruleSetFactory) {
            this.ruleSetFactory = ruleSetFactory;
        }

        private final RuleSetFactory ruleSetFactory;
        private final AtomicInteger counter = new AtomicInteger();

        public Thread newThread(Runnable r) {
            PmdThread t = new PmdThread(counter.incrementAndGet(), r, ruleSetFactory);
            threadList.add(t);
            return t;
        }

        public List threadList = Collections.synchronizedList(new LinkedList());

    }

    private static class PmdThread extends Thread {

        public PmdThread(int id, Runnable r, RuleSetFactory ruleSetFactory) {
            super(r, "PmdThread " + id);
            this.id = id;
            context = new RuleContext();
            context.setReport(new Report());
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

        public String toString() {
            return "PmdThread " + id;
        }

    }

    /**
     * Run PMD on a list of files using multiple threads.
     *
     * @throws IOException If one of the files could not be read
     */
    public static void processFiles(int threadCount, RuleSetFactory ruleSetFactory, SourceType sourceType, List files, RuleContext ctx, String rulesets,
            boolean debugEnabled, boolean shortNamesEnabled, String inputPath,
            String encoding, String excludeMarker) {

        PmdThreadFactory factory = new PmdThreadFactory(ruleSetFactory);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount, factory);

        for (Iterator i = files.iterator(); i.hasNext();) {
            DataSource dataSource = (DataSource) i.next();
            String niceFileName = dataSource.getNiceFileName(shortNamesEnabled,
                    inputPath);

            Runnable r = new PmdRunnable(executor, dataSource, niceFileName, sourceType, debugEnabled,
                                            encoding, rulesets, excludeMarker);

            executor.execute(r);
        }
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }

        Report mainReport = ctx.getReport();
        Iterator i = factory.threadList.iterator();
        while (i.hasNext()) {
            PmdThread thread = (PmdThread) i.next();
            Report r = thread.context.getReport();
            mainReport.merge(r);
        }
    }

    /**
     * Run PMD on a list of files.
     *
     * @param files             the List of DataSource instances.
     * @param ctx               the context in which PMD is operating. This contains the Report and
     *                          whatnot
     * @param rulesets          the RuleSets
     * @param debugEnabled
     * @param shortNamesEnabled
     * @param inputPath
     * @param encoding
     * @throws IOException If one of the files could not be read
     */
    public void processFiles(List files, RuleContext ctx, RuleSets rulesets,
                             boolean debugEnabled, boolean shortNamesEnabled, String inputPath,
                             String encoding) throws IOException {
        for (Iterator i = files.iterator(); i.hasNext();) {
            DataSource dataSource = (DataSource) i.next();

            String niceFileName = dataSource.getNiceFileName(shortNamesEnabled,
                    inputPath);
            ctx.setSourceCodeFilename(niceFileName);
            if (debugEnabled) {
                System.out.println("Processing " + ctx.getSourceCodeFilename());
            }

            try {
                InputStream stream = new BufferedInputStream(dataSource
                        .getInputStream());
                processFile(stream, encoding, rulesets, ctx);
            } catch (PMDException pmde) {
                if (debugEnabled) {
                    pmde.getReason().printStackTrace();
                }
                ctx.getReport().addError(new Report.ProcessingError(pmde.getMessage(), niceFileName));
            }
        }
    }

    /**
     * If in debug modus, print the names of the rules.
     *
     * @param debugEnabled the boolean indicating if debug is enabled
     * @param rulesets     the RuleSets to print
     */
    private static void printRuleNamesInDebug(boolean debugEnabled, RuleSets rulesets) {
        if (debugEnabled) {
            for (Iterator i = rulesets.getAllRules().iterator(); i.hasNext();) {
                Rule r = (Rule) i.next();
                System.out.println("Loaded rule " + r.getName());
            }
        }
    }

    /**
     * Collects the given file into a list.
     *
     * @param inputFileName a file name
     * @param fileSelector  Filtering of wanted source files
     * @return the list of files collected from the <code>inputFileName</code>
     * @see #collect(String, SourceFileSelector)
     */
    private static List collectFilesFromOneName(String inputFileName,
                                                SourceFileSelector fileSelector) {
        return collect(inputFileName, fileSelector);
    }

    /**
     * Collects the files from the given comma-separated list.
     *
     * @param fileList     comma-separated list of filenames
     * @param fileSelector Filtering of wanted source files
     * @return list of files collected from the <code>fileList</code>
     */
    private static List collectFromCommaDelimitedString(String fileList,
                                                        SourceFileSelector fileSelector) {
        List files = new ArrayList();
        for (StringTokenizer st = new StringTokenizer(fileList, ","); st
                .hasMoreTokens();) {
            files.addAll(collect(st.nextToken(), fileSelector));
        }
        return files;
    }

    /**
     * Collects the files from the given <code>filename</code>.
     *
     * @param filename     the source from which to collect files
     * @param fileSelector Filtering of wanted source files
     * @return a list of files found at the given <code>filename</code>
     * @throws RuntimeException if <code>filename</code> is not found
     */
    private static List collect(String filename, SourceFileSelector fileSelector) {
        File inputFile = new File(filename);
        if (!inputFile.exists()) {
            throw new RuntimeException("File " + inputFile.getName()
                    + " doesn't exist");
        }
        List dataSources = new ArrayList();
        if (!inputFile.isDirectory()) {
            if (filename.endsWith(".zip") || filename.endsWith(".jar")) {
                ZipFile zipFile;
                try {
                    zipFile = new ZipFile(inputFile);
                    Enumeration e = zipFile.entries();
                    while (e.hasMoreElements()) {
                        ZipEntry zipEntry = (ZipEntry) e.nextElement();
                        if (fileSelector.isWantedFile(zipEntry.getName())) {
                            dataSources.add(new ZipDataSource(zipFile, zipEntry));
                        }
                    }
                } catch (IOException ze) {
                    throw new RuntimeException("Zip file " + inputFile.getName()
                            + " can't be opened");
                }
            } else {
                dataSources.add(new FileDataSource(inputFile));
            }
        } else {
            FileFinder finder = new FileFinder();
            List files = finder.findFilesFrom(inputFile.getAbsolutePath(),
                    new SourceFileOrDirectoryFilter(fileSelector), true);
            for (Iterator i = files.iterator(); i.hasNext();) {
                dataSources.add(new FileDataSource((File) i.next()));
            }
        }
        return dataSources;
    }

}

         
