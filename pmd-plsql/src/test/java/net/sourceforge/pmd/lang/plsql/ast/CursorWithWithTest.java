/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class CursorWithWithTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCursorWithWith() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("CursorWithWith.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
