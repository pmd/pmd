/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ASTUserTriggerTest extends ApexParserTestBase {

    @Test
    void testTriggerName() {
        ApexNode<?> node = parse("trigger HelloWorldTrigger on Book__c (before insert, after update) {\n"
                + "   Book__c[] books = Trigger.new;\n" + "   MyHelloWorld.applyDiscount(books);\n" + "}\n");
        assertSame(ASTUserTrigger.class, node.getClass());
        assertEquals("HelloWorldTrigger", node.getImage());
        ASTUserTrigger trigger = (ASTUserTrigger) node;
        assertEquals("Book__c", trigger.getTargetName());
        assertEquals(Arrays.asList(TriggerUsage.AFTER_UPDATE, TriggerUsage.BEFORE_INSERT), trigger.getUsages());
    }
}
