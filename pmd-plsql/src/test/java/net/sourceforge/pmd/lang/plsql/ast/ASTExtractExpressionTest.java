/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ASTExtractExpressionTest extends AbstractPLSQLParserTst {


    @Test
    public void testXml() {
        ASTInput unit = plsql.parse("SELECT warehouse_name, EXTRACT(warehouse_spec, '/Warehouse/Docks', "
                + "'xmlns:a=\"http://warehouse/1\" xmlns:b=\"http://warehouse/2\"') \"Number of Docks\" "
                + " FROM warehouses WHERE warehouse_spec IS NOT NULL;");
        ASTExtractExpression extract = unit.getFirstDescendantOfType(ASTExtractExpression.class);
        Assert.assertTrue(extract.isXml());
        Assert.assertEquals("/Warehouse/Docks", extract.getXPath());
        Assert.assertEquals("xmlns:a=\"http://warehouse/1\" xmlns:b=\"http://warehouse/2\"", extract.getNamespace());
    }
}
