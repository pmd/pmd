package test.net.sourceforge.pmd.rx;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.io.StringReader;

import org.drools.*;
import org.drools.spi.*;
import org.drools.semantic.java.*;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rx.*;
import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.rx.facts.*;

import junit.framework.TestCase;

public class DroolsVisitorTest
    extends TestCase
{
    private String testName = null;

    private static String JAVA_TEST_PACKAGE =
	"package test;" +
	"public class HelloWorld { }";

    private static String JAVA_TEST_IMPORT_1 =
	"import java.util.*;" +
	"public class HelloWorld { }";

    private static String JAVA_TEST_IMPORT_2 =
	"import java.util.*;" +
	"import java.lang.ref.*;" +
	"public class HelloWorld { }";

    private static String JAVA_TEST_IMPORT_SINGLE =
	"import java.util.Map;" +
	"public class HelloWorld { }";

    private static String JAVA_TEST_IMPORT_DUP =
	"import java.util.*;" +
	"import java.util.*;" +
	"public class HelloWorld { }";

    private static String JAVA_TEST_CLASS =
	"class TestClass { }";

    private static String JAVA_TEST_CLASS_PUBLIC =
	"public class TestClass { }";

    private static String JAVA_TEST_CLASS_ABSTRACT =
	"abstract class TestClass { }";

    private static String JAVA_TEST_CLASS_INNER =
	"class Outer { class Inner { } }";

    private static String JAVA_TEST_CLASS_ANON =
	"class Named { " +
	"  void foo() { Named.bar( new Runnable() { } ); } " +
        "}";

    private ASTCompilationUnit current = null;

    public DroolsVisitorTest(String name) {
	super( name );
	this.testName = name;
    }

    public void testPackage() throws Throwable {
	Set results =
	    collectFacts( net.sourceforge.pmd.rx.facts.PackageFact.class,
			  JAVA_TEST_PACKAGE );
	assertEq("Expected one result.",
		 1, results.size() );
	Iterator facts = results.iterator();
	Object fact = facts.next();

	assertT( "Expected PackageFact.",
		 fact instanceof PackageFact );

	PackageFact pkgFact = (PackageFact) fact;
	assertEq("Expecting package name of 'test'",
		 "test", pkgFact.getPackageName() );
    }

    public void testImport1() throws Throwable {
	Set results =
	    collectFacts( net.sourceforge.pmd.rx.facts.ImportFact.class,
			  JAVA_TEST_IMPORT_1 );
	assertEq("Expected one result.",
		 1, results.size() );

	Iterator imports = results.iterator();
	Object fact = imports.next();

	assertT("Expected type of ImportFact",
		fact instanceof ImportFact);
	ImportFact impFact = (ImportFact) fact;
	assertEq("Expecting 'java.util' in Imports.",
		 "java.util", impFact.getImportPackage() );
    }

    public void testImport2() throws Throwable {
	Set results =
	    collectFacts( net.sourceforge.pmd.rx.facts.ImportFact.class,
			  JAVA_TEST_IMPORT_2 );
	assertEq("Expected two results.",
		 2, results.size() );

	boolean sawUtil = false;
	boolean sawRef = false;
	ACUFact acu = null;

	Iterator imports = results.iterator();
	while (imports.hasNext()) {
	    Object fact = imports.next();

	    assertT("Expected type of ImportFact",
		    fact instanceof ImportFact);
	    ImportFact impFact = (ImportFact) fact;

	    if (acu == null) {
		acu = impFact.getACU();
	    }
	    
	    assertEq("Expecting Same ACU",
		     acu, acu );

	    sawUtil = sawUtil |
		impFact.getImportPackage().equals("java.util");
	    sawRef = sawRef |
		impFact.getImportPackage().equals("java.lang.ref");
	}
	assertT("Expecting one import of 'java.util'", sawUtil);
	assertT("Expecting one import of 'java.lang.ref'", sawRef);
    }

    public void testImportDup() throws Throwable {
	Set results =
	    collectFacts( net.sourceforge.pmd.rx.facts.ImportFact.class,
			  JAVA_TEST_IMPORT_DUP );
	assertEq("Expected two results.",
		 2, results.size() );

	ACUFact acu = null;

	Iterator dups = results.iterator();
	while (dups.hasNext()) {
	    Object fact = dups.next();
	    
	    assertT( "Expected fact to be of ImportFact type.",
		     fact instanceof ImportFact);
	    ImportFact impFact = (ImportFact) fact;

	    if (acu == null) {
		acu = impFact.getACU();
	    }
	    
	    assertEq("Expecting Same ACU",
		     acu, acu );
	    
	    assertT( "Should be On Demand.",
		     impFact.isOnDemand() );
	    assertEq("Expected java.util.Map to be imported.",
		     "java.util", impFact.getImportPackage() );
	}
    }

    public void testImportSingle() throws Throwable {
	Set results =
	    collectFacts( net.sourceforge.pmd.rx.facts.ImportFact.class,
			  JAVA_TEST_IMPORT_SINGLE );
	assertEq("Expected one result.",
		 1, results.size() );

	Iterator singles = results.iterator();
	Object fact = singles.next();

	assertT( "Expected fact to be of ImportFact type.",
		 fact instanceof ImportFact );
	
	ImportFact impFact = (ImportFact) fact;

	assertT( "Should not be listed as On Demand.",
		 !impFact.isOnDemand() );
	assertEq("Expected java.util.Map to be imported.",
		 "java.util.Map", impFact.getImportPackage() );
    }

    public void testClass() throws Throwable {
	Set results =
	    collectFacts( net.sourceforge.pmd.rx.facts.ClassFact.class,
			  JAVA_TEST_CLASS );
	assertEq( "Expected one result.",
		  1, results.size() );

	Iterator facts = results.iterator();
	Object fact = facts.next();

	assertT("Expected fact to be of ClassFact type.",
		fact instanceof ClassFact );

	ClassFact classFact = (ClassFact) fact;

	assertEq("Expected ClassName to be 'TestClass'",
		 "TestClass", classFact.getClassName() );
	assertEq("Expected outer class to be 'null'",
		 null, classFact.getOuterClass() );
	verifyClassFlags( classFact, false, false, false, false );
    }

    public void testClassPublic() throws Throwable {
	Set results =  
	    collectFacts( net.sourceforge.pmd.rx.facts.ClassFact.class,
			  JAVA_TEST_CLASS_PUBLIC );

	assertEq( "Expected one result.",
		  1, results.size() );

	Iterator singles = results.iterator();
	Object fact = singles.next();

	assertT( "Expected fact to be of ClassFact type.",
		 fact instanceof ClassFact );
	
	ClassFact classFact = (ClassFact) fact;

	assertEq("Expected ClassName to be 'TestClass'",
		 "TestClass",  classFact.getClassName() );
	assertEq("Expected outer class to be 'null'",
		 null, classFact.getOuterClass() );
	verifyClassFlags( classFact, true, false, false, false );
    }

    public void testClassAbstract() throws Throwable {
	Set results =  
	    collectFacts( net.sourceforge.pmd.rx.facts.ClassFact.class,
			  JAVA_TEST_CLASS_ABSTRACT );

	assertEq( "Expected one result.",
		  1, results.size() );

	Iterator singles = results.iterator();
	Object fact = singles.next();

	assertT( "Expected fact to be of ClassFact type.",
		 fact instanceof ClassFact );
	
	ClassFact classFact = (ClassFact) fact;

	assertEq("Expected ClassName to be 'TestClass'",
		 "TestClass",  classFact.getClassName() );
	assertEq("Expected outer class to be 'null'",
		 null, classFact.getOuterClass() );
	verifyClassFlags( classFact, false, true, false, false );
    }
    
    public void testClassInner() throws Throwable {
  	Set results =
  	    collectFacts(net.sourceforge.pmd.rx.facts.ClassFact.class,
  			 JAVA_TEST_CLASS_INNER );
  	assertEq("Expecting two results.",
  		 2, results.size() );
	
  	Iterator facts = results.iterator();
	
  	ClassFact outer = null;
  	ClassFact inner = null;
	
  	while (facts.hasNext()) {
  	    Object fact = facts.next();
	    
  	    assertT("Expected both facts to be ClassFact type.",
  		    fact instanceof ClassFact );
	    
  	    ClassFact classFact = (ClassFact) fact;
	    
  	    if (classFact.getClassName().equals("Outer")) {
  		outer = classFact;
  	    }
	    
  	    if (classFact.getClassName().equals("Inner")) {
  		inner = classFact;
  	    }
	}
    }
	
    public void testClassAnon() throws Throwable {
  	Set results =
  	    collectFacts(net.sourceforge.pmd.rx.facts.ClassFact.class,
  			 JAVA_TEST_CLASS_ANON );
  	assertEq("Expecting two results.",
  		 2, results.size() );
	
  	Iterator facts = results.iterator();
	
  	ClassFact named = null;
  	ClassFact anon = null;
	
  	while (facts.hasNext()) {
  	    Object fact = facts.next();
	    
  	    assertT("Expected both facts to be ClassFact type.",
  		    fact instanceof ClassFact );
	    
  	    ClassFact classFact = (ClassFact) fact;
  	    if (classFact.getOuterClass() == null) {
  		named = classFact;
  	    } else {
  		anon = classFact;
  	    }
  	}
	
  	assertNotNull( "Expected to find Named class.", named );
  	assertNotNull( "Expected to find Anon class.", anon );
  	assertEq( "Expected both classes to have same ACU.",
  		  named.getACU(), anon.getACU() );
  	assertEq( "Expected Outer Class of Anon to be Named.",
  		  named, anon.getOuterClass() );
    }
    
    public void assertEq( String message,
			  Object expected,
			  Object result ) throws Throwable
    {
	try {
	    assertEquals( message, expected, result );
	} catch (Throwable t) {
	    current.dump(testName + ": ");
	    throw t;
	}
    }
    
    public void assertEq( String message,
			  boolean expected,
			  boolean result ) throws Throwable
    {
	try {
	    assertEquals( message, expected, result );
	} catch (Throwable t) {
	    current.dump(testName + ": ");
	    throw t;
	}
    }

    public void assertEq( String message,
			  int expected,
			  int result ) throws Throwable
    {
	try {
	    assertEquals( message, expected, result );
	} catch (Throwable t) {
	    current.dump(testName + ": ");
	    throw t;
	}
    }

    public void assertT( String message,
			 boolean cond ) throws Throwable
    {
	try {
	    assertTrue( message, cond );
	} catch (Throwable t) {
	    current.dump(testName + ": ");
	    throw t;
	}
    }

    public Set collectFacts( Class clazz,
			     String javaCode )
	throws Throwable
    {
	RuleBase rules = new RuleBase();
	FactCollector collector = new FactCollector( clazz );
	
	rules.addRule( collector );

	JavaParser parser =
	    new JavaParser( new StringReader( javaCode ));
	ASTCompilationUnit acu =
	    parser.CompilationUnit();

	current = acu;

	WorkingMemory memory = rules.createWorkingMemory();

	DroolsVisitor IUT = new DroolsVisitor( memory );
	IUT.visit( acu, null );

	return collector.getFacts();
    }


    public void verifyClassFlags( ClassFact classFact,
				  boolean bPublic,
				  boolean bAbstract,
				  boolean bFinal,
				  boolean bStrict ) 
	throws Throwable
    {
	assertEq("Public: ",
		 bPublic, classFact.isPublic() );
	assertEq("Abstract: ",
		 bAbstract, classFact.isAbstract() );
	assertEq("Final: ",
		 bFinal, classFact.isFinal() );
	assertEq("Strict: ",
		 bStrict, classFact.isStrict() );
    }
}
