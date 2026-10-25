/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ASTComparisonConditionTest extends AbstractPLSQLParserTst {

    @Test
    void testOperator() {
        ASTInput input = plsql.parse("BEGIN SELECT COUNT(1) INTO MY_TABLE FROM USERS_TABLE WHERE user_id = 1; END;");
        List<ASTComparisonCondition> conditions = input.descendants(ASTComparisonCondition.class).toList();
        assertEquals(1, conditions.size());
        assertEquals("=", conditions.get(0).getOperator());
    }

    @Test
    void testParenthesizedArithmeticLeftOperand() {
        ASTInput input = plsql.parse(""
            + "CREATE OR REPLACE PACKAGE BODY test_pkg AS\n"
            + "  PROCEDURE test_proc IS\n"
            + "  BEGIN\n"
            + "    MERGE INTO destino d\n"
            + "    USING (\n"
            + "      SELECT ap.id\n"
            + "      FROM articulos_pend ap\n"
            + "      WHERE ap.activo = 'S'\n"
            + "      AND (NVL(ap.cantidad, 0) - NVL(ap.cant_pend_salida, 0)) > 0\n"
            + "    ) src\n"
            + "    ON (d.id = src.id)\n"
            + "    WHEN MATCHED THEN UPDATE SET d.cantidad = src.cantidad;\n"
            + "  END test_proc;\n"
            + "END test_pkg;\n");
        List<ASTComparisonCondition> conditions = input.descendants(ASTComparisonCondition.class).toList();
        assertEquals(1L, conditions.stream().filter(c -> ">".equals(c.getOperator())).count());
    }

    @Test
    void testParenthesizedCondition() {
        ASTInput input = plsql.parse("BEGIN SELECT 1 INTO x FROM t WHERE (a > 0) AND (b < 1); END;");
        List<ASTComparisonCondition> conditions = input.descendants(ASTComparisonCondition.class).toList();
        assertEquals(2, conditions.size());
        assertEquals(">", conditions.get(0).getOperator());
        assertEquals("<", conditions.get(1).getOperator());
    }
}
