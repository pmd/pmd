package test.net.sourceforge.pmd.rx.rules;

import java.io.StringReader;

import java.util.Set;

import org.drools.*;
import org.drools.spi.Rule;

import net.sourceforge.pmd.rx.*;
import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.rx.facts.*;

import test.net.sourceforge.pmd.rx.*;

import junit.framework.TestCase;

public class DroolsRuleTst 
    extends TestCase
{
    public DroolsRuleTst(String name) {
	super( name );
    }

    public Set collectViolations( Rule IUT, String javaCode ) 
	throws Throwable
    {
	RuleBase rules = new RuleBase();
	FactCollector collector = new FactCollector( RuleViolationFact.class );
	
	rules.addRule( collector );
	rules.addRule( IUT );

	JavaParser parser =
	    new JavaParser( new StringReader( javaCode ));
	ASTCompilationUnit acu =
	    parser.CompilationUnit();

	WorkingMemory memory = rules.createWorkingMemory();

	DroolsVisitor visitor = new DroolsVisitor( memory );
	visitor.visit( acu, null );

	return collector.getFacts();
    }

}
