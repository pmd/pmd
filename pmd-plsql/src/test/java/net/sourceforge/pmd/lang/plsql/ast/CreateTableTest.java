/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class CreateTableTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCreateTable() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("CreateTable.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);

        // 5th column of first table statement has a inline constraint of type check
        ASTTableColumn columnStatus = input.findChildrenOfType(ASTTable.class).get(0).findChildrenOfType(ASTTableColumn.class).get(4);
        Assert.assertEquals("status", columnStatus.getFirstChildOfType(ASTID.class).getImage());
        Assert.assertEquals(ConstraintType.CHECK, columnStatus.getFirstChildOfType(ASTInlineConstraint.class).getType());
    }

    @Test
    public void parseCreateOrganizedTable() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("CreateOrganizedTable.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
