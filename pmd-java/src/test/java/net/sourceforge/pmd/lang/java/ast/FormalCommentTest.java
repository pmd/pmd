/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.BaseParserTest;

class FormalCommentTest extends BaseParserTest {

    @Test
    void testJavadocTagsAsChildren() {
        ASTCompilationUnit acu = java.parse(
            "interface Metric {"
                + "   /**\n"
                + "     * Checks if the metric can be computed on the node.\n"
                + "     *\n"
                + "     * @param node The node to check\n"
                + "     *\n"
                + "     * @return True if the metric can be computed\n"
                + "     */\n"
                + "    boolean supports(N node);\n"
                + "}");

        ASTType booleanT = acu.descendants(ASTType.class).firstOrThrow();
        JavaccToken firstToken = booleanT.getFirstToken();
        assertEquals(JavaTokenKinds.BOOLEAN, firstToken.kind, "Boolean");
        JavaccToken comment = firstToken.getPreviousComment();
        assertEquals(JavaccToken.IMPLICIT_TOKEN, comment.kind, "Implicit modifier list");
        comment = comment.getPreviousComment();
        assertEquals(JavaTokenKinds.WHITESPACE, comment.kind, "Whitespace");
        assertEquals("\n    ", comment.getImage());
        comment = comment.getPreviousComment();
        assertEquals(JavaTokenKinds.FORMAL_COMMENT, comment.kind, "Formal comment");

        List<JavadocElement> javadocs = new FormalComment(comment).getChildren();

        assertEquals(2, javadocs.size());

        JavadocElement paramTag = javadocs.get(0);
        assertEquals("param", paramTag.tag().label);

        JavadocElement returnTag = javadocs.get(1);
        assertEquals("return", returnTag.tag().label);
    }
}
