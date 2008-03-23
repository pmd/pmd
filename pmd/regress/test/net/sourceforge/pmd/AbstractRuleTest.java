/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.symboltable.SourceFileScope;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
public class AbstractRuleTest {
	
    private static class MyRule extends AbstractRule {
    	private static final PropertyDescriptor pd = new StringProperty("foo", "foo property", "x", 1.0f);

    	private static final PropertyDescriptor xpath = new StringProperty("xpath", "xpath property", "", 2.0f);

        private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap(new PropertyDescriptor[] { pd, xpath });

        protected Map<String, PropertyDescriptor> propertiesByName() {
        	return propertyDescriptorsByName;
        }

        public MyRule() {
            setName("MyRule");
            setMessage("my rule msg");
            setPriority(3);
            setProperty(pd, "value");
        }
    }

    private static class MyOtherRule extends AbstractRule {
    	private static final PropertyDescriptor pd = new StringProperty("foo", "foo property", "x", 1.0f);

		private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap(new PropertyDescriptor[] { pd });

        protected Map<String, PropertyDescriptor> propertiesByName() {
        	return propertyDescriptorsByName;
        }

		public MyOtherRule() {
            setName("MyOtherRule");
            setMessage("my other rule");
            setPriority(3);
            setProperty(pd, "value");
        }
    }

    @Test
    public void testCreateRV() {
        MyRule r = new MyRule();
        r.setRuleSetName("foo");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        s.setScope(new SourceFileScope("foo"));
        RuleViolation rv = new RuleViolation(r, ctx, s);
        assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule msg mismatch!", "my rule msg", rv.getDescription());
        assertEquals("RuleSet name mismatch!", "foo", rv.getRule().getRuleSetName());
    }

    @Test
    public void testCreateRV2() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        s.setScope(new SourceFileScope("foo"));
        RuleViolation rv = new RuleViolation(r, ctx, s, "specificdescription");
        assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "specificdescription", rv.getDescription());
    }

    @Test
    public void testRuleExclusion() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(new Integer(5), "");
        ctx.setReport(new Report());
        ctx.excludeLines(m);
        ctx.setSourceCodeFilename("filename");
        SimpleNode n = new SimpleJavaNode(1);
        n.testingOnly__setBeginColumn(5);
        n.testingOnly__setBeginLine(5);
        n.setScope(new SourceFileScope("foo"));
        RuleViolation rv = new RuleViolation(r, ctx, n, "specificdescription");
        ctx.getReport().addRuleViolation(rv);
        assertTrue(ctx.getReport().isEmpty());
    }

    @Test
    public void testEquals1() {
        MyRule r = new MyRule();
        assertFalse("A rule is never equals to null!", r.equals(null));
    }

    @Test
    public void testEquals2() {
        MyRule r = new MyRule();
        assertEquals("A rule must be equals to itself", r, r);
    }

    @Test
    public void testEquals3() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        assertEquals("Two instances of the same rule are equal", r1, r2);
        assertEquals("Hashcode for two instances of the same rule must be equal", r1.hashCode(), r2.hashCode());
    }

    @Test
    public void testEquals4() {
        MyRule myRule = new MyRule();
        assertFalse("A rule cannot be equal to an object of another class", myRule.equals("MyRule"));
    }

    @Test
    public void testEquals5() {
        MyRule myRule = new MyRule();
        MyOtherRule myOtherRule = new MyOtherRule();
        assertFalse("Two rules from different classes cannot be equal", myRule.equals(myOtherRule));
    }

    @Test
    public void testEquals6() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setName("MyRule2");
        assertFalse("Rules with different names cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals7() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setPriority(1);
        assertFalse("Rules with different priority levels cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals8() {
        MyRule r1 = new MyRule();
        r1.setProperty(MyRule.xpath, "something");
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.xpath, "something else");
        assertFalse("Rules with different properties values cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals9() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.xpath, "something else");
        assertFalse("Rules with different properties cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals10() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setMessage("another message");
        assertTrue("Rules with different messages are still equal", r1.equals(r2));
        assertTrue("Rules that are equal must have the same hashcode", r1.hashCode() == r2.hashCode());
    }


    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AbstractRuleTest.class);
    }
}
