/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MarkdownCodeBlockTest {

    @ParameterizedTest(name = "{0} is escaped by {1}")
    @CsvSource({
        "`, ```",
        "``, ```",
        "```, ````",
        "````, `````",
        "`````, ``````",
        "``````, ```````"
    })
    void testCodeFences(String input, String expectedCodeFence) {
        final MarkdownRenderer.MarkdownCodeBlock markdownCodeBlock = new MarkdownRenderer.MarkdownCodeBlock(input);
        assertThat(markdownCodeBlock.toString().trim(), startsWith(expectedCodeFence));
    }
}
