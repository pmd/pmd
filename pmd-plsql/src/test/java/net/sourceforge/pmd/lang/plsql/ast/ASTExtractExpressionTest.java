/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ASTExtractExpressionTest extends AbstractPLSQLParserTst {

    @Test
    void testXml() {
        ASTInput unit = plsql.parse("SELECT warehouse_name, EXTRACT(warehouse_spec, '/Warehouse/Docks', "
                + "'xmlns:a=\"http://warehouse/1\" xmlns:b=\"http://warehouse/2\"') \"Number of Docks\" "
                + " FROM warehouses WHERE warehouse_spec IS NOT NULL;");
        ASTExtractExpression extract = unit.getFirstDescendantOfType(ASTExtractExpression.class);
        assertTrue(extract.isXml());
        assertEquals("/Warehouse/Docks", extract.getXPath());
        assertEquals("xmlns:a=\"http://warehouse/1\" xmlns:b=\"http://warehouse/2\"", extract.getNamespace());
    }
}
