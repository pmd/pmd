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
		
		public Report processFile(InputStream is) {
			return null;
		}

		public Report processFile(FileReader fr) {
		return null; }
	
    public Report processFile(File file, String ruleSetType) {
        Report report = new Report(file.getAbsolutePath());
        List rules = RuleFactory.createRules(ruleSetType);

        try {
            Reader reader = new BufferedReader(new FileReader(file));
            JavaParser parser = new JavaParser(reader);
            ASTCompilationUnit c = parser.CompilationUnit();
            //c.dump("");
            for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                JavaParserVisitor rule = (JavaParserVisitor)iterator.next();
                c.childrenAccept(rule, report);
            }
            reader.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (ParseException pe) {
            System.out.println("Error while parsing " + file.getAbsolutePath() + " at line " + pe.currentToken.beginLine + "; continuing...");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Error while parsing " + file.getAbsolutePath() + "; "+ t.getMessage() + "; continuing...");
            //t.printStackTrace();
        }
        return report;
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
        Report report = pmd.processFile(input, RuleFactory.ALL);
        System.out.println(report.renderToText());
    }
}
