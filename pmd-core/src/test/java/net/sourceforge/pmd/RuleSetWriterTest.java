/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link RuleSetWriter}.
 *
 */
public class RuleSetWriterTest {

    /**
     * Tests the exclude rule behavior.
     * See bug #945.
     * @throws Exception any error
     */
    @Test
    public void testWrite() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RuleSetWriter writer = null;
        try {
            writer = new RuleSetWriter(out);

            RuleSet ruleSet = new RuleSet();
            RuleSet braces = new RuleSetFactory().createRuleSet("net/sourceforge/pmd/TestRuleset1.xml");
            ruleSet.addRuleSetByReference(braces, true, "MockRule2");

            writer.write(ruleSet);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        String written = out.toString("UTF-8");
        Assert.assertTrue(written.contains("<exclude name=\"MockRule2\""));
    }
}
