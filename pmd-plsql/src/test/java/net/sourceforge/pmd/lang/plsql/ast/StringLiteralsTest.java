/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class StringLiteralsTest extends AbstractPLSQLParserTst {


    @Test
    public void parseStringLiterals() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("StringLiterals.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
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
        String code = IOUtils.toString(this.getClass().getResourceAsStream("MultilineVarchar.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTStringLiteral> strings = input.findDescendantsOfType(ASTStringLiteral.class);
        Assert.assertEquals(1, strings.size());
        Assert.assertTrue(strings.get(0).getString().startsWith("\ncreate or replace and"));
    }

    private static void assertString(String quoted, String plain, int index, List<ASTStringLiteral> strings) {
        Assert.assertEquals(quoted, strings.get(index).getImage());
        Assert.assertEquals(plain, strings.get(index).getString());
    }
}
