/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class StringLiteralsTest extends AbstractPLSQLParserTst {


    @Test
    public void parseStringLiterals() throws Exception {
        ASTInput input = plsql.parseResource("StringLiterals.pls");
        List<ASTStringLiteral> strings = input.findDescendantsOfType(ASTStringLiteral.class);
        Assert.assertEquals(20, strings.size());

        assertString("'Hello'", "Hello", 0, strings);
        assertString("N'nchar literal'", "nchar literal", 4, strings);
        assertString("nQ'[ab']cd]'", "ab']cd", 11, strings);
        assertString("Q'{SELECT * FROM employees WHERE last_name = 'Smith';}'",
                "SELECT * FROM employees WHERE last_name = 'Smith';", 13, strings);
        assertString("q'{\n" + "    also multiple\n" + "    lines\n" + "  }'",
                "\n" + "    also multiple\n" + "    lines\n" + "  ", 15, strings);
    }

    @Test
    public void parseMultilineVarchar() throws Exception {
        ASTInput input = plsql.parseResource("MultilineVarchar.pls");
        List<ASTStringLiteral> strings = input.findDescendantsOfType(ASTStringLiteral.class);
        Assert.assertEquals(1, strings.size());
        Assert.assertTrue(normalizeEol(strings.get(0).getString()).startsWith("\ncreate or replace and"));
    }

    private static void assertString(String quoted, String plain, int index, List<ASTStringLiteral> strings) {
        Assert.assertEquals(quoted, normalizeEol(strings.get(index).getImage()));
        Assert.assertEquals(plain, normalizeEol(strings.get(index).getString()));
    }

    private static String normalizeEol(String s) {
        return s.replaceAll("\r\n|\n\r|\n|\r", "\n");
    }
}
