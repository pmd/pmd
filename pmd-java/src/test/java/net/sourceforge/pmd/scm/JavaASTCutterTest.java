/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;

public class JavaASTCutterTest extends AbstractASTCutterTest {
    public JavaASTCutterTest() throws IOException {
        super(new JavaMinimizerModule().getDefaultParser(), Charset.defaultCharset());
    }

    @Test
    public void testCutting() throws IOException {
        Node root = initializeFor(getClass().getResource("cutter-test-input.txt"));
        List<Node> list = new ArrayList<>();
        list.add(root.getFirstDescendantOfType(ASTClassOrInterfaceBodyDeclaration.class));
        testExactRemoval(list);
        Helper.assertResultedSourceEquals(StandardCharsets.UTF_8, getClass().getResource("cutter-test-output.txt"), tempFile);
    }
}
