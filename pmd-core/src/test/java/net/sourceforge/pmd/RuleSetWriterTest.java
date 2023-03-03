/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.rule.RuleReference;

/**
 * Unit test for {@link RuleSetWriter}.
 *
 */
class RuleSetWriterTest {

    private ByteArrayOutputStream out;
    private RuleSetWriter writer;

    /**
     * Prepare the output stream.
     */
    @BeforeEach
    void setupOutputStream() {
        out = new ByteArrayOutputStream();
        writer = new RuleSetWriter(out);
    }

    /**
     * Closes the output stream at the end.
     */
    @AfterEach
    void cleanupStream() {
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
    void testWrite() throws Exception {
        RuleSet braces = new RuleSetLoader().loadFromResource("net/sourceforge/pmd/TestRuleset1.xml");
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .withName("ruleset")
                .withDescription("ruleset description")
                .addRuleSetByReference(braces, true, "MockRule2")
                .build();

        writer.write(ruleSet);

        String written = out.toString("UTF-8");
        assertTrue(written.contains("<exclude name=\"MockRule2\""));
    }

    /**
     * Unit test for #1312 see https://sourceforge.net/p/pmd/bugs/1312/
     *
     * @throws Exception
     *             any error
     */
    @Test
    void testRuleReferenceOverriddenName() throws Exception {
        RuleSet rs = new RuleSetLoader().loadFromResource("rulesets/dummy/basic.xml");

        RuleReference ruleRef = new RuleReference(
                rs.getRuleByName("DummyBasicMockRule"),
                new RuleSetReference("rulesets/dummy/basic.xml"));
        ruleRef.setName("Foo"); // override the name

        RuleSet ruleSet = RuleSet.forSingleRule(ruleRef);

        writer.write(ruleSet);

        String written = out.toString("UTF-8");
        assertTrue(written.contains("ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\""));
    }
}
