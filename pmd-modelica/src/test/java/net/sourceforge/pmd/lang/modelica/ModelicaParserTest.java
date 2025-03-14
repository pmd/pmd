/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import org.junit.jupiter.api.Test;

class ModelicaParserTest {
    @Test
    void testParsingGrapgical() {
        ModelicaParsingHelper.DEFAULT.parseResource("ParserTestGraphical.mo");
    }

    @Test
    void testParsingTextual() {
        ModelicaParsingHelper.DEFAULT.parseResource("ParserTestTextual.mo");
    }
}
