/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

/**
 * Test scala rulesets.
 */
class RulesetFactoryTest {

    // no rulesets yet
    @Test
    void nothingToTest() throws IOException {
        Properties props = new Properties();
        props.load(RulesetFactoryTest.class.getClassLoader()
            .getResourceAsStream("category/scala/categories.properties"));
        assertEquals("", props.get("rulesets.filenames"),
            "Once rulesets are added, this should extend AbstractRuleSetFactoryTest");
    }
}
