/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class ASTSwitchLabelTest {

    @Test
    public void testDefaultOff() {
        Set<ASTSwitchLabel> ops = getNodes(ASTSwitchLabel.class, TEST1);
        assertFalse(ops.iterator().next().isDefault());
    }

    @Test
    public void testDefaultSet() {
        Set<ASTSwitchLabel> ops = getNodes(ASTSwitchLabel.class, TEST2);
        assertTrue(ops.iterator().next().isDefault());
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  switch (x) {"
            + PMD.EOL + "   case 1: y = 2;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  switch (x) {"
            + PMD.EOL + "   default: y = 2;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + "}";
}
