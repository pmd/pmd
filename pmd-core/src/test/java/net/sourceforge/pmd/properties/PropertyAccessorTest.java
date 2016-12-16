/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.ReportException;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.NumericConstants;

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
        rule.setProperty(NonRuleWithAllPropertyTypes.SINGLE_INT, NumericConstants.ZERO);
        assertSame(rule.getProperty(NonRuleWithAllPropertyTypes.SINGLE_INT), 0);

        rule.setProperty(NonRuleWithAllPropertyTypes.MULTI_INT,
                new Integer[] { NumericConstants.ZERO, NumericConstants.ONE });
        assertArrayEquals(rule.getProperty(NonRuleWithAllPropertyTypes.MULTI_INT), new Integer[] { 0, 1 });
    }

    @Test
    public void testBooleans() {

        rule.setProperty(NonRuleWithAllPropertyTypes.SINGLE_BOOL, Boolean.FALSE);
        assertFalse(rule.getProperty(NonRuleWithAllPropertyTypes.SINGLE_BOOL));

        rule.setProperty(NonRuleWithAllPropertyTypes.MULTI_BOOL, new Boolean[] { Boolean.TRUE, Boolean.FALSE });
        assertArrayEquals(rule.getProperty(NonRuleWithAllPropertyTypes.MULTI_BOOL), new Boolean[] { true, false });
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
         */
    }

    @Test
    public void testStrings() {
        rule.setProperty(NonRuleWithAllPropertyTypes.SINGLE_STR, "brian");
        assertEquals(rule.getProperty(NonRuleWithAllPropertyTypes.SINGLE_STR), "brian");

        rule.setProperty(NonRuleWithAllPropertyTypes.MULTI_STR, new String[] { "hello", "world" });
        assertTrue(CollectionUtil.arraysAreEqual(rule.getProperty(NonRuleWithAllPropertyTypes.MULTI_STR),
                new String[] { "hello", "world" }));
    }
}
