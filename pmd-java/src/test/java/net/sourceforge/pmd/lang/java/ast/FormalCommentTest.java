/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

public class FormalCommentTest extends BaseParserTest {

    @Test
    public void testJavadocTagsAsChildren() {
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
        assertEquals("Boolean", JavaTokenKinds.BOOLEAN, firstToken.kind);
        JavaccToken comment = firstToken.getPreviousComment();
        assertEquals("Implicit modifier list", JavaccToken.IMPLICIT_TOKEN, comment.kind);
        comment = comment.getPreviousComment();
        assertEquals("Whitespace", JavaTokenKinds.WHITESPACE, comment.kind);
        assertEquals("\n    ", comment.getImage());
        comment = comment.getPreviousComment();
        assertEquals("Formal comment", JavaTokenKinds.FORMAL_COMMENT, comment.kind);

        List<JavadocElement> javadocs = new FormalComment(comment).getChildren();

        Assert.assertEquals(2, javadocs.size());

        JavadocElement paramTag = javadocs.get(0);
        Assert.assertEquals("param", paramTag.tag().label);

        JavadocElement returnTag = javadocs.get(1);
        Assert.assertEquals("return", returnTag.tag().label);
    }
}
