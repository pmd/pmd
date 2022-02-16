/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import static net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil.displaySignature;
import static net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil.prettyPrint;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class PrettyPrintingUtilTest extends BaseNonParserTest {

    @Test
    void displaySignatureTestWithExtraDimensions() {
        ASTCompilationUnit root = java.parse("class A { public void foo(String[] a[]) {} ");
        ASTMethodDeclaration m = root.descendants(ASTMethodDeclaration.class).firstOrThrow();

        assertEquals("foo(String[][])", displaySignature(m));
    }

    @Test
    public void ppMethodCall() {
        ASTCompilationUnit root = java.parse("class A { { foo(1); this.foo(1); A.this.foo(); } }");
        List<ASTMethodCall> m = root.descendants(ASTMethodCall.class).toList();

        assertEquals("foo(1)", prettyPrint(m.get(0)));
        assertEquals("this.foo(1)", prettyPrint(m.get(1)));
        assertEquals("A.this.foo()", prettyPrint(m.get(2)));
    }

    @Test
    public void ppMethodCallArgsTooBig() {
        ASTCompilationUnit root = java.parse("class A { this.foo(\"a long string\", 12, 12, 12, 12, 12); } }");
        @NonNull ASTMethodCall m = root.descendants(ASTMethodCall.class).firstOrThrow();

        assertEquals("this.foo(\"a long string\", 12, ...)", prettyPrint(m));
    }
}
