/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class CursorForLoopTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCursorForLoopSimple() {
        ASTInput input = plsql.parseResource("CursorForLoopSimple.pls");
        ASTCursorForLoopStatement forloop = input.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        Assert.assertNotNull(forloop);
        ASTForIndex forindex = forloop.getFirstChildOfType(ASTForIndex.class);
        Assert.assertNotNull(forindex);
        Assert.assertEquals("someone", forindex.getImage());
    }

    @Test
    public void parseCursorForLoopNested() {
        ASTInput input = plsql.parseResource("CursorForLoopNested.pls");
        ASTCursorForLoopStatement forloop = input.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        Assert.assertNotNull(forloop);
        ASTForIndex forindex = forloop.getFirstChildOfType(ASTForIndex.class);
        Assert.assertNotNull(forindex);
        Assert.assertEquals("c_cmp", forindex.getImage());

        ASTCursorForLoopStatement forloop2 = forloop.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        ASTForIndex forindex2 = forloop2.getFirstChildOfType(ASTForIndex.class);
        Assert.assertEquals("c_con", forindex2.getImage());

        ASTCursorForLoopStatement forloop3 = forloop2.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        ASTForIndex forindex3 = forloop3.getFirstChildOfType(ASTForIndex.class);
        Assert.assertEquals("c_pa", forindex3.getImage());
    }

    @Test
    public void parseCursorForLoop1047a() {
        ASTInput input = plsql.parseResource("CursorForLoop1047a.pls");
        Assert.assertNotNull(input);
    }

    @Test
    public void parseCursorForLoop1047b() {
        ASTInput input = plsql.parseResource("CursorForLoop1047b.pls");
        Assert.assertNotNull(input);
    }

    @Test
    public void parseCursorForLoop681() {
        ASTInput input = plsql.parseResource("CursorForLoop681.pls");
        Assert.assertNotNull(input);
    }
}
