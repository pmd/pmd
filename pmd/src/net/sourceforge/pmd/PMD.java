/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 3:23:17 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParserVisitor;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.reports.Report;
import net.sourceforge.pmd.reports.ReportFactory;

import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class PMD {
		
    public void processFile(String filename, InputStream is, RuleSet ruleSet, RuleContext ctx) throws FileNotFoundException {
        try {
            InputStreamReader reader = new InputStreamReader(is);
            JavaParser parser = new JavaParser(reader);
            ASTCompilationUnit c = parser.CompilationUnit();
            //c.dump("");
            List acus = new ArrayList();
            acus.add(c);
            ctx.setFilename(filename);
            ruleSet.apply(acus, ctx);
            reader.close();
        } catch (ParseException pe) {
            System.out.println("Error while parsing " + filename + " at line " + pe.currentToken.beginLine + "; continuing...");
        } catch (Throwable t) {
            System.out.println("Error while parsing " + filename + "; "+ t.getMessage() + "; continuing...");
            //t.printStackTrace();
        }
	}

    public void processFile(String filename, InputStream is, Rule rule, RuleContext ctx) throws FileNotFoundException {
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        processFile(filename, is, rules, ctx);
    }

	public void processFile(String filename, InputStream is, String ruleSetFile, RuleContext ctx) throws FileNotFoundException {
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        FileInputStream fis = new FileInputStream(ruleSetFile);
        RuleSet rules = ruleSetFactory.createRuleSet(fis);
        processFile(filename, is, rules, ctx);
	}

    public void processFile(File file, String ruleSetFile, RuleContext ctx) throws FileNotFoundException{
        processFile(file.getAbsolutePath(), new FileInputStream(file), ruleSetFile, ctx);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new RuntimeException("Please pass in a java source code filename, a report format, and a rule set file");
        }
        File input = new File(args[0]);
        if (!input.exists()) {
            throw new RuntimeException("File " + args[0] + " doesn't exist");
        }
        PMD pmd = new PMD();
        ReportFactory rf = new ReportFactory();
        Report report = rf.createReport(args[1]);
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        try {
            pmd.processFile(input, args[2], ctx);
            System.out.println(report.render());
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }
}
