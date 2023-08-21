/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class InOutNoCopyTest extends AbstractPLSQLParserTst {

    @Test
    void parseInOutNoCopy() {
        ASTInput input = plsql.parseResource("InOutNoCopy.pls");
        assertNotNull(input);
        List<ASTFormalParameter> params = input.findDescendantsOfType(ASTFormalParameter.class);
        assertEquals(18, params.size());
        //detailed check of first 6 test cases
        assertFalse(params.get(0).isIn());
        assertFalse(params.get(0).isOut());
        assertFalse(params.get(0).isNoCopy());
        assertTrue(params.get(1).isIn());
        assertFalse(params.get(1).isOut());
        assertFalse(params.get(1).isNoCopy());
        assertFalse(params.get(2).isIn());
        assertTrue(params.get(2).isOut());
        assertFalse(params.get(2).isNoCopy());
        assertTrue(params.get(3).isIn());
        assertTrue(params.get(3).isOut());
        assertFalse(params.get(3).isNoCopy());
        assertTrue(params.get(4).isIn());
        assertTrue(params.get(4).isOut());
        assertTrue(params.get(4).isNoCopy());
        assertFalse(params.get(5).isIn());
        assertTrue(params.get(5).isOut());
        assertTrue(params.get(5).isNoCopy());
        //piecemeal test of other test cases
        assertFalse(params.get(11).isIn());
        assertTrue(params.get(11).isOut());
        assertTrue(params.get(11).isNoCopy());
        assertTrue(params.get(16).isIn());
        assertTrue(params.get(16).isOut());
        assertTrue(params.get(16).isNoCopy());
    }

}
