/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;


import java.util.function.Consumer;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.util.OptionalBool;

import junit.framework.AssertionFailedError;

public class AbruptCompletionTests extends BaseNonParserTest {

    private final JavaParsingHelper java17 = java.withDefaultVersion("17");

    private Executable canCompleteNormally(String stmt, Consumer<OptionalBool> expected) {
        return () -> {
            ASTCompilationUnit ast = java17.parse("class Foo {{ " + stmt + "; }}");

            ASTStatement e = ast.descendants(ASTBlock.class).crossFindBoundaries().firstOrThrow();


            OptionalBool actual = AbruptCompletionAnalysis.completesNormally(e);
            expected.accept(actual);
        };
    }

    private Executable canCompleteNormally(String stmt) {
        return canCompleteNormally(stmt, actual -> {
            if (actual == OptionalBool.NO) {
                throw new AssertionFailedError("Code can complete normally: `" + stmt + "`");
            }
        });
    }

    private Executable mustCompleteNormally(String stmt) {
        return canCompleteNormally(stmt, actual -> {
            if (actual != OptionalBool.YES) {
                throw new AssertionFailedError("Code MUST complete normally, got " + actual + ": `" + stmt + "`");
            }
        });
    }

    private Executable mustCompleteAbruptly(String stmt) {
        return canCompleteNormally(stmt, actual -> {
            if (actual != OptionalBool.NO) {
                throw new AssertionFailedError("Code MUST complete abruptly, got " + actual + ": `" + stmt + "`");
            }
        });
    }

    @Test
    public void testIfStatements() {
        Assertions.assertAll(
            canCompleteNormally(
                "if (foo) {}"
                    + "else { throw new Exception(); }"
            ),
            canCompleteNormally(
                "if (foo) { throw new Exception(); }"
            ),
            mustCompleteAbruptly(
                "if (foo) { throw new Exception(); }"
                    + "else { throw new Exception(); }"
            )
        );
    }


    @Test
    public void testWhileStmt() {
        Assertions.assertAll(
            canCompleteNormally("while(foo) { return; }"),
            mustCompleteNormally("while(foo) { break; }"),
            mustCompleteNormally("l: while(foo) { break; }"),
            mustCompleteNormally("l: while(foo) { break l; }"),
            mustCompleteNormally("l: while(foo) { if (x) break l; }"),

            canCompleteNormally("while(true) { if (foo) break; }"),

            mustCompleteAbruptly("while(true) { return; }"),
            mustCompleteAbruptly("while(true) { }"),
            mustCompleteNormally("while(true) { break; }"),
            mustCompleteAbruptly("while(true) { while(foo) break; }"),
            mustCompleteNormally("x: while(true) { while(foo) break x; }"),
            mustCompleteAbruptly("while(true) { if (print()) return; }")
        );
    }

    @Test
    public void testWhileContinue() {
        Assertions.assertAll(
            mustCompleteNormally("while(foo) { if (x) continue; }"),

            // this is trivial, but the condition may have side-effects
            canCompleteNormally("while(foo) { continue; }"),
            canCompleteNormally("if (foo) while(foo) { continue; } "
                                    + "else throw e;"),

            mustCompleteAbruptly("while(true) { continue; }")
        );
    }

    @Test
    public void testSwitchFallthrough() {
        Assertions.assertAll(
            mustCompleteNormally("switch(foo) {}"),
            mustCompleteNormally("switch(foo) { case 1: break; }"),
            mustCompleteNormally("switch(foo) { case 1: break; case 2: foo(); }"),
            canCompleteNormally("switch(foo) { case 1: return; case 2: foo(); }")
        );
    }

    @Test
    public void testSwitchArrow() {
        Assertions.assertAll(
            mustCompleteNormally("switch(foo) {}"),
            mustCompleteNormally("switch(foo) { case 1 -> X; default->  X;}"),

            canCompleteNormally("switch(foo) { case 1 -> throw X; }"),
            canCompleteNormally("switch(foo) { case 1 -> throw X; }"),

            mustCompleteAbruptly("switch(foo) { case 1 -> throw X; default-> throw X;}")
        );
    }

    @Test
    public void testSwitchExhaustive() {
        Assertions.assertAll(
            // no default, even with exhaustive enum, means it can complete normally
            canCompleteNormally("enum Local { A } Local a = Local.A;\n"
                                    + "switch(a) { case A: return; }"),
            mustCompleteAbruptly("enum Local { A } Local a = Local.A;\n"
                                    + "switch(a) { case A: return; default: return; }")
        );
    }

    @Test
    public void testFalseLoopIsUnreachable() {
        Assertions.assertAll(
            mustCompleteNormally("while(false) { }"),
            mustCompleteNormally("for(;false;) { }")
        );
    }

    @Test
    public void testLabeledStmt() {
        Assertions.assertAll(
            canCompleteNormally("l: if (foo) break l; else break l;"),
            mustCompleteAbruptly("if (foo) break l; else break l;")
        );
    }


}
