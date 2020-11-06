/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTSwitchLabelTest extends BaseParserTest {

    @Test
    public void testDefaultOff() {
        List<ASTSwitchLabel> ops = java.getNodes(ASTSwitchLabel.class, TEST1);
        assertFalse(ops.get(0).isDefault());
    }

    @Test
    public void testDefaultSet() {
        List<ASTSwitchLabel> ops = java.getNodes(ASTSwitchLabel.class, TEST2);
        assertTrue(ops.get(0).isDefault());
    }

    private static final String TEST1 = "public class Foo {\n void bar() {\n  switch (x) {\n   case 1: y = 2;\n  }\n }\n}";

    private static final String TEST2 = "public class Foo {\n void bar() {\n  switch (x) {\n   default: y = 2;\n  }\n }\n}";
}
