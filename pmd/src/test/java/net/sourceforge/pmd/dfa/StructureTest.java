/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dfa;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.Structure;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

import org.junit.Test;

public class StructureTest {

    @Test
    public void testAddResultsinDFANodeContainingAddedNode() {
        Structure s = new Structure(Language.JAVA.getDefaultVersion().getLanguageVersionHandler().getDataFlowHandler());
        Node n = new ASTMethodDeclaration(1);
        assertEquals(n, s.createNewNode(n).getNode());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StructureTest.class);
    }
}
