/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;

public class ModelicaParserTest {
    @Test
    public void testParsingGrapgical() {
        ASTStoredDefinition node = ModelicaLoader.parse("ParserTestGraphical.mo");
        Assert.assertNotNull(node);
    }

    @Test
    public void testParsingTextual() {
        ASTStoredDefinition node = ModelicaLoader.parse("ParserTestTextual.mo");
        Assert.assertNotNull(node);
    }
}
