/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java13Test {


    private final JavaParsingHelper java12 =
        JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("12")
                                         .withResourceContext(getClass(), "jdkversiontests/java13/");

    private final JavaParsingHelper java13p = java12.withDefaultVersion("13-preview");

    @Test
    public void testTextBlocks() {
        ASTCompilationUnit compilationUnit = java13p.parseResource("TextBlocks.java");
        List<ASTStringLiteral> literals = compilationUnit.findDescendantsOfType(ASTStringLiteral.class);
        Assert.assertEquals(10, literals.size());
        for (int i = 0; i < 8; i++) {
            ASTStringLiteral literal = literals.get(i);
            Assert.assertTrue(literal.isTextBlock());
        }
        Assert.assertEquals("\"\"\"\n"
                                + "                <html>\n"
                                + "                    <body>\n"
                                + "                        <p>Hello, world</p>\n"
                                + "                    </body>\n"
                                + "                </html>\n"
                                + "                \"\"\"",
                            literals.get(0).getImage());
        Assert.assertFalse(literals.get(8).isTextBlock());
        Assert.assertTrue(literals.get(9).isTextBlock());
    }

    @Test(expected = ParseException.class)
    public void testTextBlocksBeforeJava13() {
        java12.parseResource("TextBlocks.java");
    }

}
