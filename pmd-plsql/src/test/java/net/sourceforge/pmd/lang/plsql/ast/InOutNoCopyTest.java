/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class InOutNoCopyTest extends AbstractPLSQLParserTst {

    @Test
    public void parseInOutNoCopy() {
        ASTInput input = plsql.parseResource("InOutNoCopy.pls");
        Assert.assertNotNull(input);
        List<ASTFormalParameter> params = input.findDescendantsOfType(ASTFormalParameter.class);
        Assert.assertEquals(18, params.size());
        //detailed check of first 6 test cases
        Assert.assertFalse(params.get(0).isIn());
        Assert.assertFalse(params.get(0).isOut());
        Assert.assertFalse(params.get(0).isNoCopy());
        Assert.assertTrue(params.get(1).isIn());
        Assert.assertFalse(params.get(1).isOut());
        Assert.assertFalse(params.get(1).isNoCopy());
        Assert.assertFalse(params.get(2).isIn());
        Assert.assertTrue(params.get(2).isOut());
        Assert.assertFalse(params.get(2).isNoCopy());
        Assert.assertTrue(params.get(3).isIn());
        Assert.assertTrue(params.get(3).isOut());
        Assert.assertFalse(params.get(3).isNoCopy());
        Assert.assertTrue(params.get(4).isIn());
        Assert.assertTrue(params.get(4).isOut());
        Assert.assertTrue(params.get(4).isNoCopy());
        Assert.assertFalse(params.get(5).isIn());
        Assert.assertTrue(params.get(5).isOut());
        Assert.assertTrue(params.get(5).isNoCopy());
        //piecemeal test of other test cases
        Assert.assertFalse(params.get(11).isIn());
        Assert.assertTrue(params.get(11).isOut());
        Assert.assertTrue(params.get(11).isNoCopy());
        Assert.assertTrue(params.get(16).isIn());
        Assert.assertTrue(params.get(16).isOut());
        Assert.assertTrue(params.get(16).isNoCopy());
    }

}
