/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.ReportException;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.NumericConstants;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Brian Remedios
 */
public class PropertyAccessorTest {

    private Rule rule;

    @Before
    public void setUpSingleRule() {
        rule = new NonRuleWithAllPropertyTypes();
    }

    @Test
    public void testIntegers() {
        rule.setProperty(NonRuleWithAllPropertyTypes.singleInt, NumericConstants.ZERO);
        assertSame(rule.getProperty(NonRuleWithAllPropertyTypes.singleInt), 0);

        rule.setProperty(NonRuleWithAllPropertyTypes.multiInt, new Integer[] { NumericConstants.ZERO,
                NumericConstants.ONE });
        assertArrayEquals(rule.getProperty(NonRuleWithAllPropertyTypes.multiInt), new Integer[] { 0, 1 });
    }

    @Test
    public void testBooleans() {

        rule.setProperty(NonRuleWithAllPropertyTypes.singleBool, Boolean.FALSE);
        assertFalse(rule.getProperty(NonRuleWithAllPropertyTypes.singleBool));

        rule.setProperty(NonRuleWithAllPropertyTypes.multiBool, new Boolean[] { Boolean.TRUE, Boolean.FALSE });
        assertArrayEquals(rule.getProperty(NonRuleWithAllPropertyTypes.multiBool), new Boolean[] { true, false });
    }

    @Ignore
    @Test
    public void testFloats() throws ReportException {
        /*
         * rule.setProperty("singleFloat", new Float(0));
         * assertTrue(rule.getFloatProperty("singleFloat") == 0f);
         * 
         * rule.setProperties("multiBool", new Boolean[] {Boolean.TRUE,
         * Boolean.FALSE});
         * assertTrue(areEqual(rule.getBooleanProperties("multiBool"), new
         * boolean[]{true, false}));
         * 
         * boolean exceptionOccurred = false; try {
         * rule.setProperties("singleBool", new Boolean[] {Boolean.TRUE,
         * Boolean.FALSE}); } catch (Exception ex) { exceptionOccurred = true; }
         * assertTrue(exceptionOccurred);
         * 
         * exceptionOccurred = false; try { rule.setProperty("multiBool",
         * Boolean.TRUE); } catch (Exception ex) { exceptionOccurred = true; }
         * assertTrue(exceptionOccurred);
         */}

    @Test
    public void testStrings() {
        rule.setProperty(NonRuleWithAllPropertyTypes.singleStr, "brian");
        assertEquals(rule.getProperty(NonRuleWithAllPropertyTypes.singleStr), "brian");

        rule.setProperty(NonRuleWithAllPropertyTypes.multiStr, new String[] { "hello", "world" });
        assertTrue(CollectionUtil.arraysAreEqual(rule.getProperty(NonRuleWithAllPropertyTypes.multiStr), new String[] {
                "hello", "world" }));
    }
}
