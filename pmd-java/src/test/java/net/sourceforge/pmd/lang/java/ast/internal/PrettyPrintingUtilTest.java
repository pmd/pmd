/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import static net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil.displaySignature;
import static net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil.prettyPrint;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.util.StringUtil;

class PrettyPrintingUtilTest extends BaseParserTest {

    @Test
    void displaySignatureTestWithExtraDimensions() {
        ASTCompilationUnit root = java.parse("class A { public void foo(String[] a[]) {} }");
        ASTMethodDeclaration m = root.descendants(ASTMethodDeclaration.class).firstOrThrow();

        assertEquals("foo(String[][])", displaySignature(m));
    }

    @Test
    void ppMethodCall() {
        ASTCompilationUnit root = java.parse("class A { { foo(1); this.foo(1); A.this.foo(); } }");
        List<ASTMethodCall> m = root.descendants(ASTMethodCall.class).toList();

        assertThat(prettyPrint(m.get(0)), contentEquals("foo(1)"));
        assertThat(prettyPrint(m.get(1)), contentEquals("this.foo(1)"));
        assertThat(prettyPrint(m.get(2)), contentEquals("A.this.foo()"));
    }

    @Test
    void ppMethodCallArgsTooBig() {
        ASTCompilationUnit root = java.parse("class A { { this.foo(\"a long string\", 12, 12, 12, 12, 12); } }");
        @NonNull ASTMethodCall m = root.descendants(ASTMethodCall.class).firstOrThrow();

        assertThat(prettyPrint(m), contentEquals("this.foo(\"a long string\", 12...)"));
    }

    @Test
    void ppMethodCallOnCast() {
        ASTCompilationUnit root = java.parse("class A { { ((Object) this).foo(12); } }");
        @NonNull ASTMethodCall m = root.descendants(ASTMethodCall.class).firstOrThrow();

        assertThat(prettyPrint(m), contentEquals("((Object) this).foo(12)"));
    }

    private static Matcher<CharSequence> contentEquals(String str) {
        return new BaseMatcher<CharSequence>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof CharSequence && str.contentEquals((CharSequence) o);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a char sequence with content \"" + StringUtil.escapeJava(str) + "\"");
            }
        };
    }
}
