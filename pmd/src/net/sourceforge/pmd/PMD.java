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
		
	public Report processFile(String filename, InputStream is, String ruleSetType, String format) throws FileNotFoundException {
        Report report = new Report(filename, format);
        List rules = RuleFactory.createRules(ruleSetType);

        try {
            InputStreamReader reader = new InputStreamReader(is);
            JavaParser parser = new JavaParser(reader);
            ASTCompilationUnit c = parser.CompilationUnit();
            //c.dump("");
            for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                JavaParserVisitor rule = (JavaParserVisitor)iterator.next();
                c.childrenAccept(rule, report);
            }
            reader.close();
        } catch (ParseException pe) {
            System.out.println("Error while parsing " + filename + " at line " + pe.currentToken.beginLine + "; continuing...");
        } catch (Throwable t) {
            System.out.println("Error while parsing " + filename + "; "+ t.getMessage() + "; continuing...");
            //t.printStackTrace();
        }
        return report;
	}

    public Report processFile(File file, String ruleSetType, String format) throws FileNotFoundException{
        return processFile(file.getAbsolutePath(), new FileInputStream(file), ruleSetType, format);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Pass a filename in");
        }
        File input = new File(args[0]);
        if (!input.exists()) {
            throw new RuntimeException("File " + args[0] + " doesn't exist");
        }
        PMD pmd = new PMD();
        try {
            Report report = pmd.processFile(input, RuleFactory.ALL, "xml");
            System.out.println(report.render());
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }
}
