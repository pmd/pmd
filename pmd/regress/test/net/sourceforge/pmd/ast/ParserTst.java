package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;

import java.util.Set;
import java.util.HashSet;

import java.io.StringReader;

import java.lang.reflect.*;

import net.sourceforge.pmd.ast.*;

public class ParserTst 
    extends TestCase
{
    private class Collector
	implements InvocationHandler
    {
	private Class clazz = null;
	private Set collection = new HashSet();

	public Collector( Class clazz ) {
	    this.clazz = clazz;
	}

	public Set getCollection() {
	    return collection;
	}

	public Object invoke( Object proxy,
			      Method method,
			      Object params[] ) 
	    throws Throwable
	{
	    if (method.getName().equals("visit")) {
		if (clazz.isInstance( params[0] )) {
		    collection.add( params[0] );
		}
	    }

	    Method childrenAccept = 
		params[0].getClass().getMethod( "childrenAccept",
						new Class[] { JavaParserVisitor.class,
							      Object.class } );
	    childrenAccept.invoke( params[0], 
				   new Object[] { proxy, null } );
	    return null;
	}
    }

    public Set getNodes( Class clazz,
			 String javaCode )
	throws Throwable
    {
	Collector coll = new Collector( clazz );
	JavaParser parser = new JavaParser( new StringReader( javaCode ));

	ASTCompilationUnit cu = parser.CompilationUnit();

	JavaParserVisitor jpv = 
	    (JavaParserVisitor) 
	    Proxy.newProxyInstance( JavaParserVisitor.class.getClassLoader(),
				    new Class[] { JavaParserVisitor.class },
				    coll );
	jpv.visit( cu, null );
	return coll.getCollection();
    }
}
