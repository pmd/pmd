/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTFormalParameterTest extends BaseParserTest {

    @Test
    public void testVarargs() {
        int nrOfVarArgs = 0;
        int nrOfNoVarArgs = 0;

        List<ASTFormalParameter> ops = java.getNodes(ASTFormalParameter.class, TEST1, "1.5");
        for (ASTFormalParameter b : ops) {
            ASTVariableDeclaratorId variableDeclId = b.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
            if (!"x".equals(variableDeclId.getImage())) {
                assertTrue(b.isVarargs());
                nrOfVarArgs++;
            } else {
                assertFalse(b.isVarargs());
                nrOfNoVarArgs++;
            }
        }

        // Ensure that both possibilities are tested
        assertEquals(1, nrOfVarArgs);
        assertEquals(1, nrOfNoVarArgs);
    }

    private static final String TEST1 = "class Foo {\n void bar(int x, int... others) {}\n}";
}
