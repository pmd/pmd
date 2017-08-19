/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

public class ASTFormalParameterTest {

    @Test
    public void testVarargs() {
        int nrOfVarArgs = 0;
        int nrOfNoVarArgs = 0;

        Set<ASTFormalParameter> ops = getNodes(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"),
                ASTFormalParameter.class, TEST1);
        for (Iterator<ASTFormalParameter> iter = ops.iterator(); iter.hasNext();) {
            ASTFormalParameter b = iter.next();
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

    private static final String TEST1 = "class Foo {" + PMD.EOL + " void bar(int x, int... others) {}" + PMD.EOL + "}";
}
