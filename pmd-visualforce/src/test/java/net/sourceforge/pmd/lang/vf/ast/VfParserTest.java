/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import org.junit.Test;

/**
 * @author sergey.gorbaty
 */
public class VfParserTest extends AbstractVfNodesTest {

    @Test
    public void testSingleDoubleQuoteAndEL() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${!'yes'}</span>");
    }

    @Test
    public void testSingleDoubleQuoteAndELFunction() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${!method}</span>");
    }

    @Test
    public void testSingleDoubleQuote() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${\"yes\"}</span>");
    }

}
