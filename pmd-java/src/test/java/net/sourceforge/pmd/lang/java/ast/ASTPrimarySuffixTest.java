/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTPrimarySuffixTest extends BaseParserTest {

    @Test
    public void testArrayDereference() {
        List<ASTPrimarySuffix> ops = java.getNodes(ASTPrimarySuffix.class, TEST1);
        assertTrue(ops.get(0).isArrayDereference());
    }

    @Test
    public void testArguments() {
        List<ASTPrimarySuffix> ops = java.getNodes(ASTPrimarySuffix.class, TEST2);
        assertTrue(ops.get(0).isArguments());
    }

    private static final String TEST1 = "public class Foo {\n  {x[0] = 2;}\n}";

    private static final String TEST2 = "public class Foo {\n  {foo(a);}\n}";
}
