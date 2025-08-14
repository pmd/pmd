/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.lang.java.types.InvocationMatcher.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;

class InvocationMatcherTest extends BaseParserTest {

    @Test
    void testSimpleMatcher() {

        ASTMethodCall call =
            java.parse("class Foo {{ Integer.valueOf('c'); }}")
                .descendants(ASTMethodCall.class).firstOrThrow();

        assertMatch(call, "_#valueOf(int)");
        assertMatch(call, "java.lang.Integer#valueOf(int)");
        assertMatch(call, "java.lang.Integer#_(int)");
        assertMatch(call, "java.lang.Integer#_(_*)");

        assertNoMatch(call, "java.lang.Integer#valueOf(char)");
        assertNoMatch(call, "java.lang.Integer#valueOf2(_*)");
        assertNoMatch(call, "java.lang.Object#valueOf(_*)");
    }

    @Test
    void testCtorMatchers() {

        ASTConstructorCall call =
            java.parse("class Foo {{ new java.util.ArrayList('c'); }}")
                .descendants(ASTConstructorCall.class).firstOrThrow();

        assertMatch(call, "_#new(int)");
        assertMatch(call, "java.util.ArrayList#new(int)");
        assertMatch(call, "java.util.ArrayList#_(int)");
        assertMatch(call, "java.util.ArrayList#_(_*)");

        assertNoMatch(call, "java.util.ArrayList#new()");
        assertNoMatch(call, "java.util.ArrayList#_()");

        assertNoMatch(call, "java.util.List#new(_*)");
        assertNoMatch(call, "java.util.List#_(_*)");
        assertNoMatch(call, "java.lang.Object#new(int)");
    }

    @Test
    void testArray() {

        ASTMethodCall call =
            java.parse("class Foo {{ new int[0].toString(); }}")
                .descendants(ASTMethodCall.class).firstOrThrow();

        assertMatch(call, "int[]#toString()");
        assertMatch(call, "_#toString()");
        assertMatch(call, "int[]#_()");
        assertMatch(call, "int[]#_(_*)");
        assertMatch(call, "_#_(_*)");

        assertNoMatch(call, "_#new(int)");
        assertNoMatch(call, "_[][]#_(_*)");
        // maybe we should support this one later
        assertNoMatch(call, "_[]#toString()");
    }

    @Test
    void testWhitespaceErrorMessage() {

        parse("_#_(int,int)"); // does not fail
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> parse("_#_(int, int)"));
        assertThat(e.getMessage(), equalTo("Expected type at index 8:\n"
                                                 + "    \"_#_(int, int)\"\n"
                                                 + "             ^\n"));
    }

    private void assertMatch(InvocationNode call, String sig) {
        assertTrue(parse(sig).matchesCall(call), sig + " should match " + call);
    }

    private void assertNoMatch(InvocationNode call, String s) {
        assertFalse(parse(s).matchesCall(call), s + " should not match " + call);
    }

    @Test
    void testEquals() {
        InvocationMatcher matcher1 = parse("java.lang.String#toString()");
        InvocationMatcher matcher2 = parse("java.lang.String#toString()");
        InvocationMatcher matcher3 = parse("java.lang.Object#toString()");
        InvocationMatcher matcher4 = parse("java.lang.String#valueOf(int)");
        InvocationMatcher matcher5 = parse("_#toString()");

        assertTrue(matcher1.equals(matcher2));
        assertTrue(matcher2.equals(matcher1));
        assertTrue(matcher1.equals(matcher1));

        assertFalse(matcher1.equals(null));
        assertFalse(matcher1.equals("not a matcher"));  // NOPMD
        assertFalse(matcher1.equals(matcher3));
        assertFalse(matcher1.equals(matcher4));
        assertFalse(matcher1.equals(matcher5));

        InvocationMatcher anyArgsMatch1 = parse("java.lang.String#toString(_*)");
        InvocationMatcher anyArgsMatch2 = parse("java.lang.String#toString(_*)");
        InvocationMatcher emptyArgsMatch = parse("java.lang.String#toString()");

        assertTrue(anyArgsMatch1.equals(anyArgsMatch2));
        assertFalse(anyArgsMatch1.equals(emptyArgsMatch));

        InvocationMatcher wildcardMethod1 = parse("java.lang.String#_(int)");
        InvocationMatcher wildcardMethod2 = parse("java.lang.String#_(int)");
        InvocationMatcher namedMethod = parse("java.lang.String#valueOf(int)");

        assertTrue(wildcardMethod1.equals(wildcardMethod2));
        assertFalse(wildcardMethod1.equals(namedMethod));
    }

    @Test
    void testHashCode() {
        InvocationMatcher matcher1 = parse("java.lang.String#toString()");
        InvocationMatcher matcher2 = parse("java.lang.String#toString()");

        assertThat(matcher1.hashCode(), equalTo(matcher2.hashCode()));
    }
}
