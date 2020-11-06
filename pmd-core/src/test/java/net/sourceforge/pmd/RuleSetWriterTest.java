/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.rule.RuleReference;

/**
 * Unit test for {@link RuleSetWriter}.
 *
 */
public class RuleSetWriterTest {

    private ByteArrayOutputStream out;
    private RuleSetWriter writer;

    /**
     * Prepare the output stream.
     */
    @Before
    public void setupOutputStream() {
        out = new ByteArrayOutputStream();
        writer = new RuleSetWriter(out);
    }

    /**
     * Closes the output stream at the end.
     */
    @After
    public void cleanupStream() {
        if (writer != null) {
            writer.close();
        }
    }

    /**
     * Tests the exclude rule behavior. See bug #945.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testWrite() throws Exception {
        RuleSet braces = RulesetsFactoryUtils.defaultFactory().createRuleSet("net/sourceforge/pmd/TestRuleset1.xml");
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .withName("ruleset")
                .withDescription("ruleset description")
                .addRuleSetByReference(braces, true, "MockRule2")
                .build();

        writer.write(ruleSet);

        String written = out.toString("UTF-8");
        Assert.assertTrue(written.contains("<exclude name=\"MockRule2\""));
    }

    /**
     * Unit test for #1312 see https://sourceforge.net/p/pmd/bugs/1312/
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testRuleReferenceOverriddenName() throws Exception {
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet rs = ruleSetFactory.createRuleSet("dummy-basic");
        RuleSetReference ruleSetReference = new RuleSetReference("rulesets/dummy/basic.xml");

        RuleReference ruleRef = new RuleReference();
        ruleRef.setRule(rs.getRuleByName("DummyBasicMockRule"));
        ruleRef.setRuleSetReference(ruleSetReference);
        ruleRef.setName("Foo"); // override the name

        RuleSet ruleSet = ruleSetFactory.createSingleRuleRuleSet(ruleRef);

        writer.write(ruleSet);

        String written = out.toString("UTF-8");
        Assert.assertTrue(written.contains("ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\""));
    }
}
