/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class CommentAssignmentTest extends BaseParserTest {

    /**
     * Blank lines in comments should not raise an exception. See bug #1048.
     */
    @Test
    void testFilteredCommentIn() {
        ASTCompilationUnit node = java.parse("""
                                                 public class Foo {
                                                      /* multi line comment with blank lines
                                                 
                                                 
                                                  */
                                                         /** a formal comment with blank lines
                                                 
                                                 
                                                  */\
                                                 }\
                                                 """);

        JavaComment comment = node.getComments().getFirst();

        assertFalse(comment.isSingleLine());
        assertFalse(comment.hasJavadocContent());
        assertEquals("multi line comment with blank lines", StringUtils.join(comment.getFilteredLines(), ' '));

        comment = node.getComments().get(1);
        assertFalse(comment.isSingleLine());
        assertTrue(comment.hasJavadocContent());
        assertThat(comment, instanceOf(JavadocComment.class));
        assertEquals("a formal comment with blank lines", StringUtils.join(comment.getFilteredLines(), ' '));
    }


    @Test
    void testCommentAssignments() {

        ASTCompilationUnit node = java.parse("""
                                                 public class Foo {
                                                      /** Comment 1 */
                                                         public void method1() {}
                                                    \s
                                                         /** Comment 2 */
                                                    \s
                                                         /** Comment 3 */
                                                         public void method2() {}\
                                                 }\
                                                 """);

        List<ASTMethodDeclaration> methods = node.descendants(ASTMethodDeclaration.class).toList();
        assertCommentEquals(methods.getFirst(), "/** Comment 1 */");
        assertCommentEquals(methods.get(1), "/** Comment 3 */");
    }

    @Test
    void testCommentAssignmentsWithAnnotation() {

        ASTCompilationUnit node = java.parse("""
                                                 public class Foo {
                                                      /** Comment 1 */
                                                         @Oha public void method1() {}
                                                    \s
                                                         /** Comment 2 */
                                                     @Oha
                                                         /** Comment 3 */
                                                         public void method2() {}\
                                                 }\
                                                 """);

        List<ASTMethodDeclaration> methods = node.descendants(ASTMethodDeclaration.class).toList();
        assertCommentEquals(methods.getFirst(), "/** Comment 1 */");
        assertCommentEquals(methods.get(1), "/** Comment 2 */");
    }

    @Test
    void testCommentAssignmentOnPackage() {

        ASTCompilationUnit node = java.parse("""
                                                 /** Comment 1 */
                                                 package bar;
                                                 """);

        assertCommentEquals(node.descendants(ASTPackageDeclaration.class).firstOrThrow(),
                            "/** Comment 1 */");
    }

    @Test
    void testCommentAssignmentOnClass() {

        ASTCompilationUnit node = java.parse("""
                                                 /** outer */
                                                 class Foo { \
                                                  /** inner */ class Nested { } \
                                                  { /** local */ class Local {}} \
                                                  /** enum */enum NestedEnum {}\
                                                 }\
                                                 """);

        List<ASTTypeDeclaration> types = node.descendants(ASTTypeDeclaration.class).crossFindBoundaries().toList();
        assertCommentEquals(types.getFirst(), "/** outer */");
        assertCommentEquals(types.get(1), "/** inner */");
        assertCommentEquals(types.get(2), "/** local */");
        assertCommentEquals(types.get(3), "/** enum */");
    }

    @Test
    void testCommentAssignmentOnEnum() {

        ASTCompilationUnit node = java.parse("enum Foo { "
                                                 + " /** A */ A,"
                                                 + " /** B */ @Oha B,"
                                                 + " C,"
                                                 + " /* not javadoc */ D,"
                                                 + "}");

        List<ASTEnumConstant> constants = node.descendants(ASTEnumConstant.class).toList();
        assertCommentEquals(constants.getFirst(), "/** A */");
        assertCommentEquals(constants.get(1), "/** B */");
        assertHasNoComment(constants.get(2));
        assertHasNoComment(constants.get(3));
    }


    private void assertCommentEquals(JavadocCommentOwner pack, String expected) {
        assertNotNull(pack.getJavadocComment(), "null comment on " + pack);
        assertEquals(expected, pack.getJavadocComment().getText().toString());
    }

    private void assertHasNoComment(JavadocCommentOwner pack) {
        assertNull(pack.getJavadocComment(), "Expected null comment on " + pack);
    }
}
