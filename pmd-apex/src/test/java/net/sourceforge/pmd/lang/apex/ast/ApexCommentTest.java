/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void fieldDeclarationHasFormalComment() {
        final String commentContent = "/** formal comment */";
        ASTApexFile file = apex.parse("class MyClass {\n"
                + "  " + commentContent + "\n"
                + "  Integer field;\n"
                + "}\n");
        ASTFormalComment comment = file.descendants(ASTFieldDeclaration.class).crossFindBoundaries()
                .children(ASTFormalComment.class).first();
        assertEquals(commentContent, comment.getImage());
    }
}
