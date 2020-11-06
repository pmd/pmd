/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class RuleContextTest {

    @Test
    public void testReport() {
        RuleContext ctx = new RuleContext();
        assertEquals(0, ctx.getReport().size());
        Report r = new Report();
        ctx.setReport(r);
        Report r2 = ctx.getReport();
        assertEquals("report object mismatch", r, r2);
    }

    @Test
    public void testSourceCodeFilename() {
        RuleContext ctx = new RuleContext();
        assertEquals("filename should be empty", "", ctx.getSourceCodeFilename());
        ctx.setSourceCodeFile(new File("dir/foo.java"));
        assertEquals("filename mismatch", "foo.java", ctx.getSourceCodeFilename());
    }

    @Test
    public void testSourceCodeFile() {
        RuleContext ctx = new RuleContext();
        assertNull("file should be null", ctx.getSourceCodeFile());
        ctx.setSourceCodeFile(new File("somefile.java"));
        assertEquals("filename mismatch", new File("somefile.java"), ctx.getSourceCodeFile());
    }

    @Test
    public void testAttributes() {
        RuleContext ctx1 = new RuleContext();
        Object obj1 = new Object();
        Object obj2 = new Object();
        assertNull("attribute should be null", ctx1.getAttribute("attribute"));
        boolean set = ctx1.setAttribute("attribute", obj1);
        assertTrue("attribute should have been set", set);
        assertNotNull("attribute should not be null", ctx1.getAttribute("attribute"));
        assertSame("attribute should be expected instance", ctx1.getAttribute("attribute"), obj1);
        set = ctx1.setAttribute("attribute", obj2);
        assertFalse("attribute should not have been set", set);
        assertSame("attribute should be expected instance", ctx1.getAttribute("attribute"), obj1);
        Object value = ctx1.removeAttribute("attribute");
        assertSame("attribute value should be expected instance", value, obj1);
        assertNull("attribute should be null", ctx1.getAttribute("attribute"));
    }

    @Test
    public void testSharedAttributes() {
        RuleContext ctx1 = new RuleContext();
        RuleContext ctx2 = new RuleContext(ctx1);
        StringBuilder obj1 = new StringBuilder();
        StringBuilder obj2 = new StringBuilder();

        ctx1.setAttribute("attribute1", obj1);
        ctx2.setAttribute("attribute2", obj2);
        assertNotNull("attribute should not be null", ctx1.getAttribute("attribute1"));
        assertNotNull("attribute should not be null", ctx1.getAttribute("attribute2"));
        assertNotNull("attribute should not be null", ctx2.getAttribute("attribute1"));
        assertNotNull("attribute should not be null", ctx2.getAttribute("attribute2"));
        assertSame("attribute should be expected instance", ctx1.getAttribute("attribute1"), obj1);
        assertSame("attribute should be expected instance", ctx1.getAttribute("attribute2"), obj2);
        assertSame("attribute should be expected instance", ctx2.getAttribute("attribute1"), obj1);
        assertSame("attribute should be expected instance", ctx2.getAttribute("attribute2"), obj2);

        ctx1.removeAttribute("attribute1");
        assertNull("attribute should be null", ctx1.getAttribute("attribute1"));
        assertNull("attribute should be null", ctx2.getAttribute("attribute1"));
        assertNotNull("attribute should not be null", ctx1.getAttribute("attribute2"));
        assertNotNull("attribute should not be null", ctx2.getAttribute("attribute2"));

        StringBuilder value = (StringBuilder) ctx1.getAttribute("attribute2");
        assertEquals("attribute value should be empty", "", value.toString());
        value.append("x");
        StringBuilder value1 = (StringBuilder) ctx1.getAttribute("attribute2");
        assertEquals("attribute value should be 'x'", "x", value1.toString());
        StringBuilder value2 = (StringBuilder) ctx2.getAttribute("attribute2");
        assertEquals("attribute value should be 'x'", "x", value2.toString());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleContextTest.class);
    }
}
