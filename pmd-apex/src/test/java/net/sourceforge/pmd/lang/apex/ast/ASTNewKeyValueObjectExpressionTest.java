/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class ASTNewKeyValueObjectExpressionTest extends ApexParserTestBase {

    @Test
    void testParameterName() {
        ASTUserClassOrInterface<?> node = parse("""
                public class Foo {\s
                    public void foo(String newName, String tempID) {\s
                        if (Contact.sObjectType.getDescribe().isCreateable() && Contact.sObjectType.getDescribe().isUpdateable()) {
                            upsert new Contact(FirstName = 'First', LastName = 'Last', Phone = '414-414-4414');
                        }
                    }\s
                }\
                """);

        ASTNewKeyValueObjectExpression keyValueExpr = node.descendants(ASTNewKeyValueObjectExpression.class).first();
        assertEquals(3, keyValueExpr.getParameterCount());

        List<ASTLiteralExpression> literals = keyValueExpr.descendants(ASTLiteralExpression.class).toList();
        assertEquals(3, literals.size());
        assertEquals("FirstName", literals.getFirst().getName());
        assertEquals("LastName", literals.get(1).getName());
        assertEquals("Phone", literals.get(2).getName());
    }

}
