/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class JaxenXPathRuleQueryTest {

    @Test
    public void testListAttribute() {
        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);

        assertQuery(1, "//dummyNode[@SimpleAtt = \"foo\"]", dummy);
        assertQuery(1, "//dummyNode[@Enum = \"FOO\"]", dummy);
        assertQuery(0, "//dummyNode[@Enum = \"BAR\"]", dummy);

        // queries with lists are not supported with xpath 1.0
        assertQuery(0, "//dummyNode[@List = \"[A, B]\"]", dummy);
        assertQuery(0, "//dummyNode[contains(@List, \"B\")]", dummy);
        assertQuery(0, "//dummyNode[@List = \"C\"]", dummy);
        assertQuery(0, "//dummyNode[@EnumList = \"[FOO, BAR]\"]", dummy);
        assertQuery(0, "//dummyNode[contains(@EnumList, \"BAR\")]", dummy);
        assertQuery(0, "//dummyNode[@EmptyList = \"A\"]", dummy);
    }

    private static void assertQuery(int resultSize, String xpath, Node node) {
        JaxenXPathRuleQuery query = createQuery(xpath);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());
        List<Node> result = query.evaluate(node, data);
        Assert.assertEquals(resultSize, result.size());
    }

    private static JaxenXPathRuleQuery createQuery(String xpath) {
        JaxenXPathRuleQuery query = new JaxenXPathRuleQuery();
        query.setVersion("1.0");
        query.setProperties(Collections.<PropertyDescriptor<?>, Object>emptyMap());
        query.setXPath(xpath);
        return query;
    }
}
