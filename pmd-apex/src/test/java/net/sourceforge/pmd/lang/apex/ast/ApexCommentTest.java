/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

public class ApexCommentTest extends ApexParserTestBase {


    @Test
    public void testContainsComment1() {
        ASTApexFile file = apex.parse("class Foo {void foo(){try {\n"
                                          + "} catch (Exception e) {\n"
                                          + "  /* OK: block comment inside of empty catch block; should not be reported */\n"
                                          + "}}}");

        ASTCatchBlockStatement catchBlock = file.descendants(ASTCatchBlockStatement.class).crossFindBoundaries().firstOrThrow();
        Assert.assertTrue(catchBlock.getContainsComment());
    }
}
