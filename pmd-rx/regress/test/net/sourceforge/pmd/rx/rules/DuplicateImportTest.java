package test.net.sourceforge.pmd.rx.rules;

import java.util.Set;
import java.util.Iterator;

import net.sourceforge.pmd.rx.facts.*;
import net.sourceforge.pmd.rx.rules.*;

import test.net.sourceforge.pmd.rx.*;

public class DuplicateImportTest
    extends DroolsRuleTst
{
    private String testName = null;

    private String JAVA_NO_DUPE =
	"import java.util.*;" +
	"public class HelloWorld { }";

    private String JAVA_DUPE_ON_DEMAND =
	"import java.util.*;" +
	"import java.util.*;" +
	"public class HelloWorld { }";

    private String JAVA_DUPE_NO_DEMAND =
	"import java.util.List;" +
	"import java.util.List;" +
	"public class HelloWorld { }";

    private String JAVA_DUPE_ON_NO_DEMAND =
	"import java.util.*;" +
	"import java.util.List;" +
	"public class HelloWorld { }";

    public DuplicateImportTest(String name) {
	super( name );
	this.testName = name;
    }

    public void testNoDupes() 
	throws Throwable
    {
	Set results = collectViolations( new DuplicateImport(),
					 JAVA_NO_DUPE );
	Iterator rvs = results.iterator();
	while (rvs.hasNext()) {
	    RuleViolationFact rvFact = (RuleViolationFact) rvs.next();
	    ImportFact impFact = (ImportFact) rvFact.getFact();

	    System.err.println("DuplicateImport: " + impFact.getACU() + "/" +
			       impFact.getImportPackage() + "/" +
			       Integer.toString( impFact.getLineNumber() ));
	}

	assertEquals("Expecting no violations",
		     0, results.size() );
    }

    public void testDupeOnDemand()
	throws Throwable
    {
	Set results = collectViolations( new DuplicateImport(),
					 JAVA_DUPE_ON_DEMAND );
	
	assertEquals("Expecting 2 violations",
		     2, results.size() );
    }

    public void testDupeNoDemand()
	throws Throwable
    {
	Set results = collectViolations( new DuplicateImport(),
					 JAVA_DUPE_NO_DEMAND );
	assertEquals("Expecting 2 violations",
		     2, results.size() );
    }

    public void testDupeOnNoDemand()
	throws Throwable
    {
	Set results = collectViolations( new DuplicateImport(),
					 JAVA_DUPE_ON_NO_DEMAND );
	assertEquals("Expecting 2 violations",
		     2, results.size() );
    }

}
