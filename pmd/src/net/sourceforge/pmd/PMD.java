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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PMD {
    public static final String EOL = System.getProperty("line.separator", "\n");
    public static final String VERSION = "3.5";

    private String excludeMarker = ExcludeLines.EXCLUDE_MARKER;
    private SourceTypeDiscoverer sourceTypeDiscoverer = new SourceTypeDiscoverer();
    private SourceTypeHandlerBroker sourceTypeHandlerBroker = new SourceTypeHandlerBroker();

    public PMD() {
    }

    /**
     * @param targetJDKVersion
     * @deprecated Use the no-args constructor and the setJavaVersion method instead
     */
    public PMD(TargetJDKVersion targetJDKVersion) {
        if (targetJDKVersion instanceof TargetJDK1_3) {
            setJavaVersion(SourceType.JAVA_13);
        } else if (targetJDKVersion instanceof TargetJDK1_5) {
            setJavaVersion(SourceType.JAVA_15);
        }
    }

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
     * Processes the file read by the reader agains the rule set.
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
            SourceTypeHandler sourceTypeHandler = sourceTypeHandlerBroker
                    .getVisitorsFactoryForSourceType(sourceType);

            ExcludeLines excluder = new ExcludeLines(reader, excludeMarker);
            ctx.excludeLines(excluder.getLinesToExclude());

            Parser parser = sourceTypeHandler.getParser();
            Object rootNode = parser.parse(excluder.getCopyReader());
            Thread.yield();

            // TODO - move SymbolFacade traversal inside JavaParser.CompilationUnit()
            sourceTypeHandler.getSymbolFacade().start(rootNode);

            Language language = SourceTypeToRuleLanguageMapper.getMappedLanguage(sourceType);

            if (ruleSets.usesDFA(language)) {
                sourceTypeHandler.getDataFlowFacade().start(rootNode);
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

        PMD pmd = new PMD();
        if (opts.getTargetJDK().equals("1.3")) {
            if (opts.debugEnabled())
                System.out.println("In JDK 1.3 mode");
            pmd.setJavaVersion(SourceType.JAVA_13);
        } else if (opts.getTargetJDK().equals("1.5")) {
            if (opts.debugEnabled())
                System.out.println("In JDK 1.5 mode");
            pmd.setJavaVersion(SourceType.JAVA_15);
        } else {
            if (opts.debugEnabled())
                System.out.println("In JDK 1.4 mode");
            pmd.setJavaVersion(SourceType.JAVA_14);
        }
        pmd.setExcludeMarker(opts.getExcludeMarker());

        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        report.start();

        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            RuleSets rulesets = ruleSetFactory.createRuleSets(opts.getRulesets());
            printRuleNamesInDebug(opts.debugEnabled(), rulesets);

            pmd.processFiles(files, ctx, rulesets, opts.debugEnabled(), opts
                    .shortNamesEnabled(), opts.getInputPath(), opts.getEncoding());
        } catch (FileNotFoundException fnfe) {
            System.out.println(opts.usage());
            fnfe.printStackTrace();
        } catch (RuleSetNotFoundException rsnfe) {
            System.out.println(opts.usage());
            rsnfe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println(opts.usage());
            ioe.printStackTrace();
        }
        report.end();

        try {
            Renderer r = opts.createRenderer();
            System.out.println(r.render(ctx.getReport()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(opts.usage());
            if (opts.debugEnabled()) {
                e.printStackTrace();
            }
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
     * @see #collect(String)
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
