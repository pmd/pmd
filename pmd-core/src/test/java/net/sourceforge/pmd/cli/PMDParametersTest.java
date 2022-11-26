/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.util.CollectionUtil;

class PMDParametersTest {

    @Test
    void testMultipleDirsAndRuleSets() {
        PmdParametersParseResult result = PmdParametersParseResult.extractParameters(
            "-d", "a", "b", "-R", "x.xml", "y.xml"
        );
        assertMultipleDirsAndRulesets(result);
    }

    @Test
    void testMultipleDirsAndRuleSetsWithCommas() {
        PmdParametersParseResult result = PmdParametersParseResult.extractParameters(
            "-d", "a,b", "-R", "x.xml,y.xml"
        );
        assertMultipleDirsAndRulesets(result);
    }

    @Test
    void testMultipleDirsAndRuleSetsWithRepeatedOption() {
        PmdParametersParseResult result = PmdParametersParseResult.extractParameters(
            "-d", "a", "-d", "b", "-R", "x.xml", "-R", "y.xml"
        );
        assertMultipleDirsAndRulesets(result);
    }

    @Test
    void testNoPositionalParametersAllowed() {
        assertError(
            //                        vvvv
            "-R", "x.xml", "-d", "a", "--", "-d", "b"
        );
    }


    private void assertMultipleDirsAndRulesets(PmdParametersParseResult result) {
        assertFalse(result.isError());
        PMDConfiguration config = result.toConfiguration();
        assertEquals(CollectionUtil.map(config.getInputPathList(), Path::toString), listOf("a", "b"));
        assertEquals(config.getRuleSetPaths(), listOf("x.xml", "y.xml"));
    }

    @Test
    void testEmptyDirOption() {
        assertError("-d", "-R", "y.xml");
    }

    @Test
    void testEmptyRulesetOption() {
        assertError("-R", "-d", "something");
    }

    private void assertError(String... params) {
        PmdParametersParseResult result = PmdParametersParseResult.extractParameters(params);
        assertTrue(result.isError());
    }

}
