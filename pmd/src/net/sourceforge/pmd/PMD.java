package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class PMD {
    public static final String EOL = System.getProperty("line.separator", "\n");

    /**
     * @param reader - a Reader to the Java code to analyse
     * @param ruleSet - the set of rules to process against the file
     * @param ctx - the context in which PMD is operating.  This contains the Renderer and whatnot
     */
    public void processFile(Reader reader, RuleSet ruleSet, RuleContext ctx) throws PMDException {
        try {
            JavaParser parser = new JavaParser(reader);
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
     * @param ruleSet - the set of rules to process against the file
     * @param ctx - the context in which PMD is operating.  This contains the Report and whatnot
     */
    public void processFile(InputStream fileContents, RuleSet ruleSet, RuleContext ctx) throws PMDException {
        processFile(new InputStreamReader(fileContents), ruleSet, ctx);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            usage();
            System.exit(1);
        }

        String inputFileName = args[0];
        String reportFormat = args[1];
        String ruleSets = args[2];

        List files;
        if (inputFileName.indexOf(',') != -1) {
            files = collectFromCommaDelimitedString(inputFileName);
        } else {
            files = collectFilesFromOneName(inputFileName);
        }

        PMD pmd = new PMD();

        RuleContext ctx = new RuleContext();
        ctx.setReport(new Report());

        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            RuleSet rules = ruleSetFactory.createRuleSet(ruleSets);
            for (Iterator i = files.iterator(); i.hasNext();) {
                File file = (File) i.next();
                ctx.setSourceCodeFilename(file.getAbsolutePath());
                try {
                    pmd.processFile(new FileInputStream(file), rules, ctx);
                } catch (PMDException pmde) {
                    ctx.getReport().addError(new Report.ProcessingError(pmde.getMessage(), file.getAbsolutePath()));
                }
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (RuleSetNotFoundException rsnfe) {
            rsnfe.printStackTrace();
        }

        Renderer renderer = null;
        if (reportFormat.equals("xml")) {
            renderer = new XMLRenderer();
        } else if (reportFormat.equals("ideaj")) {
            renderer = new IDEAJRenderer(args);
        } else if (reportFormat.equals("text")) {
            renderer = new TextRenderer();
        } else if (reportFormat.equals("emacs")) {
            renderer = new EmacsRenderer();
        } else if (reportFormat.equals("csv")) {
            renderer = new CSVRenderer();
        } else if (reportFormat.equals("html")) {
            renderer = new HTMLRenderer();
        } else if (!reportFormat.equals("")) {
            try {
                renderer = (Renderer)Class.forName(reportFormat).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("Please supply a renderer name");
            usage();
            return;
        }
        System.out.println(renderer.render(ctx.getReport()));
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
        List files;
        if (!inputFile.isDirectory()) {
            files = new ArrayList();
            files.add(inputFile);
        } else {
            FileFinder finder = new FileFinder();
            files = finder.findFilesFrom(inputFile.getAbsolutePath(), new JavaLanguage.JavaFileOrDirectoryFilter(), true);
        }
        return files;
    }

    private static void usage() {
        final String EOL = System.getProperty("line.separator");
        System.err.println(EOL +
            "Please pass in a java source code filename or directory, a report format, " + EOL +
            "and a ruleset filename or a comma-delimited string of ruleset filenames." + EOL +
            "For example: " + EOL +
            "c:\\> java -jar pmd-1.1.jar c:\\my\\source\\code html rulesets/unusedcode.xml," +
            "rulesets/imports.xml" + EOL);
    }
}
