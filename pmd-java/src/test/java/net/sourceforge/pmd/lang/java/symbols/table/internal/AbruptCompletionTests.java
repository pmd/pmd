/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class AbruptCompletionTests extends BaseNonParserTest {

    private final JavaParsingHelper java15p = java.withDefaultVersion("15-preview");

    private Executable canCompleteNormally(String stmt, boolean expected) {
        return () -> {
            ASTCompilationUnit ast = java15p.parse("class Foo {{ " + stmt + "; }}");

            ASTStatement e = ast.descendants(ASTBlock.class).crossFindBoundaries().firstOrThrow();

            boolean actual = PatternBindingsUtil.canCompleteNormally(e);
            assertEquals(expected, actual, "Can '" + stmt + "' complete normally?");
        };
    }

    private Executable canCompleteNormally(String stmt) {
        return canCompleteNormally(stmt, true);
    }

    private Executable mustCompleteAbruptly(String stmt) {
        return canCompleteNormally(stmt, false);
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
            canCompleteNormally("while(foo) { break; }"),
            canCompleteNormally("l: while(foo) { break; }"),
            canCompleteNormally("l: while(foo) { break l; }"),
            canCompleteNormally("l: while(foo) { if (x) break l; }"),
            canCompleteNormally("while(foo) { if (x) continue; }"),

            mustCompleteAbruptly("while(true) { return; }"),
            mustCompleteAbruptly("while(true) { if (print()) return; }"),
            mustCompleteAbruptly("while(true) { }"),
            mustCompleteAbruptly("while(true) { if (print()) return; }")
        );
    }


}
