/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApexCommentTest extends ApexParserTestBase {

    @Test
    void testContainsComment1() {
        ASTApexFile file = apex.parse("class Foo {void foo(){try {\n"
                                          + "} catch (Exception e) {\n"
                                          + "  /* OK: block comment inside of empty catch block; should not be reported */\n"
                                          + "}}}");

        ASTCatchBlockStatement catchBlock = file.descendants(ASTCatchBlockStatement.class).crossFindBoundaries().firstOrThrow();
        assertTrue(catchBlock.getContainsComment());
    }
}
