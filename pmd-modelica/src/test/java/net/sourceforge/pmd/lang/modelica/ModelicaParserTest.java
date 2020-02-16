/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import org.junit.Test;

public class ModelicaParserTest {
    @Test
    public void testParsingGrapgical() {
        ModelicaParsingHelper.DEFAULT.parseResource("ParserTestGraphical.mo");
    }

    @Test
    public void testParsingTextual() {
        ModelicaParsingHelper.DEFAULT.parseResource("ParserTestTextual.mo");
    }
}
