/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class CommentAssignmentTest extends BaseNonParserTest {

    /**
     * Blank lines in comments should not raise an exception. See bug #1048.
     */
    @Test
    public void testFilteredCommentIn() {
        ASTCompilationUnit node = java.parse("public class Foo {\n"
                                                 + "     /* multi line comment with blank lines\n\n\n */\n"
                                                 + "        /** a formal comment with blank lines\n\n\n */"
                                                 + "}");

        Comment comment = node.getComments().get(0);

        assertThat(comment, instanceOf(MultiLineComment.class));
        assertEquals("multi line comment with blank lines", comment.getFilteredComment());

        comment = node.getComments().get(1);
        assertThat(comment, instanceOf(FormalComment.class));
        assertEquals("a formal comment with blank lines", comment.getFilteredComment());
    }


    @Test
    public void testCommentAssignments() {

        ASTCompilationUnit node = java.parse("public class Foo {\n"
                                                 + "     /** Comment 1 */\n"
                                                 + "        public void method1() {}\n"
                                                 + "    \n"
                                                 + "        /** Comment 2 */\n"
                                                 + "    \n"
                                                 + "        /** Comment 3 */\n"
                                                 + "        public void method2() {}" + "}");

        List<ASTMethodDeclaration> methods = node.findDescendantsOfType(ASTMethodDeclaration.class);
        Assert.assertEquals("/** Comment 1 */", methods.get(0).getJavadocComment().getImage());
        Assert.assertEquals("/** Comment 3 */", methods.get(1).getJavadocComment().getImage());
    }
}
