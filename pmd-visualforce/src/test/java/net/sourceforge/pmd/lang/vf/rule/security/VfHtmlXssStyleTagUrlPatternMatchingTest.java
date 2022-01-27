/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/***
 * Unit tests to focus on regex pattern used to identify URL methods within style tags
 */
public class VfHtmlXssStyleTagUrlPatternMatchingTest {


    @Test
    public void testUrlMethodPatternMatchForPositive() {
        final String sampleString = "div {  background: url(blah";
        assertTrue("Sample should be considered as starting a URL method: " + sampleString, VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString));
    }

    @Test
    public void testUrlMethodPatternMatchForCaseInsensitive() {
        final String sampleString = "div {  background: uRl(";
        assertTrue("Sample should be considered as starting a URL method: " + sampleString, VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString));
    }

    @Test
    public void testUrlMethodPatternMatchForWhitespaceAfterUrl() {
        final String sampleString = "div {  background: url (";
        assertTrue("Sample should be considered as starting a URL method: " + sampleString, VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString));
    }

    @Test
    public void testUrlMethodPatternMatchForClosedUrl() {
        final String sampleString = "div {  background: url('myUrl')";
        assertFalse("Sample should not be considered as starting a URL method: " + sampleString, VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString));
    }

    @Test
    public void testUrlMethodPatternMatchForClosedUrlWithNoContent() {
        final String sampleString = "div {  background: url() ";
        assertFalse("Sample should not be considered as starting a URL method: " + sampleString, VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString));
    }

    @Test
    public void testUrlMethodPatternMatchForUrlNoBracket() {
        final String sampleString = "div {  background: url";
        assertFalse("Sample should not be considered as starting a URL method: " + sampleString, VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString));
    }

    @Test
    public void testUrlMethodPatternMatchForNoUrl() {
        final String sampleString = "div {  background: myStyle('";
        assertFalse("Sample should not be considered as starting a URL method: " + sampleString, VfHtmlStyleTagXssRule.isWithinUrlMethod(sampleString));
    }
}
