/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTSwitchLabelTest extends BaseParserTest {

    @Test
    void testDefaultOff() {
        List<ASTSwitchLabel> ops = java.getNodes(ASTSwitchLabel.class, "public class Foo {\n void bar() {\n  switch (x) {\n   case 1: y = 2;\n  }\n }\n}");
        assertFalse(ops.get(0).isDefault());
    }

    @Test
    void testDefaultSet() {
        List<ASTSwitchLabel> ops = java.getNodes(ASTSwitchLabel.class, TEST2);
        assertTrue(ops.get(0).isDefault());
    }

    private static final String TEST1 = "public class Foo {\n void bar() {\n  switch (x) {\n   case 1: y = 2;\n  }\n }\n}";

    private static final String TEST2 = "public class Foo {\n void bar() {\n  switch (x) {\n   default: y = 2;\n  }\n }\n}";
}
