/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java23TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java23 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("23")
                    .withResourceContext(Java21TreeDumpTest.class, "jdkversiontests/java23/");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java23;
    }

    @Test
    void jep467MarkdownDocumentationComments() {
        doTest("Jep467_MarkdownDocumentationComments");
        ASTCompilationUnit unit = java23.parseResource("Jep467_MarkdownDocumentationComments.java");
        List<JavaComment> comments = unit.getComments();

        // in total 4 comment blocks (1 license header and 3 javadoc comments with ///)
        // the new ///-style comment lines should be collapsed into blocks
        assertEquals(4, comments.size());

        // first comment is the license header
        assertFalse(comments.get(0).hasJavadocContent());
        assertFalse(comments.get(0).isSingleLine());

        for (JavaComment c : comments.subList(1, comments.size())) {
            assertTrue(c.getText().startsWith("///"));
            assertTrue(c.hasJavadocContent());
            assertInstanceOf(JavadocComment.class, c);
        }

        JavadocComment classComment = unit.getTypeDeclarations().first().getJavadocComment();
        assertEquals(7, classComment.getReportLocation().getStartLine());
        assertEquals(9, classComment.getReportLocation().getEndLine());
        assertThat(classComment.getText().toString(), containsString("JEP 467: Markdown Documentation Comments</a>"));
        assertFalse(classComment.isSingleLine());

        List<JavadocComment> methodComments = unit.descendants(ASTMethodDeclaration.class).toStream()
                .map(ASTMethodDeclaration::getJavadocComment)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        assertEquals(2, methodComments.size());
        assertThat(methodComments.get(0).getText().toString(), containsString("@param prefix the prefix"));
        assertFalse(methodComments.get(0).isSingleLine());
        assertThat(methodComments.get(1).getText().toString(), containsString("{@inheritDoc}"));
        assertTrue(methodComments.get(1).isSingleLine());
    }
}
