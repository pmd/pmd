/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

        assertFalse(comment.isSingleLine());
        assertFalse(comment.hasJavadocContent());
        assertEquals("multi line comment with blank lines", StringUtils.join(comment.filteredLines(), ' '));

        comment = node.getComments().get(1);
        assertFalse(comment.isSingleLine());
        assertTrue(comment.hasJavadocContent());
        assertThat(comment, instanceOf(FormalComment.class));
        assertEquals("a formal comment with blank lines", StringUtils.join(comment.filteredLines(), ' '));
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

        List<ASTMethodDeclaration> methods = node.descendants(ASTMethodDeclaration.class).toList();
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

        List<ASTMethodDeclaration> methods = node.descendants(ASTMethodDeclaration.class).toList();
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

    @Test
    public void testCommentAssignmentOnEnum() {

        ASTCompilationUnit node = java.parse("enum Foo { "
                                                 + " /** A */ A,"
                                                 + " /** B */ @Oha B,"
                                                 + " C,"
                                                 + " /* not javadoc */ D,"
                                                 + "}");

        List<ASTEnumConstant> constants = node.descendants(ASTEnumConstant.class).toList();
        assertCommentEquals(constants.get(0), "/** A */");
        assertCommentEquals(constants.get(1), "/** B */");
        assertHasNoComment(constants.get(2));
        assertHasNoComment(constants.get(3));
    }


    private void assertCommentEquals(JavadocCommentOwner pack, String expected) {
        Assert.assertNotNull("null comment on " + pack, pack.getJavadocComment());
        Assert.assertEquals(expected, pack.getJavadocComment().getText().toString());
    }

    private void assertHasNoComment(JavadocCommentOwner pack) {
        Assert.assertNull("Expected null comment on " + pack, pack.getJavadocComment());
    }
}
