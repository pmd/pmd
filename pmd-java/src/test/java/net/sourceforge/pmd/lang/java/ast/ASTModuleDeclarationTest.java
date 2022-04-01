/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

public class ASTModuleDeclarationTest extends BaseParserTest {

    @Test
    public void testAnnotatable() {
        ASTCompilationUnit root = java9.parse("@A @B module foo { } ");
        ASTModuleDeclaration mod = root.getModuleDeclaration();
        assertTrue(mod.isAnnotationPresent("A"));
        assertTrue(mod.isAnnotationPresent("B"));
    }

}
