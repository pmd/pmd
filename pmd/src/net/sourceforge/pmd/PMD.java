/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 3:23:17 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.renderers.*;
import net.sourceforge.pmd.swingui.PMDViewer;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaFileOrDirectoryFilter;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class PMD {

    /**
     * @param reader - an InputStream to the Java code to analyse
     * @param ruleSet - the set of rules to process against the file
     * @param ctx - the context in which PMD is operating.  This contains the Renderer and whatnot
     */
    public void processFile(Reader reader, RuleSet ruleSet, RuleContext ctx) {
        try {
            JavaParser parser = new JavaParser(reader);
            ASTCompilationUnit c = parser.CompilationUnit();
            Thread.yield();
            //c.dump("");
            SymbolFacade stb = new SymbolFacade();
            stb.initializeWith(c);
            List acus = new ArrayList();
            acus.add(c);
            ruleSet.apply(acus, ctx);
            reader.close();
        } catch (ParseException pe) {
            System.out.println("Error while parsing " + ctx.getSourceCodeFilename() + " at line " + pe.currentToken.beginLine + "; continuing...");
        } catch (Throwable t) {
            System.out.println("Error while processing " +  ctx.getSourceCodeFilename() + "; "+ t.getClass() + ":" +  t.getMessage() + "; continuing...");
            t.printStackTrace();
        }
    }

    /**
     * @param fileContents - an InputStream to the Java code to analyse
     * @param ruleSet - the set of rules to process against the file
     * @param ctx - the context in which PMD is operating.  This contains the Report and whatnot
     */
    public void processFile(InputStream fileContents, RuleSet ruleSet, RuleContext ctx) {
        processFile(new InputStreamReader(fileContents), ruleSet, ctx);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }

        if (args[0].equals("-g")) {
            try {
                PMDViewer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (args.length < 3) {
            usage();
            System.exit(1);
        }

        String inputFileName = args[0];
        String reportFormat = args[1];
        String ruleSets = args[2];

        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new RuntimeException("File " + inputFileName + " doesn't exist");
        }

        List files = null;
        if (!inputFile.isDirectory()) {
            files = new ArrayList();
            files.add(inputFile);
        } else {
            FileFinder finder = new FileFinder();
            files = finder.findFilesFrom(inputFile.getAbsolutePath(), new JavaFileOrDirectoryFilter(), true);
        }

        PMD pmd = new PMD();

        RuleContext ctx = new RuleContext();
        ctx.setReport(new Report());

        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            RuleSet rules = ruleSetFactory.createRuleSet(ruleSets);
            for (Iterator i = files.iterator(); i.hasNext();) {
                File file = (File)i.next();
                ctx.setSourceCodeFilename(file.getAbsolutePath());
                pmd.processFile(new FileInputStream(file), rules, ctx);
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (RuleSetNotFoundException rsnfe) {
            rsnfe.printStackTrace();
        }

        if (reportFormat.equals("xml")) {
            Renderer rend = new XMLRenderer();
            System.out.println(rend.render(ctx.getReport()));
        } else if (reportFormat.equals("ideaj")) {
            IDEAJRenderer special = new IDEAJRenderer();
            if (args[4].equals(".method")) {
                // working on a directory tree
                String sourcePath = args[3];
                System.out.println(special.render(ctx.getReport(), sourcePath));
            } else {
                // working on one file
                String classAndMethodName = args[4];
                String singleFileName = args[5];
                System.out.println(special.render(ctx.getReport(), classAndMethodName, singleFileName));
            }
        } else if (reportFormat.equals("text")) {
            Renderer rend = new TextRenderer();
            System.out.println(rend.render(ctx.getReport()));
        } else {
            Renderer rend = new HTMLRenderer();
            System.out.println(rend.render(ctx.getReport()));
        }
    }

    private static void usage() {
        System.err.println("");
        System.err.println("Please pass in a java source code filename or directory, a report format, and a ruleset filename or a comma-delimited string of ruleset filenames." + System.getProperty("line.separator") + "For example: " + System.getProperty("line.separator") + "c:\\> java -jar pmd-0.9.jar c:\\my\\source\\code html rulesets/unusedcode.xml,rulesets/imports.xml");
        System.err.println("");
    }
}
