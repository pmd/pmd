package test.net.sourceforge.pmd.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.cpd.ReportException;
import net.sourceforge.pmd.util.CollectionUtil;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class PropertyAccessorTest extends SimpleAggregatorTst {

    private AbstractRule rule;

    @Before
    public void setUp() {
        rule = new NonRuleWithAllPropertyTypes();
    }

    public static boolean areEqual(int[] a, int[] b) {
    	if (a.length != b.length) return false;
    	for (int i=0; i<a.length; i++) {
    		if (a[i] != b[i]) return false;
    	}
    	return true;
    }
   
    public static boolean areEqual(boolean[] a, boolean[] b) {
    	if (a.length != b.length) return false;
    	for (int i=0; i<a.length; i++) {
    		if (a[i] != b[i]) return false;
    	}
    	return true;
    }
    
    @Test
    public void testIntegers() {
    	rule.setProperty(NonRuleWithAllPropertyTypes.singleInt, new Integer(0));
        assertTrue(rule.getIntProperty(NonRuleWithAllPropertyTypes.singleInt) == 0);
        
    	rule.setProperties(NonRuleWithAllPropertyTypes.multiInt, new Object[] {new Integer(0), new Integer(1)});
        assertTrue(areEqual(rule.getIntProperties(NonRuleWithAllPropertyTypes.multiInt), new int[]{0, 1}));
    }
    
    @Test(expected = RuntimeException.class)
    public void testIntegersSingle() {
        rule.setProperties(NonRuleWithAllPropertyTypes.singleInt, new Object[] { new Integer(0), new Integer(1) });
    }

    @Test(expected=RuntimeException.class)
    public void testIntegersMultiple() {
        rule.setProperty(NonRuleWithAllPropertyTypes.multiInt, new Integer(0));
    }
     
    @Test
    public void testBooleans() {

    	rule.setProperty(NonRuleWithAllPropertyTypes.singleBool, Boolean.FALSE);
        assertFalse(rule.getBooleanProperty(NonRuleWithAllPropertyTypes.singleBool));
        
    	rule.setProperties(NonRuleWithAllPropertyTypes.multiBool, new Boolean[] {Boolean.TRUE, Boolean.FALSE});
        assertTrue(areEqual(rule.getBooleanProperties(NonRuleWithAllPropertyTypes.multiBool), new boolean[]{true, false}));
        
    }
    
    @Test(expected = RuntimeException.class)
    public void testBooleansSingle() {
        rule.setProperties(NonRuleWithAllPropertyTypes.singleBool, new Boolean[] { Boolean.TRUE, Boolean.FALSE });
    }

    @Test(expected = RuntimeException.class)
    public void testBooleansMultiple() {

        rule.setProperty(NonRuleWithAllPropertyTypes.multiBool, Boolean.TRUE);
    }
    
    @Ignore
    @Test
    public void testFloats() throws ReportException {
/*
    	rule.setProperty("singleFloat", new Float(0));
        assertTrue(rule.getFloatProperty("singleFloat") == 0f);
        
    	rule.setProperties("multiBool", new Boolean[] {Boolean.TRUE, Boolean.FALSE});
        assertTrue(areEqual(rule.getBooleanProperties("multiBool"), new boolean[]{true, false}));
        
        boolean exceptionOccurred = false;
        try {
        	rule.setProperties("singleBool", new Boolean[] {Boolean.TRUE, Boolean.FALSE});
        	} catch (Exception ex) {
        		exceptionOccurred = true;
        	}
        assertTrue(exceptionOccurred);
        
        exceptionOccurred = false;
        try {
        	rule.setProperty("multiBool", Boolean.TRUE);
        	} catch (Exception ex) {
        		exceptionOccurred = true;
        	}
        assertTrue(exceptionOccurred);
*/    }
    
    @Test
    public void testStrings() {
    	rule.setProperty(NonRuleWithAllPropertyTypes.singleStr, "brian");
        assertEquals(rule.getStringProperty(NonRuleWithAllPropertyTypes.singleStr), "brian");
        
    	rule.setProperties(NonRuleWithAllPropertyTypes.multiStr, new String[] {"hello", "world"});
    	assertTrue(CollectionUtil.arraysAreEqual(rule.getStringProperties(NonRuleWithAllPropertyTypes.multiStr),  new String[] {"hello", "world"}));
    }

    @Test(expected = RuntimeException.class)
    public void testStringsSingle() {
        rule.setProperties(NonRuleWithAllPropertyTypes.singleStr, new String[] { "hello", "world" });
    }

    @Test(expected = RuntimeException.class)
    public void testStringsMultiple() {
        rule.setProperty(NonRuleWithAllPropertyTypes.multiStr, "brian");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PropertyAccessorTest.class);
    }
}
