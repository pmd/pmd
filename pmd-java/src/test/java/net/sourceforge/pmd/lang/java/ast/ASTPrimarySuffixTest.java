/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class ASTPrimarySuffixTest {

    @Test
    public void testArrayDereference() {
        Set<ASTPrimarySuffix> ops = getNodes(ASTPrimarySuffix.class, TEST1);
        assertTrue(ops.iterator().next().isArrayDereference());
    }

    @Test
    public void testArguments() {
        Set<ASTPrimarySuffix> ops = getNodes(ASTPrimarySuffix.class, TEST2);
        assertTrue(ops.iterator().next().isArguments());
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL + "  {x[0] = 2;}" + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + "  {foo(a);}" + PMD.EOL + "}";
}
