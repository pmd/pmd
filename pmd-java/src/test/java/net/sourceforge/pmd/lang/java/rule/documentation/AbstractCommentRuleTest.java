/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.MultiLineComment;

public class AbstractCommentRuleTest {

    private AbstractCommentRule testSubject = new AbstractCommentRule() {
    };

    /**
     * Blank lines in comments should not raise an exception. See bug #1048.
     */
    @Test
    public void testFilteredCommentIn() {
        JavaccToken token = new JavaccToken("/* multi line comment with blank lines\n\n\n */");

        String filtered = testSubject.filteredCommentIn(new MultiLineComment(token));
        assertEquals("multi line comment with blank lines", filtered);

        token = new JavaccToken("/** a formal comment with blank lines\n\n\n */");
        filtered = testSubject.filteredCommentIn(new FormalComment(token));
        assertEquals("a formal comment with blank lines", filtered);
    }

    @Test
    public void testTagsIndicesIn() {
        String comment = "    /**\n"
                + "     * Checks if the metric can be computed on the node.\n"
                + "     *\n"
                + "     * @param node The node to check\n"
                + "     *\n"
                + "     * @return True if the metric can be computed\n"
                + "     */\n"
                + "    boolean supports(N node);\n"
                + "";

        List<Integer> indices = testSubject.tagsIndicesIn(comment);
        Assert.assertEquals(2, indices.size());
        Assert.assertEquals(79, indices.get(0).intValue());
        Assert.assertEquals(123, indices.get(1).intValue());
    }

    @Test
    public void testCommentAssignments() {
        ASTCompilationUnit node = JavaParsingHelper.WITH_PROCESSING.parse("public class Foo {" + "     /** Comment 1 */\n"
                                                                + "        public void method1() {}\n" + "    \n" + "        /** Comment 2 */\n" + "    \n"
                                                                + "        /** Comment 3 */\n" + "        public void method2() {}" + "}");

        testSubject.assignCommentsToDeclarations(node);
        List<ASTMethodDeclaration> methods = node.findDescendantsOfType(ASTMethodDeclaration.class);
        Assert.assertEquals("/** Comment 1 */", AbstractCommentRule.getComment(methods.get(0)).getImage());
        Assert.assertEquals("/** Comment 3 */", AbstractCommentRule.getComment(methods.get(1)).getImage());
    }
}
