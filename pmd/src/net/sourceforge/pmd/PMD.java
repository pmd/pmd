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

import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class PMD {
		
	public void processFile(String filename, InputStream is, String ruleSetType, RuleContext ctx)
            throws FileNotFoundException {
        List rules = RuleFactory.createRules(ruleSetType);

        try {
            InputStreamReader reader = new InputStreamReader(is);
            JavaParser parser = new JavaParser(reader);
            ASTCompilationUnit c = parser.CompilationUnit();
            List acus = new ArrayList();
            acus.add(c);
            //c.dump("");
            for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                Rule rule = (Rule)iterator.next();
                rule.apply(acus, ctx);
            }
            reader.close();
        } catch (ParseException pe) {
            System.out.println("Error while parsing " + filename + " at line " + pe.currentToken.beginLine + "; continuing...");
        } catch (Throwable t) {
            System.out.println("Error while parsing " + filename + "; "+ t.getMessage() + "; continuing...");
            //t.printStackTrace();
        }
	}

    public void processFile(File file, String ruleSetType, RuleContext ctx) throws FileNotFoundException{
        processFile(file.getAbsolutePath(), new FileInputStream(file), ruleSetType, ctx);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("Please pass in both a filename and a format");
        }
        File input = new File(args[0]);
        if (!input.exists()) {
            throw new RuntimeException("File " + args[0] + " doesn't exist");
        }
        PMD pmd = new PMD();
        Report report = new Report(args[1], input.getAbsolutePath());
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        try {
            pmd.processFile(input, RuleFactory.ALL, ctx);
            System.out.println(report.render());
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }
}
