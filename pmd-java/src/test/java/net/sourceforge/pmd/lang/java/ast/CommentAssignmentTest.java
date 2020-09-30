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
        assertCommentEquals(methods.get(0), "/** Comment 1 */");
        assertCommentEquals(methods.get(1), "/** Comment 3 */");
    }

    @Test
    public void testCommentAssignmentsWithAnnotation() {

        ASTCompilationUnit node = java.parse("public class Foo {\n"
                                                 + "     /** Comment 1 */\n"
                                                 + "        @Oha public void method1() {}\n"
                                                 + "    \n"
                                                 + "        /** Comment 2 */\n"
                                                 + "    @Oha\n"
                                                 // note that since this is the first token, the prev comment gets selected
                                                 + "        /** Comment 3 */\n"
                                                 + "        public void method2() {}" + "}");

        List<ASTMethodDeclaration> methods = node.findDescendantsOfType(ASTMethodDeclaration.class);
        assertCommentEquals(methods.get(0), "/** Comment 1 */");
        assertCommentEquals(methods.get(1), "/** Comment 2 */");
    }

    @Test
    public void testCommentAssignmentOnPackage() {

        ASTCompilationUnit node = java.parse("/** Comment 1 */\n"
                                                 + "package bar;\n");

        assertCommentEquals(node.descendants(ASTPackageDeclaration.class).firstOrThrow(),
                            "/** Comment 1 */");
    }

    @Test
    public void testCommentAssignmentOnClass() {

        ASTCompilationUnit node = java.parse("/** outer */\n"
                                                 + "class Foo { "
                                                 + " /** inner */ class Nested { } "
                                                 + " { /** local */ class Local {}} "
                                                 + " /** enum */enum NestedEnum {}"
                                                 + "}");

        List<ASTAnyTypeDeclaration> types = node.descendants(ASTAnyTypeDeclaration.class).toList();
        assertCommentEquals(types.get(0), "/** outer */");
        assertCommentEquals(types.get(1), "/** inner */");
        assertCommentEquals(types.get(2), "/** local */");
        assertCommentEquals(types.get(3), "/** enum */");
    }


    private void assertCommentEquals(JavadocCommentOwner pack, String expected) {
        Assert.assertNotNull("null comment on " + pack, pack.getJavadocComment());
        Assert.assertEquals(expected, pack.getJavadocComment().getText().toString());
    }
}
