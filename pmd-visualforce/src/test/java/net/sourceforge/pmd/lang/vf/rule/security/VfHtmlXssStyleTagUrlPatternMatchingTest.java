/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/***
 * Unit tests to focus on regex pattern used to identify URL methods within style tags
 */
class VfHtmlXssStyleTagUrlPatternMatchingTest {


    @Test
    void testUrlMethodPatternMatchForPositive() {
        final String sampleString = "div {  background: url(blah";
        assertTrue(VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString), "Sample should be considered as starting a URL method: " + sampleString);
    }

    @Test
    void testUrlMethodPatternMatchForCaseInsensitive() {
        final String sampleString = "div {  background: uRl(";
        assertTrue(VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString), "Sample should be considered as starting a URL method: " + sampleString);
    }

    @Test
    void testUrlMethodPatternMatchForWhitespaceAfterUrl() {
        final String sampleString = "div {  background: url (";
        assertTrue(VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString), "Sample should be considered as starting a URL method: " + sampleString);
    }

    @Test
    void testUrlMethodPatternMatchForClosedUrl() {
        final String sampleString = "div {  background: url('myUrl')";
        assertFalse(VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString), "Sample should not be considered as starting a URL method: " + sampleString);
    }

    @Test
    void testUrlMethodPatternMatchForClosedUrlWithNoContent() {
        final String sampleString = "div {  background: url() ";
        assertFalse(VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString), "Sample should not be considered as starting a URL method: " + sampleString);
    }

    @Test
    void testUrlMethodPatternMatchForUrlNoBracket() {
        final String sampleString = "div {  background: url";
        assertFalse(VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString), "Sample should not be considered as starting a URL method: " + sampleString);
    }

    @Test
    void testUrlMethodPatternMatchForNoUrl() {
        final String sampleString = "div {  background: myStyle('";
        assertFalse(VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString), "Sample should not be considered as starting a URL method: " + sampleString);
    }
}
