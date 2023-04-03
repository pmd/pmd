/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class StringLiteralsTest extends AbstractPLSQLParserTst {


    @Test
    void parseStringLiterals() throws Exception {
        ASTInput input = plsql.parseResource("StringLiterals.pls");
        List<ASTStringLiteral> strings = input.findDescendantsOfType(ASTStringLiteral.class);
        assertEquals(20, strings.size());

        assertString("'Hello'", "Hello", 0, strings);
        assertString("N'nchar literal'", "nchar literal", 4, strings);
        assertString("nQ'[ab']cd]'", "ab']cd", 11, strings);
        assertString("Q'{SELECT * FROM employees WHERE last_name = 'Smith';}'",
                "SELECT * FROM employees WHERE last_name = 'Smith';", 13, strings);
        assertString("q'{\n" + "    also multiple\n" + "    lines\n" + "  }'",
                "\n" + "    also multiple\n" + "    lines\n" + "  ", 15, strings);
    }

    @Test
    void parseMultilineVarchar() throws Exception {
        ASTInput input = plsql.parseResource("MultilineVarchar.pls");
        List<ASTStringLiteral> strings = input.findDescendantsOfType(ASTStringLiteral.class);
        assertEquals(1, strings.size());
        assertTrue(normalizeEol(strings.get(0).getString()).startsWith("\ncreate or replace and"));
    }

    private static void assertString(String quoted, String plain, int index, List<ASTStringLiteral> strings) {
        assertEquals(quoted, normalizeEol(strings.get(index).getImage()));
        assertEquals(plain, normalizeEol(strings.get(index).getString()));
    }

    private static String normalizeEol(String s) {
        return s.replaceAll("\r\n|\n\r|\n|\r", "\n");
    }
}
