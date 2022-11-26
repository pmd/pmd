/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;


import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.table.internal.PatternBindingsUtil.BindSet;
import net.sourceforge.pmd.util.CollectionUtil;

class PatternBindingsTests extends BaseParserTest {

    private final JavaParsingHelper java17 = java.withDefaultVersion("17");

    private Executable declares(String expr, Set<String> trueVars, Set<String> falseVars) {
        return () -> {
            ASTCompilationUnit ast = java17.parse("class Foo {{ Object o = (" + expr + "); }}");

            ASTExpression e = ast.descendants(ASTExpression.class).crossFindBoundaries().firstOrThrow();

            BindSet bindSet = PatternBindingsUtil.bindersOfExpr(e);
            checkBindings(expr, trueVars, bindSet.getTrueBindings(), true);
            checkBindings(expr, falseVars, bindSet.getFalseBindings(), false);
        };
    }

    private void checkBindings(String expr, Set<String> expected, Set<ASTVariableDeclaratorId> bindings, boolean isTrue) {
        Set<String> actual = CollectionUtil.map(toSet(), bindings, ASTVariableDeclaratorId::getName);
        assertEquals(expected, actual, "Bindings of '" + expr + "' when " + isTrue);
    }

    private Executable declaresNothing(String expr) {
        return declares(expr, emptySet(), emptySet());
    }

    @Test
    void testUnaries() {
        String stringS = "a instanceof String s";
        assertAll(
            declares(stringS, setOf("s"), emptySet()),
            declares("!(" + stringS + ")", emptySet(), setOf("s")),

            declaresNothing("foo(" + stringS + ")"),
            declaresNothing("foo(" + stringS + ") || true")
        );
    }

    @Test
    void testBooleanConditionals() {
        String stringS = "(a instanceof String s)";
        String stringP = "(a instanceof String p)";
        assertAll(
            declares(stringS + " || " + stringP, emptySet(), emptySet()),
            declares(stringS + " && " + stringP, setOf("s", "p"), emptySet()),
            declares("!(" + stringS + " || " + stringP + ")", emptySet(), emptySet()),
            declares("!(" + stringS + " && " + stringP + ")", emptySet(), setOf("s", "p")),

            declares("!" + stringS + " || " + stringP, emptySet(), setOf("s")),
            declares("!" + stringS + " || !" + stringP, emptySet(), setOf("s", "p")),
            declares("!" + stringS + " && !" + stringP, emptySet(), emptySet()),
            declares(stringS + " && !" + stringP, setOf("s"), emptySet())
        );
    }


}
