/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

import apex.jorje.semantic.ast.compilation.Compilation;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class ApexXpathRuleTest {

    @Test
    public void testXPathAttributesOfUnderlyingNode() {

        String code = "class MyApexClass {\n"
            + "    void bar(){\n"
            + "\tb f = a && b;\n"
            + "         if(!x.lit() && lis != null) {\n"
            + "            foo();\n"
            + "         }\n"
            + "    }\n"
            + "}";

        ApexNode<Compilation> compilation = ApexParserTestHelpers.parse(code);

        compilation.findDescendantsOfType(ASTBooleanExpression.class).forEach(expr -> {
            List<Attribute> attributes = IteratorUtil.toList(expr.getXPathAttributesIterator());

            assertTrue(attributes.stream().anyMatch(a -> a.getName().equals("Op")));
        });
    }


}
