/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.symboltable.SymbolFacade;

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

    private TargetJDKVersion targetJDKVersion;

    public PMD() {
        targetJDKVersion = new TargetJDK1_4();
    }

    public PMD(TargetJDKVersion targetJDKVersion) {
        this.targetJDKVersion = targetJDKVersion;
    }

    /**
     * @param reader - a Reader to the Java code to analyse
     * @param ruleSet - the set of rules to process against the file
     * @param ctx - the context in which PMD is operating.  This contains the Renderer and whatnot
     */
    public void processFile(Reader reader, RuleSet ruleSet, RuleContext ctx) throws PMDException {
        try {
            JavaParser parser = targetJDKVersion.createParser(reader);
            ASTCompilationUnit c = parser.CompilationUnit();
            Thread.yield();
            SymbolFacade stb = new SymbolFacade();
            stb.initializeWith(c);
            List acus = new ArrayList();
            acus.add(c);
            ruleSet.apply(acus, ctx);
            reader.close();
        } catch (ParseException pe) {
            throw new PMDException("Error while parsing " + ctx.getSourceCodeFilename(), pe);
        } catch (Exception e) {
            throw new PMDException("Error while processing " + ctx.getSourceCodeFilename(), e);
        }
    }

    /**
     * @param fileContents - an InputStream to the Java code to analyse
     * @param encoding - the source code's character set encoding
     * @param ruleSet - the set of rules to process against the file
     * @param ctx - the context in which PMD is operating.  This contains the Report and whatnot
     */
    public void processFile(InputStream fileContents, String encoding, RuleSet ruleSet, RuleContext ctx) throws PMDException {
        try {
            processFile(new InputStreamReader(fileContents, encoding), ruleSet, ctx);
        } catch (UnsupportedEncodingException uee) {
            throw new PMDException("Unsupported encoding exception: " + uee.getMessage());
        }
    }

    /**
     * @param fileContents - an InputStream to the Java code to analyse
     * @param ruleSet - the set of rules to process against the source code
     * @param ctx - the context in which PMD is operating.  This contains the Report and whatnot
     */
    public void processFile(InputStream fileContents, RuleSet ruleSet, RuleContext ctx) throws PMDException {
        processFile(fileContents, System.getProperty("file.encoding"), ruleSet, ctx);
    }

    public static void main(String[] args) {
        CommandLineOptions opts = new CommandLineOptions(args);

        List files;
        if (opts.containsCommaSeparatedFileList()) {
            files = collectFromCommaDelimitedString(opts.getInputPath());
        } else {
            files = collectFilesFromOneName(opts.getInputPath());
        }

        PMD pmd;
        if (opts.jdk13()) {
            pmd = new PMD(new TargetJDK1_3());
        } else {
            pmd = new PMD();
        }

        RuleContext ctx = new RuleContext();
        ctx.setReport(new Report());

        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            RuleSet rules = ruleSetFactory.createRuleSet(opts.getRulesets());
            for (Iterator i = files.iterator(); i.hasNext();) {
                DataSource dataSource = (DataSource) i.next();
                String niceFileName = dataSource.getNiceFileName(opts.shortNamesEnabled(), opts.getInputPath());
                ctx.setSourceCodeFilename(niceFileName);
                if (opts.debugEnabled()) {
                    System.out.println("Processing " + ctx.getSourceCodeFilename());
                }
                try {
                    pmd.processFile(new BufferedInputStream(dataSource.getInputStream()), opts.getEncoding(), rules, ctx);
                } catch (PMDException pmde) {
                    if (opts.debugEnabled()) {
                        pmde.getReason().printStackTrace();
                    }
                    ctx.getReport().addError(new Report.ProcessingError(pmde.getMessage(), niceFileName));
                }
            }
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

    private static List collectFilesFromOneName(String inputFileName) {
        return collect(inputFileName);
    }

    private static List collectFromCommaDelimitedString(String fileList) {
        List files = new ArrayList();
        for (StringTokenizer st = new StringTokenizer(fileList, ","); st.hasMoreTokens();) {
            files.addAll(collect(st.nextToken()));
        }
        return files;
    }

    private static List collect(String filename) {
        File inputFile = new File(filename);
        if (!inputFile.exists()) {
            throw new RuntimeException("File " + inputFile.getName() + " doesn't exist");
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
                        if (zipEntry.getName().endsWith(".java")) {
                            dataSources.add(new ZipDataSource(zipFile, zipEntry));
                        }
                    }
                } catch (IOException ze) {
                    throw new RuntimeException("Zip file " + inputFile.getName() + " can't be opened");
                }
            } else {
                dataSources.add(new FileDataSource(inputFile));
            }
        } else {
            FileFinder finder = new FileFinder();
            List files = finder.findFilesFrom(inputFile.getAbsolutePath(), new JavaLanguage.JavaFileOrDirectoryFilter(), true);
            for (Iterator i = files.iterator(); i.hasNext(); ) {
                dataSources.add(new FileDataSource((File) i.next()));
            }
        }
        return dataSources;
    }

}
