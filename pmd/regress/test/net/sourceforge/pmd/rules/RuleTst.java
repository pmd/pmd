package test.net.sourceforge.pmd.rules;

/**
 * RuleTst
 *
 * Extend your Rule TestCases from here to get some
 * juicy code sharing.
 */

import java.io.InputStream;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;
import net.sourceforge.pmd.ast.*;

public class RuleTst
    extends TestCase
{
    public RuleTst( String name ) {
	super( name );
    }

    public Report process( String fileName,
			   Rule rule )
	throws Throwable
    {
	// Set up the Context
	RuleContext ctx = new RuleContext();
	ctx.setReport( new Report("xml", fileName) );

	// Parse the file
	InputStream javaFile =
	    getClass().getClassLoader().getResourceAsStream( fileName );
	JavaParser parser = new JavaParser( javaFile );
	ASTCompilationUnit astCU = parser.CompilationUnit();

	// Collect the ACUs
	List acus = new ArrayList();
	acus.add( astCU );
	
	// Apply the rules
	rule.apply( acus, ctx );
	
	// Return the report.
	return ctx.getReport();

    }

    public Report process2(String file, Rule rule)  {
        try {
            PMD p = new PMD();
            RuleContext ctx = new RuleContext();
            ctx.setReport(new Report("xml", file));
            p.processFile(file, getClass().getClassLoader().getResourceAsStream(file), rule, ctx);
            return ctx.getReport();
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("File " + file + " not found");
        }
    }
}
