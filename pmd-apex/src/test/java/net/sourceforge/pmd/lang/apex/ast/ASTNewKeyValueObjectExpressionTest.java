/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTNewKeyValueObjectExpressionTest extends ApexParserTestBase {

    @Test
    public void testParameterName() {
        ApexNode<Compilation> node = parse("public class Foo { \n"
                + "    public void foo(String newName, String tempID) { \n"
                + "        if (Contact.sObjectType.getDescribe().isCreateable() && Contact.sObjectType.getDescribe().isUpdateable()) {\n"
                + "            upsert new Contact(FirstName = 'First', LastName = 'Last', Phone = '414-414-4414');\n"
                + "        }\n" + "    } \n" + "}");

        ASTNewKeyValueObjectExpression keyValueExpr = node.getFirstDescendantOfType(ASTNewKeyValueObjectExpression.class);
        Assert.assertEquals(3, keyValueExpr.getParameterCount());

        List<ASTLiteralExpression> literals = keyValueExpr.findDescendantsOfType(ASTLiteralExpression.class);
        Assert.assertEquals(3, literals.size());
        Assert.assertEquals("FirstName", literals.get(0).getName());
        Assert.assertEquals("LastName", literals.get(1).getName());
        Assert.assertEquals("Phone", literals.get(2).getName());
    }

}
