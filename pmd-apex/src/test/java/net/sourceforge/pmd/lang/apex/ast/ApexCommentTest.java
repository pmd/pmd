/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApexCommentTest extends ApexParserTestBase {
    private static final String FORMAL_COMMENT_CONTENT = "/** formal comment */";

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
        ASTApexFile file = apex.parse("class MyClass {\n"
                + "  " + FORMAL_COMMENT_CONTENT + "\n"
                + "  Integer field;\n"
                + "}\n");
        ASTFormalComment comment = file.descendants(ASTUserClass.class)
                .children(ASTFieldDeclarationStatements.class)
                .children(ASTFieldDeclaration.class)
                .children(ASTFormalComment.class).first();
        assertEquals(FORMAL_COMMENT_CONTENT, comment.getImage());
    }

    @Test
    void methodHasFormalComment() {
        ASTApexFile file = apex.parse(FORMAL_COMMENT_CONTENT + "\n"
            + "class MyClass {\n"
            + "  " + FORMAL_COMMENT_CONTENT + "\n"
            + "  public void bar() {}\n"
            + "}");
        ASTFormalComment comment = file.descendants(ASTUserClass.class).children(ASTMethod.class).children(ASTFormalComment.class).first();
        assertEquals(FORMAL_COMMENT_CONTENT, comment.getImage());
    }

    @Test
    void methodHasFormalCommentAnnotatedClass() {
        ASTApexFile file = apex.parse(FORMAL_COMMENT_CONTENT + "\n"
                + "@RestResource(urlMapping='/api/v1/get/*')\n"
                + "class MyClass {\n"
                + "  " + FORMAL_COMMENT_CONTENT + "\n"
                + "  public void bar() {}\n"
                + "}");
        ASTFormalComment comment = file.descendants(ASTUserClass.class).children(ASTMethod.class).children(ASTFormalComment.class).first();
        assertEquals(FORMAL_COMMENT_CONTENT, comment.getImage());
    }

    @Test
    void classHasFormalComment() {
        ASTApexFile file = apex.parse(FORMAL_COMMENT_CONTENT + "\n"
                + "class MyClass {}");
        ASTFormalComment comment = file.descendants(ASTUserClass.class).children(ASTFormalComment.class).first();
        assertEquals(FORMAL_COMMENT_CONTENT, comment.getImage());
    }
}
