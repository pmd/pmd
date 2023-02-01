/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;

class AbruptCompletionTests extends BaseParserTest {

    private final JavaParsingHelper java17 = java.withDefaultVersion("17");

    private Executable canCompleteNormally(String stmt, Consumer<Boolean> expected) {
        return canCompleteNormally(stmt, (actual, ignored) -> expected.accept(actual), it -> it);
    }

    private Executable canCompleteNormally(String stmtText, BiConsumer<Boolean, ASTStatement> expected, Function<ASTBlock, ASTStatement> getNode) {
        return () -> {
            ASTCompilationUnit ast = java17.parse("class Foo {{ " + stmtText + "; }}");

            ASTBlock block = ast.descendants(ASTBlock.class).crossFindBoundaries().firstOrThrow();
            ASTStatement stmt = getNode.apply(block);

            boolean actual = AbruptCompletionAnalysis.canCompleteNormally(stmt);
            expected.accept(actual, stmt);
        };
    }

    private Executable canCompleteNormally(String stmtText, boolean expected, Function<ASTBlock, ASTStatement> getNode) {
        return canCompleteNormally(
            stmtText,
            (actual, stmt) -> assertEquals(expected, actual, "Can " + stmt.getText() + " complete normally?"),
            getNode
        );
    }

    private Executable canCompleteNormally(String stmt) {
        return canCompleteNormally(stmt, actual -> {
            if (!actual) {
                fail("Code CAN complete normally: `" + stmt + "`");
            }
        });
    }

    private Executable mustCompleteAbruptly(String stmt) {
        return canCompleteNormally(stmt, actual -> {
            if (actual) {
                fail("Code MUST complete abruptly: `" + stmt + "`");
            }
        });
    }

    @Test
    void testIfStatements() {
        assertAll(
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
    void testWhileStmt() {
        assertAll(
            canCompleteNormally("while(foo) { return; }"),
            canCompleteNormally("while(foo) { break; }"),
            canCompleteNormally("l: while(foo) { break; }"),
            canCompleteNormally("l: while(foo) { break l; }"),
            canCompleteNormally("l: while(foo) { if (x) break l; }"),

            canCompleteNormally("while(true) { if (foo) break; }"),
            canCompleteNormally("while(false) { return; }"),

            mustCompleteAbruptly("while(true) { return; }"),
            mustCompleteAbruptly("while(true) { }"),
            canCompleteNormally("while(true) { break; }"),
            mustCompleteAbruptly("while(true) { while(foo) break; }"),
            canCompleteNormally("x: while(true) { while(foo) break x; }"),
            mustCompleteAbruptly("while(true) { if (print()) return; }")
        );
    }

    @Test
    void testForLoop() {
        assertAll(
            canCompleteNormally("for(; foo; ) { return; }"),
            canCompleteNormally("for(;foo;) { break; }"),
            canCompleteNormally("l: for(;foo;) { break; }"),
            canCompleteNormally("l: for(;foo;) { break l; }"),
            canCompleteNormally("l: for(;foo;) { if (x) break l; }"),

            canCompleteNormally("for(;true;) { if (foo) break; }"),
            canCompleteNormally("for(;false;) { return; }"),

            mustCompleteAbruptly("for(;true;) { return; }"),
            mustCompleteAbruptly("for(;true;) { }"),
            canCompleteNormally("for(;true;) { break; }"),
            mustCompleteAbruptly("for(;true;) { while(foo) break; }"),
            canCompleteNormally("x: for(;true;) { while(foo) break x; }"),
            mustCompleteAbruptly("for(;true;) { if (print()) return; }")
        );
    }

    @Test
    void testDoLoop() {
        assertAll(
            canCompleteNormally("do { } while(foo);"),
            canCompleteNormally("do { continue; } while(foo);"),
            canCompleteNormally("do { break; } while(foo);"),

            canCompleteNormally("do { break; } while(true);"),

            mustCompleteAbruptly("do { return; } while(true);"),
            mustCompleteAbruptly("do { if(foo) return; } while(true);"),
            mustCompleteAbruptly("do { continue; } while(true);"),

            mustCompleteAbruptly("do { return; } while(foo);")
        );
    }

    @Test
    void testForeachLoop() {
        assertAll(
            canCompleteNormally("for (int i : new int[]{}) { }"),
            canCompleteNormally("for (int i : new int[]{}) { continue; }"),
            canCompleteNormally("for (int i : new int[]{}) { break; }"),
            canCompleteNormally("for (int i : new int[]{}) { return; }")
        );
    }

    @Test
    void testWhileContinue() {
        assertAll(
            canCompleteNormally("while(foo) { if (x) continue; }"),

            // this is trivial, but the condition may have side-effects
            canCompleteNormally("while(foo) { continue; }"),
            canCompleteNormally("if (foo) while(foo) { continue; } "
                                    + "else throw e;"),

            mustCompleteAbruptly("while(true) { continue; }")
        );
    }

    @Test
    void testSwitchFallthrough() {
        assertAll(
            canCompleteNormally("switch(foo) {}"),
            canCompleteNormally("switch(foo) { case 1: break; }"),
            canCompleteNormally("switch(foo) { case 1: break; case 2: foo(); }"),
            canCompleteNormally("switch(foo) { case 1: return; case 2: foo(); }")
        );
    }

    @Test
    void testSwitchArrow() {
        assertAll(
            canCompleteNormally("switch(foo) {}"),
            canCompleteNormally("switch(foo) { case 1 -> X; default->  X;}"),

            canCompleteNormally("switch(foo) { case 1 -> throw X; }"),
            canCompleteNormally("switch(foo) { case 1 -> throw X; }"),

            mustCompleteAbruptly("switch(foo) { case 1 -> throw X; default-> throw X;}")
        );
    }

    @Test
    void testTry() {
        assertAll(
            canCompleteNormally("try {}"),
            canCompleteNormally("try { foo(); }"),
            canCompleteNormally("try {{}}"),
            canCompleteNormally("try { } catch (Exception e) { }"),
            canCompleteNormally("try { throw x; } catch (Exception e) { }"),
            canCompleteNormally("try { return; } catch (Exception e) { }"),
            canCompleteNormally("try { } catch (Exception e) { return; }"),
            mustCompleteAbruptly("try { return; } catch (Exception e) { return; }"),
            mustCompleteAbruptly("try { } catch (Exception e) { } finally { return; }"),
            mustCompleteAbruptly("try { } catch (Exception e) { throw x; } finally { throw x; }"),

            mustCompleteAbruptly("while(true) { "
                                     + "try { } finally { throw x; } "
                                     + "}"),

            mustCompleteAbruptly("try {} finally { throw x; }"),
            mustCompleteAbruptly("try { throw x; } finally { }"),
            mustCompleteAbruptly("try { throw x; } finally { throw x; }")
        );
    }

    @Test
    void testSwitchExhaustive() {
        assertAll(
            // no default, even with exhaustive enum, means it can complete normally
            canCompleteNormally("enum Local { A } Local a = Local.A;\n"
                                    + "switch(a) { case A: return; }"),
            mustCompleteAbruptly("enum Local { A } Local a = Local.A;\n"
                                     + "switch(a) { case A: return; default: return; }")
        );
    }

    @Test
    void testYield() {
        assertAll(
            canCompleteNormally("int i = switch(1) {"
                                    + "  case 1 -> { yield 1; }"
                                    + "  default -> { yield 2; }"
                                    + "}"),
            canCompleteNormally("int i = switch(1) {"
                                    + "  case 1 -> { return; }"
                                    + "  default -> { yield 2; }"
                                    + "}"),

            canCompleteNormally(
                "int i = switch(1) {"
                    + "  case 1 -> { return; }"
                    + "  default -> { if (x) yield 2; else yield 3; }"
                    + "}",
                false, // the if stmt must return abruptly because of yield
                n -> n.descendants(ASTIfStatement.class).firstOrThrow()
            )

        );
    }

    @Test
    void testFalseLoopIsUnreachable() {
        assertAll(
            canCompleteNormally("while(false) { }"),
            canCompleteNormally("for(;false;) { }")
        );
    }

    @Test
    void testLabeledStmt() {
        assertAll(
            canCompleteNormally("l: if (foo) break l; else break l;"),
            mustCompleteAbruptly("if (foo) break l; else break l;")
        );
    }


}
