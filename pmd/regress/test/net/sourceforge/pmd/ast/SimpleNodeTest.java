package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.*;

import java.util.Set;
import java.util.Iterator;

public class SimpleNodeTest
    extends ParserTst
{
    public SimpleNodeTest( String name ) {
	super( name );
    }

    public void testMethodDiffLines() 
	throws Throwable
    {
	String javaCode = "public class Test {\n";
	javaCode += "  public void helloWorld() \n"; // Line 2, Col 3
	javaCode += "  { System.err.println(\"Hello World\"); \n";
	javaCode += " } \n"; // Line 4, Col 2
	javaCode += "}";

	Set methods = getNodes( ASTMethodDeclaration.class,
				javaCode );
	Iterator iter = methods.iterator();
	assertTrue( iter.hasNext() );
	verifyNode( (SimpleNode) iter.next(),
		    2, 3, 4, 2 );
    }

    public void testMethodSameColumn() 
	throws Throwable
    {
	String javaCode = "public class Test {\n";
	javaCode += "public void helloWorld() {\n"; // Line 2, Col 1
	javaCode += "} \n"; // Line 3, Col 1
	javaCode += "}\n";

	Set methods = getNodes( ASTMethodDeclaration.class,
				javaCode );
	Iterator iter = methods.iterator();
	assertTrue( iter.hasNext() );
	verifyNode( (SimpleNode) iter.next(),
		    2, 1, 3, 1 );
    }

    public void testMethodSameLine() 
	throws Throwable
    {
	String javaCode = "public class Test {\n";
	javaCode += "  public void helloWorld() {}\n"; // 2, 3 -> 2, 29
	javaCode += "}\n";

	Set methods = getNodes( ASTMethodDeclaration.class,
				javaCode );
	Iterator iter = methods.iterator();
	assertTrue( iter.hasNext() );
	verifyNode( (SimpleNode) iter.next(),
		    2, 3, 2, 29 );
    }

    public void testNoLookahead() throws Throwable
    {
	String javaCode = "public class Foo { }\n"; // 1, 7 -> 1, 20

	Set uCD = getNodes( ASTUnmodifiedClassDeclaration.class,
					    javaCode );
	Iterator iter = uCD.iterator();
	assertTrue( iter.hasNext() );
	verifyNode( (SimpleNode) iter.next(),
		    1, 8, 1, 20 );
	
    }

    public void verifyNode( SimpleNode node,
			    int beginLine, int beginCol,
			    int endLine, int endCol ) {
	assertEquals( "Wrong Line Number provided for Start: ",
		      beginLine, node.getBeginLine() );
	assertEquals( "Wrong Column provided for Begin: ",
		      beginCol, node.getBeginColumn() );
	assertEquals( "Wrong Line Number provided for End: ",
		      endLine, node.getEndLine() );
	assertEquals( "Wrong Column provide for End: ",
		      endCol, node.getEndColumn() );
	
    }
}
