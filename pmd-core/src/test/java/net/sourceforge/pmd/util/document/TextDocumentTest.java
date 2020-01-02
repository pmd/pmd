/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;

public class TextDocumentTest {

    @Test
    public void testSingleLineRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse");

        TextRegion region = doc.createRegion(0, "bonjour".length());

        assertEquals(0, region.getStartOffset());
        assertEquals("bonjour".length(), region.getLength());
        assertEquals("bonjour".length(), region.getEndOffset());

        RegionWithLines withLines = doc.addLineInfo(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1, withLines.getBeginColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
        assertEquals("bonjour".length(), withLines.getEndColumn() - withLines.getBeginColumn());
    }

    @Test
    public void testMultiLineRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse");

        TextRegion region = doc.createRegion("bonjou".length(), "r\noha\ntri".length());

        assertEquals("bonjou".length(), region.getStartOffset());
        assertEquals("r\noha\ntri".length(), region.getLength());
        assertEquals("bonjour\noha\ntri".length(), region.getEndOffset());

        RegionWithLines withLines = doc.addLineInfo(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(3, withLines.getEndLine());
        assertEquals(1 + "bonjou".length(), withLines.getBeginColumn());
        assertEquals(1 + "tri".length(), withLines.getEndColumn());
    }

    @Test
    public void testEmptyRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse");

        TextRegion region = doc.createRegion("bonjour".length(), 0);

        assertEquals("bonjour".length(), region.getStartOffset());
        assertEquals(0, region.getLength());
        assertEquals(region.getStartOffset(), region.getEndOffset());

        RegionWithLines withLines = doc.addLineInfo(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1 + "bonjour".length(), withLines.getBeginColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
    }

}
