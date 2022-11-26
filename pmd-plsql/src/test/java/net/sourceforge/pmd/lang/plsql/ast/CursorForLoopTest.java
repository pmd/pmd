/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class CursorForLoopTest extends AbstractPLSQLParserTst {

    @Test
    void parseCursorForLoopSimple() {
        ASTInput input = plsql.parseResource("CursorForLoopSimple.pls");
        ASTCursorForLoopStatement forloop = input.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        assertNotNull(forloop);
        ASTForIndex forindex = forloop.getFirstChildOfType(ASTForIndex.class);
        assertNotNull(forindex);
        assertEquals("someone", forindex.getImage());
    }

    @Test
    void parseCursorForLoopNested() {
        ASTInput input = plsql.parseResource("CursorForLoopNested.pls");
        ASTCursorForLoopStatement forloop = input.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        assertNotNull(forloop);
        ASTForIndex forindex = forloop.getFirstChildOfType(ASTForIndex.class);
        assertNotNull(forindex);
        assertEquals("c_cmp", forindex.getImage());

        ASTCursorForLoopStatement forloop2 = forloop.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        ASTForIndex forindex2 = forloop2.getFirstChildOfType(ASTForIndex.class);
        assertEquals("c_con", forindex2.getImage());

        ASTCursorForLoopStatement forloop3 = forloop2.getFirstDescendantOfType(ASTCursorForLoopStatement.class);
        ASTForIndex forindex3 = forloop3.getFirstChildOfType(ASTForIndex.class);
        assertEquals("c_pa", forindex3.getImage());
    }

    @Test
    void parseCursorForLoop1047a() {
        ASTInput input = plsql.parseResource("CursorForLoop1047a.pls");
        assertNotNull(input);
    }

    @Test
    void parseCursorForLoop1047b() {
        ASTInput input = plsql.parseResource("CursorForLoop1047b.pls");
        assertNotNull(input);
    }

    @Test
    void parseCursorForLoop681() {
        ASTInput input = plsql.parseResource("CursorForLoop681.pls");
        assertNotNull(input);
    }
}
