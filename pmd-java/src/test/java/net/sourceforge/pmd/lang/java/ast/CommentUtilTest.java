/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class CommentUtilTest {

    @Test
    public void testFindJavaDocTags() {
        String formalComment =
                  "/**\n"
                + " * @see something\n"
                + " * @author Author1\n"
                + " * @author Author2\n"
                + " * @param parm1 description\n"
                + " */\n";

        Map<String, Integer> javadocTagsIn = CommentUtil.javadocTagsIn(formalComment);
        Assert.assertEquals(3, javadocTagsIn.size());
        Assert.assertEquals(7, javadocTagsIn.get("see").intValue());
        Assert.assertEquals("@see", formalComment.substring(7, 7 + 4));
        Assert.assertEquals("@author", formalComment.substring(javadocTagsIn.get("author"),
                javadocTagsIn.get("author") + "author".length() + 1));
    }

    @Test
    public void testFindJavaDocTagsEmpty() {
        Map<String, Integer> javadocTagsIn = CommentUtil.javadocTagsIn("");
        Assert.assertEquals(0, javadocTagsIn.size());
    }

    @Test
    public void testFindJavaDocTagsNull() {
        Map<String, Integer> javadocTagsIn = CommentUtil.javadocTagsIn(null);
        Assert.assertEquals(0, javadocTagsIn.size());
    }

    @Test
    public void testMultiLinesInSingleLine() {
        String comment = "/* single line. */";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("single line.", lines.get(0));
    }

    @Test
    public void testMultiLinesInSingleLineSimple() {
        String comment = "// single line.";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("single line.", lines.get(0));
    }

    @Test
    public void testMultiLinesInSingleLineFormal() {
        String comment = "/** single line. */";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("single line.", lines.get(0));
    }

    @Test
    public void testMultiLinesInMultiLine() {
        String comment =
                  "/*\n"
                + " * line 1\n"
                + " * line 2\n"
                + " */\n";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("line 1", lines.get(0));
        Assert.assertEquals("line 2", lines.get(1));
    }

    @Test
    public void testMultiLinesInMultiLineCrLf() {
        String comment =
                  "/*\r\n"
                + " * line 1\r\n"
                + " * line 2\r\n"
                + " */\r\n";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("line 1", lines.get(0));
        Assert.assertEquals("line 2", lines.get(1));
    }

    @Test
    public void testMultiLinesInMultiLineFormal() {
        String comment =
                  "/**\n"
                + " * line 1\n"
                + " * line 2\n"
                + " */\n";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("line 1", lines.get(0));
        Assert.assertEquals("line 2", lines.get(1));
    }

    @Test
    public void testMultiLinesInMultiLineFormalCrLf() {
        String comment =
                  "/**\r\n"
                + " * line 1\r\n"
                + " * line 2\r\n"
                + " */\r\n";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("line 1", lines.get(0));
        Assert.assertEquals("line 2", lines.get(1));
    }

    @Test
    public void testMultiLinesInMultiLineNoAsteriskEmpty() {
        String comment =
                  "/**\n"
                + " * line 1\n"
                + "line 2\n"
                + "\n"
                + " */\n";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("line 1", lines.get(0));
        Assert.assertEquals("line 2", lines.get(1));
    }

    @Test
    public void testTrim() {
        List<String> lines = Arrays.asList("", "a", "", "");
        List<String> trimmed = CommentUtil.trim(lines);
        Assert.assertEquals(1, trimmed.size());
        Assert.assertEquals("a", trimmed.get(0));
    }

    @Test
    public void testTrimNotMiddle() {
        List<String> lines = Arrays.asList("a", "b", "", "c");
        List<String> trimmed = CommentUtil.trim(lines);
        Assert.assertEquals(4, trimmed.size());
        Assert.assertEquals("a", trimmed.get(0));
        Assert.assertEquals("b", trimmed.get(1));
        Assert.assertEquals("", trimmed.get(2));
        Assert.assertEquals("c", trimmed.get(3));
    }

    @Test
    public void testTrimEmpty() {
        List<String> trimmed = CommentUtil.trim(new ArrayList<String>());
        Assert.assertEquals(0, trimmed.size());
    }

    @Test
    public void testTrimNull() {
        List<String> trimmed = CommentUtil.trim(null);
        Assert.assertEquals(0, trimmed.size());
    }

    @Test
    public void testWordAfter() {
        String wordAfter = CommentUtil.wordAfter("@param param1 Description", "@param".length());
        Assert.assertEquals("param1", wordAfter);
    }

    @Test
    public void testWordAfterPositionOutOfBounds() {
        String wordAfter = CommentUtil.wordAfter("@param param1 Description", Integer.MAX_VALUE);
        Assert.assertNull(wordAfter);
    }

    @Test
    public void testWordAfterNull() {
        String wordAfter = CommentUtil.wordAfter(null, 0);
        Assert.assertNull(wordAfter);
    }

    @Test
    public void testJavadocAfter() {
        String javadocContentAfter = CommentUtil.javadocContentAfter("@param param1 The Description\n",
                "@param param1".length());
        Assert.assertEquals("The Description", javadocContentAfter);
    }

    @Test
    public void testJavadocAfterOutOfBounds() {
        String javadocContentAfter = CommentUtil.javadocContentAfter("@param param1 The Description\n",
                Integer.MAX_VALUE);
        Assert.assertNull(javadocContentAfter);
    }

    @Test
    public void testJavadocAfterNull() {
        String javadocContentAfter = CommentUtil.javadocContentAfter(null, 0);
        Assert.assertNull(javadocContentAfter);
    }

    @Test
    public void testJavadoc() {
        String comment = "    /**\n"
                + "     * Checks if the metric can be computed on the node.\n"
                + "     *\n"
                + "     * @param node The node to check\n"
                + "     *\n"
                + "     * @return True if the metric can be computed\n"
                + "     */\n"
                + "    boolean supports(N node);\n"
                + "";
        List<String> lines = CommentUtil.multiLinesIn(comment);
        lines = CommentUtil.trim(lines);

        for (String line : lines) {
            Map<String, Integer> tags = CommentUtil.javadocTagsIn(line);
            for (String tag : tags.keySet()) {
                int pos = tags.get(tag) + tag.length() + 1;
                String wordAfter = CommentUtil.wordAfter(line, pos);
                pos = pos + wordAfter.length() + 1;
                String description = CommentUtil.javadocContentAfter(line, pos);
                if ("param".equals(tag)) {
                    Assert.assertEquals("node", wordAfter); // the parameter name
                    Assert.assertEquals("The node to check", description);
                } else if ("return".equals(tag)) {
                    Assert.assertEquals("True", wordAfter);
                    Assert.assertEquals("if the metric can be computed", description);
                }
            }
        }
    }
}
