/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.dfa.Structure;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class StructureTest extends BaseNonParserTest {

    @Test
    public void testAddResultsinDFANodeContainingAddedNode() {
        ASTMethodDeclaration n = java.parse("class Foo { void foo() { } }").descendants(ASTMethodDeclaration.class).first();
        Structure s = new Structure(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion()
                .getLanguageVersionHandler().getDataFlowHandler());
        assertEquals(n, s.createNewNode(n).getNode());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StructureTest.class);
    }
}
