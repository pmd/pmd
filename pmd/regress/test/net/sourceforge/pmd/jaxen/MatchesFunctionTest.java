package test.net.sourceforge.pmd.jaxen;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.JavaParserVisitor;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.jaxen.Attribute;
import net.sourceforge.pmd.jaxen.MatchesFunction;
import org.jaxen.Context;
import org.jaxen.FunctionCallException;

import java.util.ArrayList;
import java.util.List;

public class MatchesFunctionTest extends TestCase implements Node {

    public void jjtOpen() {
    }

    public void jjtClose() {
    }

    public void jjtSetParent(Node n) {
    }

    public Node jjtGetParent() {
        return null;
    }

    public void jjtAddChild(Node n, int i) {
    }

    public Node jjtGetChild(int i) {
        return null;
    }

    public int jjtGetNumChildren() {
        return 0;
    }

    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return null;
    }

    private String className;

    public String getValue() {
        return className;
    }

    public void testMatch() throws FunctionCallException, NoSuchMethodException {
        className = "Foo";
        assertTrue(tryRegexp("Foo") instanceof List);
    }

    public void testNoMatch() throws FunctionCallException, NoSuchMethodException {
        className = "bar";
        assertTrue(tryRegexp("Foo") instanceof Boolean);
        className = "FobboBar";
        assertTrue(tryRegexp("Foo") instanceof Boolean);
    }

    private Object tryRegexp(String exp) throws FunctionCallException, NoSuchMethodException {
        MatchesFunction function = new MatchesFunction();
        List list = new ArrayList();
        List attrs = new ArrayList();
        attrs.add(new Attribute(this, "matches", getClass().getMethod("getValue", new Class[0])));
        list.add(attrs);
        list.add(exp);
        Context c = new Context(null);
        c.setNodeSet(new ArrayList());
        return function.call(c, list);
    }
}

 	  	 
