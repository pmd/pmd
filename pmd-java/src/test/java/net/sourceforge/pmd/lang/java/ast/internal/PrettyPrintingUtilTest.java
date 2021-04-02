/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class PrettyPrintingUtilTest extends BaseNonParserTest {

    @Test
    void displaySignatureTestWithExtraDimensions() {
        ASTCompilationUnit root = java.parse("class A { public void foo(String[] a[]) {} ");
        ASTMethodDeclaration m = root.descendants(ASTMethodDeclaration.class).firstOrThrow();

        assertEquals("foo(String[][])", PrettyPrintingUtil.displaySignature(m));
    }
}
