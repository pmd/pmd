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

public class PMD {
		
	public void processFile(String filename, InputStream is, String ruleSetType, Report report)
            throws FileNotFoundException {
        List rules = RuleFactory.createRules(ruleSetType);

        try {
            InputStreamReader reader = new InputStreamReader(is);
            JavaParser parser = new JavaParser(reader);
            ASTCompilationUnit c = parser.CompilationUnit();
            //c.dump("");
            for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                c.childrenAccept((JavaParserVisitor)iterator.next(), report);
            }
            reader.close();
        } catch (ParseException pe) {
            System.out.println("Error while parsing " + filename + " at line " + pe.currentToken.beginLine + "; continuing...");
        } catch (Throwable t) {
            System.out.println("Error while parsing " + filename + "; "+ t.getMessage() + "; continuing...");
            //t.printStackTrace();
        }
	}

    public void processFile(File file, String ruleSetType, Report report) throws FileNotFoundException{
        processFile(file.getAbsolutePath(), new FileInputStream(file), ruleSetType, report);
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
        try {
            pmd.processFile(input, RuleFactory.ALL, report);
            System.out.println(report.render());
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }
}
