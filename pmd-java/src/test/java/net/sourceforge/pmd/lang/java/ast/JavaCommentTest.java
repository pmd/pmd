/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.BaseParserTest;

/**
 * @author Clément Fournier
 */
class JavaCommentTest extends BaseParserTest {

    @Test
    void testFilteredLines() {
        JavaComment comment = parseComment(
            "/**\n"
                + " * @author Clément Fournier\n"
                + " *"
                + " */\n"
        );

        assertThat(comment.getFilteredLines(),
                   contains(Chars.wrap("@author Clément Fournier")));
    }

    @Test
    void testFilteredLinesKeepBlankLines() {
        JavaComment comment = parseComment(
            "/**\n"
                + " * @author Clément Fournier\n"
                + " *"
                + " */\n"
        );

        assertThat(comment.getFilteredLines(true),
                   contains(Chars.wrap(""), Chars.wrap("@author Clément Fournier"), Chars.wrap("")));
    }

    JavaComment parseComment(String text) {
        ASTCompilationUnit parsed = java.parse(text);
        return JavaComment.getLeadingComments(parsed).findFirst().get();
    }


    @Test
    void getLeadingComments() {
        ASTCompilationUnit parsed = java.parse("/** a */ class Fooo { /** b */ int field; }");
        List<JavadocCommentOwner> docCommentOwners = parsed.descendants(JavadocCommentOwner.class).toList();

        checkCommentMatches(docCommentOwners.get(0), "/** a */");
        checkCommentMatches(docCommentOwners.get(1), "/** b */");
    }

    private static void checkCommentMatches(JavadocCommentOwner commentOwner, String expectedText) {
        // this is preassigned by the comment assignment pass
        JavadocComment comment = commentOwner.getJavadocComment();
        assertEquals(expectedText, comment.getText().toString());

        // this is fetched adhoc
        List<JavaComment> collected = JavaComment.getLeadingComments(commentOwner).collect(Collectors.toList());
        assertEquals(listOf(comment), collected);
    }
}
