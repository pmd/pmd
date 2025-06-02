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
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
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
        testPrettyPrint("this.foo(\"a long string\", 12, 12, 12, 12, 12)",
            ASTMethodCall.class, "this.foo(\"a long string\", 12...)");
    }

    @Test
    void ppMethodCallOnCast() {
        testPrettyPrintIdentity("((Object) this).foo(12)", ASTMethodCall.class);
    }

    @Test
    void ppMethodRef() {
        testPrettyPrintIdentity("ASTW::meth", ASTMethodReference.class);
    }

    @Test
    void ppMethodRefWithTyArgs() {
        testPrettyPrint("foo(ASTW::<String>meth)", ASTMethodReference.class,
            "ASTW::<String>meth");
    }

    @Test
    void ppCtorCall() {
        testPrettyPrintIdentity("new Foo(1)", ASTConstructorCall.class);
    }

    @Test
    void ppUnary() {
        testPrettyPrintIdentity("-+4", ASTUnaryExpression.class);
    }


    @Test
    void ppConditional() {
        testPrettyPrintIdentity("true ? false : 1", ASTConditionalExpression.class);
    }

    @Test
    void ppInfix() {
        // note: removes unnecessary parens
        testPrettyPrint("(1+2)+2", ASTInfixExpression.class, "1 + 2 + 2");
        testPrettyPrint("(1+2)*2", ASTInfixExpression.class, "(1 + 2) * 2");
    }



    @Test
    void ppLambdaExpr() {
        testPrettyPrintIdentity("(a, b) -> new Foo()", ASTLambdaExpression.class);
        testPrettyPrintIdentity("() -> new Foo()", ASTLambdaExpression.class);
        testPrettyPrintIdentity("x -> new Foo()", ASTLambdaExpression.class);
        testPrettyPrint("(x) -> new Foo()", ASTLambdaExpression.class, "x -> new Foo()");
    }

    @Test
    void ppLambdaBlock() {
        testPrettyPrint("(a, b) -> {return new Foo(); }", ASTLambdaExpression.class,
            "(a, b) -> { ... }");
    }

    private <T extends ASTExpression> void testPrettyPrint(String expr, Class<T> nodeTy, String expected) {
        ASTCompilationUnit root = java.parse("class A { { Object x = " + expr + "; } }");
        @NonNull T node = root.descendants(nodeTy).firstOrThrow();
        assertThat(prettyPrint(node), contentEquals(expected));
    }

    private <T extends ASTExpression> void testPrettyPrintIdentity(String expr, Class<T> nodeTy) {
        testPrettyPrint(expr, nodeTy, expr);
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
