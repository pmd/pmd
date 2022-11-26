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
        ASTUserClassOrInterface<?> node = parse("public class Foo { \n"
                + "    public void foo(String newName, String tempID) { \n"
                + "        if (Contact.sObjectType.getDescribe().isCreateable() && Contact.sObjectType.getDescribe().isUpdateable()) {\n"
                + "            upsert new Contact(FirstName = 'First', LastName = 'Last', Phone = '414-414-4414');\n"
                + "        }\n" + "    } \n" + "}");

        ASTNewKeyValueObjectExpression keyValueExpr = node.getFirstDescendantOfType(ASTNewKeyValueObjectExpression.class);
        assertEquals(3, keyValueExpr.getParameterCount());

        List<ASTLiteralExpression> literals = keyValueExpr.findDescendantsOfType(ASTLiteralExpression.class);
        assertEquals(3, literals.size());
        assertEquals("FirstName", literals.get(0).getName());
        assertEquals("LastName", literals.get(1).getName());
        assertEquals("Phone", literals.get(2).getName());
    }

}
