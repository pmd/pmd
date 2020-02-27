/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

/**
 * Tests new java14 preview features.
 */
public class Java14PreviewTest {
    private final JavaParsingHelper java14 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("14")
                                             .withResourceContext(Java14Test.class, "jdkversiontests/java14/");

    private final JavaParsingHelper java14p = java14.withDefaultVersion("14-preview");
    private final JavaParsingHelper java13 = java14.withDefaultVersion("13");

    @Test
    public void textBlocks() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("TextBlocks.java");
        List<ASTLiteral> literals = compilationUnit.findDescendantsOfType(ASTLiteral.class);
        Assert.assertEquals(16, literals.size());
        Assert.assertFalse(literals.get(2).isTextBlock());
        Assert.assertFalse(literals.get(12).isTextBlock());

        List<ASTLiteral> textBlocks = new ArrayList<>();
        for (ASTLiteral literal : literals) {
            if (literal.isTextBlock()) {
                textBlocks.add(literal);
            }
        }
        Assert.assertEquals(14, textBlocks.size());
        Assert.assertEquals("\"\"\"\n"
                                + "                      <html>   \n"
                                + "                          <body>\n"
                                + "                              <p>Hello, world</p>    \n"
                                + "                          </body> \n"
                                + "                      </html>   \n"
                                + "                      \"\"\"",
                            textBlocks.get(0).getImage());
        Assert.assertEquals("<html>\n"
                          + "    <body>\n"
                          + "        <p>Hello, world</p>\n"
                          + "    </body>\n"
                          + "</html>\n", textBlocks.get(0).getTextBlockContent());

        // with escapes
        Assert.assertEquals("\"\"\"\n"
                          + "                      <html>\\r\n"
                          + "                          <body>\\r\n"
                          + "                              <p>Hello, world</p>\\r\n"
                          + "                          </body>\\r\n"
                          + "                      </html>\\r\n"
                          + "                      \"\"\"", textBlocks.get(3).getImage());
        Assert.assertEquals("<html>\r\n"
                          + "    <body>\r\n"
                          + "        <p>Hello, world</p>\r\n"
                          + "    </body>\r\n"
                          + "</html>\r\n", textBlocks.get(3).getTextBlockContent());
        // season
        Assert.assertEquals("\"\"\"\n                winter\"\"\"", textBlocks.get(4).getImage());
        Assert.assertEquals("winter", textBlocks.get(4).getTextBlockContent());
        // period
        Assert.assertEquals("\"\"\"\n"
                + "                        winter\n"
                + "                        \"\"\"", textBlocks.get(5).getImage());
        Assert.assertEquals("winter\n", textBlocks.get(5).getTextBlockContent());
        // empty
        Assert.assertEquals("\"\"\"\n                       \"\"\"", textBlocks.get(8).getImage());
        Assert.assertEquals("", textBlocks.get(8).getTextBlockContent());
        // escaped text block in inside text block
        Assert.assertEquals("\"\"\"\n"
                          + "            String text = \\\"\"\"\n"
                          + "                A text block inside a text block\n"
                          + "            \\\"\"\";\n"
                          + "            \"\"\"", textBlocks.get(11).getImage());
        Assert.assertEquals("String text = \"\"\"\n"
                          + "    A text block inside a text block\n"
                          + "\"\"\";\n", textBlocks.get(11).getTextBlockContent());
        // new escape: line continuation
        Assert.assertEquals("\"\"\"\n"
                          + "                      Lorem ipsum dolor sit amet, consectetur adipiscing \\\n"
                          + "                      elit, sed do eiusmod tempor incididunt ut labore \\\n"
                          + "                      et dolore magna aliqua.\\\n"
                          + "                      \"\"\"", textBlocks.get(12).getImage());
        Assert.assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing "
                + "elit, sed do eiusmod tempor incididunt ut labore "
                + "et dolore magna aliqua.", textBlocks.get(12).getTextBlockContent());
        // new escape: space escape
        Assert.assertEquals("\"\"\"\n"
                          + "                        red  \\s\n"
                          + "                        green\\s\n"
                          + "                        blue \\s\n"
                          + "                        \"\"\"", textBlocks.get(13).getImage());
        Assert.assertEquals("red   \n"
                          + "green \n"
                          + "blue  \n", textBlocks.get(13).getTextBlockContent());
    }

    @Test(expected = ParseException.class)
    public void textBlocksBeforeJava14PreviewShouldFail() {
        java13.parseResource("TextBlocks.java");
    }

    @Test(expected = ParseException.class)
    public void stringEscapeSequenceShouldFail() {
        java14.parse("class Foo { String s =\"a\\sb\"; }");
    }
}
