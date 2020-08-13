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

public class Java15Test {
    private final JavaParsingHelper java15 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("15")
                                             .withResourceContext(Java15Test.class, "jdkversiontests/java15/");
    private final JavaParsingHelper java15p = java15.withDefaultVersion("15-preview");
    private final JavaParsingHelper java14 = java15.withDefaultVersion("14");

    @Test
    public void textBlocks() {
        textBlocks(java15);
        textBlocks(java15p);
    }

    private void textBlocks(JavaParsingHelper parser) {
        ASTCompilationUnit compilationUnit = parser.parseResource("TextBlocks.java");
        List<ASTLiteral> literals = compilationUnit.findDescendantsOfType(ASTLiteral.class);
        Assert.assertEquals(22, literals.size());
        Assert.assertFalse(literals.get(2).isTextBlock());
        Assert.assertFalse(literals.get(12).isTextBlock());
        Assert.assertFalse(literals.get(17).isTextBlock());
        Assert.assertFalse(literals.get(18).isTextBlock());
        Assert.assertFalse(literals.get(20).isTextBlock());
        Assert.assertFalse(literals.get(21).isTextBlock());

        List<ASTLiteral> textBlocks = new ArrayList<>();
        for (ASTLiteral literal : literals) {
            if (literal.isTextBlock()) {
                textBlocks.add(literal);
            }
        }
        Assert.assertEquals(16, textBlocks.size());
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

        // Note: More tests are in ASTLiteralTest.
    }

    @Test(expected = ParseException.class)
    public void textBlocksBeforeJava15ShouldFail() {
        java14.parseResource("TextBlocks.java");
    }

    @Test(expected = ParseException.class)
    public void stringEscapeSequenceShouldFail() {
        java14.parse("class Foo { String s =\"a\\sb\"; }");
    }

}
