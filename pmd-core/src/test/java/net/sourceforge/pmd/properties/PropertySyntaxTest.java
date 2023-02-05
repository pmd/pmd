/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.RulesetFactoryTestBase;

/**
 * @author Clément Fournier
 */
class PropertySyntaxTest extends RulesetFactoryTestBase {

    @Test
    void testStringProp() {
        assertValueRoundTrip(PropertyParsingUtil.STRING, "ad", "ad");
        assertValueRoundTrip(PropertyParsingUtil.STRING, "", "");
    }

    @Test
    void testStringListProp() {
        assertValueRoundTrip(PropertyParsingUtil.STRING_LIST, "ad|j", listOf("ad", "j"));
    }


    private <T> void assertValueRoundTrip(PropertySerializer<T> mapper, String input, T expected) {
        T parsed = mapper.fromString(input);
        assertEquals(expected, parsed, "fromString");
        String str = mapper.toString(parsed);
        assertEquals(input, str, "toString");
    }

}
