/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTSwitchStatementTest extends BaseParserTest {

    @Test
    public void exhaustiveEnumSwitchWithDefault() {
        ASTSwitchStatement switchStatement = getNodes(ASTSwitchStatement.class,
                "import java.nio.file.AccessMode; class Foo { void bar(AccessMode m) {"
                + "switch (m) { case READ: break; default: break; } } }")
                .get(0);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch()); // this should not throw a NPE...
        Assert.assertTrue(switchStatement.hasDefaultCase());
    }
}
