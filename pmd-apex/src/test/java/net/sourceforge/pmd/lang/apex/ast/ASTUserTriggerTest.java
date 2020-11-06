/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ASTUserTriggerTest extends ApexParserTestBase {

    @Test
    public void testTriggerName() {
        ApexNode<?> node = parse("trigger HelloWorldTrigger on Book__c (before insert, after update) {\n"
                + "   Book__c[] books = Trigger.new;\n" + "   MyHelloWorld.applyDiscount(books);\n" + "}\n");
        Assert.assertSame(ASTUserTrigger.class, node.getClass());
        Assert.assertEquals("HelloWorldTrigger", node.getImage());
        ASTUserTrigger trigger = (ASTUserTrigger) node;
        Assert.assertEquals("Book__c", trigger.getTargetName());
        Assert.assertEquals(Arrays.asList(TriggerUsage.AFTER_UPDATE, TriggerUsage.BEFORE_INSERT), trigger.getUsages());
    }
}
