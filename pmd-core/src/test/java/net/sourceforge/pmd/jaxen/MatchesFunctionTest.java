/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.jaxen;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.FunctionCallException;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.xpath.MatchesFunction;

public class MatchesFunctionTest {

    public static class MyNode extends AbstractNode {
        private String className;

        public MyNode() {
            super(1);
        }

        @Override
        public String toString() {
            return "MyNode";
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }


        @Override
        public String getXPathNodeName() {
            return "MyNode";
        }
    }

    @Test
    public void testMatch() throws FunctionCallException, NoSuchMethodException {
        MyNode myNode = new MyNode();
        myNode.setClassName("Foo");
        assertTrue(tryRegexp(myNode, "Foo") instanceof List);
    }

    @Test
    public void testNoMatch() throws FunctionCallException, NoSuchMethodException {
        MyNode myNode = new MyNode();
        myNode.setClassName("bar");
        assertTrue(tryRegexp(myNode, "Foo") instanceof Boolean);
        myNode.setClassName("FobboBar");
        assertTrue(tryRegexp(myNode, "Foo") instanceof Boolean);
    }

    private Object tryRegexp(MyNode myNode, String exp) throws FunctionCallException, NoSuchMethodException {
        MatchesFunction function = new MatchesFunction();
        List<Object> list = new ArrayList<>();
        List<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute(myNode, "matches", myNode.getClass().getMethod("getClassName", new Class[0])));
        list.add(attrs);
        list.add(exp);
        Context c = new Context(null);
        c.setNodeSet(new ArrayList<>());
        return function.call(c, list);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MatchesFunctionTest.class);
    }
}
