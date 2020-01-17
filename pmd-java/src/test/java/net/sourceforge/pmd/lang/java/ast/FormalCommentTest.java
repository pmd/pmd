/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

public class FormalCommentTest {

    @Test
    public void testJavadocTagsAsChildren() {
        String comment = "    /**\n"
                + "     * Checks if the metric can be computed on the node.\n"
                + "     *\n"
                + "     * @param node The node to check\n"
                + "     *\n"
                + "     * @return True if the metric can be computed\n"
                + "     */\n"
                + "    boolean supports(N node);\n"
                + "";

        JavaccToken token = new JavaccToken(comment);
        FormalComment commentNode = new FormalComment(token);

        Assert.assertEquals(2, commentNode.getNumChildren());

        JavadocElement paramTag = (JavadocElement) commentNode.getChild(0);
        Assert.assertEquals("param", paramTag.tag().label);

        JavadocElement returnTag = (JavadocElement) commentNode.getChild(1);
        Assert.assertEquals("return", returnTag.tag().label);
    }
}
