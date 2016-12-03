/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers.parse;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTUserTriggerTest {

    @Test
    public void testTriggerName() {
        ApexNode<Compilation> node = parse("trigger HelloWorldTrigger on Book__c (before insert) {\n"
                + "   Book__c[] books = Trigger.new;\n" + "   MyHelloWorld.applyDiscount(books);\n" + "}\n");
        Assert.assertSame(ASTUserTrigger.class, node.getClass());
        Assert.assertEquals("HelloWorldTrigger", node.getImage());
    }
}
