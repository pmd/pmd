package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.*;

import java.util.Set;
import java.util.Iterator;

public class SimpleNodeTest
    extends ParserTst
{

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
	String javaCode = "public class Foo { }\n"; // 1, 8 -> 1, 20

	Set uCD = getNodes( ASTUnmodifiedClassDeclaration.class,
					    javaCode );
	Iterator iter = uCD.iterator();
	assertTrue( iter.hasNext() );
	verifyNode( (SimpleNode) iter.next(),
		    1, 8, 1, 20 );
	
    }

    public void testNames() throws Throwable {
        String code = "import java.io.File; \n public class Foo{}";
        Set name = getNodes(ASTName.class, code);
        Iterator i = name.iterator();
        assertTrue(i.hasNext());

        while (i.hasNext()) {
            SimpleNode node = (SimpleNode) i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 1, 16, 1, 19);
            }

        }
    }

    public void testNames2() throws Throwable {
        String code = "import java.io.\nFile; \n public class Foo{}";
        Set name = getNodes(ASTName.class, code);
        Iterator i = name.iterator();
        assertTrue(i.hasNext());

        while (i.hasNext()) {
            SimpleNode node = (SimpleNode) i.next();
            if (node.getImage().equals("java.io.File")) {
                verifyNode(node, 2, 1, 2, 4);
                // This is a BUG!  Should start on line 1.
            }

            if (node.getImage().equals("Foo")) {
                verifyNode(node, 2, 15, 2, 18);
            }

        }
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
