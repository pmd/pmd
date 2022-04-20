/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ParenthesisGroupTest extends AbstractPLSQLParserTst {

    @Test
    public void parseParenthesisGroup0() {
        ASTInput input = plsql.parseResource("ParenthesisGroup0.pls");
        Assert.assertNotNull(input);
    }
    
    @Test
    public void parseParenthesisGroup1() {
        ASTInput input = plsql.parseResource("ParenthesisGroup1.pls");
        Assert.assertNotNull(input);
    }
    
    @Test
    public void parseParenthesisGroup2() {
        ASTInput input = plsql.parseResource("ParenthesisGroup2.pls");
        Assert.assertNotNull(input);
    }
    
}
