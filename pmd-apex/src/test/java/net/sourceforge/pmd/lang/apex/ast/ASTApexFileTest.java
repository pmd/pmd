/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ASTApexFileTest extends ApexParserTestBase {
    @Test
    void classDeclaration() {
        ASTApexFile apexFile = apex.parse("class Foo {}");
        assertNotNull(apexFile.getMainNode());
    }

    @Test
    void anonymousBlock() {
        ASTApexFile apexFile = apex.parse("System.debug('test');");
        assertNull(apexFile.getMainNode());
        assertNotNull(apexFile.getAnonymousBlock());
        assertEquals(1, apexFile.getAnonymousBlock().getNumChildren());
        assertInstanceOf(ASTExpressionStatement.class, apexFile.getAnonymousBlock().getFirstChild());
    }
}
