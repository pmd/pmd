/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class EscapeUtilsTest {

    @Test
    public void testEscapeMarkdown() {
        assertEquals("This is a \\\\backslash", EscapeUtils.escapeMarkdown("This is a \\backslash"));
        assertEquals("This \"\\*\" is not a emphasis", EscapeUtils.escapeMarkdown("This \"*\" is not a emphasis"));
        assertEquals("This \"\\*\\*\" is not a strong style", EscapeUtils.escapeMarkdown("This \"**\" is not a strong style"));
        assertEquals("This \"\\[foo\\]\" does not start a link", EscapeUtils.escapeMarkdown("This \"[foo]\" does not start a link"));
        assertEquals("This \"\\~bar\\~\" is not a strike-through", EscapeUtils.escapeMarkdown("This \"~bar~\" is not a strike-through"));
        assertEquals("That's \"\\|\" just a bar", EscapeUtils.escapeMarkdown("That's \"|\" just a bar"));
        assertEquals("This \"\\_\" is just a underscore", EscapeUtils.escapeMarkdown("This \"_\" is just a underscore"));
    }

    @Test
    public void testEscapeHtmlWithinMarkdownSingleLine() {
        assertEquals("a &lt;script&gt; tag outside of `<script>` backticks should be escaped",
                EscapeUtils.escapeSingleLine("a <script> tag outside of `<script>` backticks should be escaped"));
        assertEquals("a &lt;script&gt; &quot;tag&quot; outside of `<script>` backticks should be escaped &lt;multiple&gt; times `<strong>`.",
                EscapeUtils.escapeSingleLine("a <script> \"tag\" outside of `<script>` backticks should be escaped <multiple> times `<strong>`."));
        assertEquals("URLS: a https://pmd.github.io or a <https://pmd.github.io> are turned into links",
                EscapeUtils.escapeSingleLine("URLS: a https://pmd.github.io or a <https://pmd.github.io> are turned into links"));
        assertEquals("multiple URLS: <https://pmd.github.io> and <https://pmd.github.io> are two links",
                EscapeUtils.escapeSingleLine("multiple URLS: <https://pmd.github.io> and <https://pmd.github.io> are two links"));
        assertEquals("URL: <http://www.google.com> is a url without ssl",
                EscapeUtils.escapeSingleLine("URL: <http://www.google.com> is a url without ssl"));
        assertEquals("> this is a quote line",
                EscapeUtils.escapeSingleLine("> this is a quote line"));
        assertEquals("combination of URLs and backticks: <https://pmd.github.io> but `<script>` &lt;strong&gt;escaped&lt;/strong&gt;",
                EscapeUtils.escapeSingleLine("combination of URLs and backticks: <https://pmd.github.io> but `<script>` <strong>escaped</strong>"));
        assertEquals("combination of URLs and backticks: `<script>` &lt;strong&gt;escaped&lt;/strong&gt; but <https://pmd.github.io>",
                EscapeUtils.escapeSingleLine("combination of URLs and backticks: `<script>` <strong>escaped</strong> but <https://pmd.github.io>"));
    }

    @Test
    public void testEscapeHtmlWithinMarkdownBlocks() {
        String text = "paragraph\n\n> quote <script>\n> quote line \"2\"\n>quote line `<script>` 3\n\n"
                + "next paragraph\n\n    code <script> \"a < b\"\n    code line 2\n\n"
                + "next paragraph\n\n```\ncode <script> \"a < b\"\ncode line 2\n```\n\n"
                + "next paragraph\n\n```java\nString = \"code <script> with syntax highlighting\";\ncode line 2\n```\n";
        String expected = "paragraph\n\n> quote &lt;script&gt;\n> quote line &quot;2&quot;\n>quote line `<script>` 3\n\n"
                + "next paragraph\n\n    code <script> \"a < b\"\n    code line 2\n\n"
                + "next paragraph\n\n```\ncode <script> \"a < b\"\ncode line 2\n```\n\n"
                + "next paragraph\n\n```java\nString = \"code <script> with syntax highlighting\";\ncode line 2\n```\n";
        List<String> escaped = EscapeUtils.escapeLines(Arrays.asList(text.split("\n")));
        assertEquals(Arrays.asList(expected.split("\n")), escaped);
    }
}
