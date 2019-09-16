/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class SaxonXPathRuleQueryTest {

    @Test
    public void testListAttribute() {
        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);

        assertQuery(1, "//dummyNode[@List = \"A\"]", dummy);
        assertQuery(1, "//dummyNode[@List = \"B\"]", dummy);
        assertQuery(0, "//dummyNode[@List = \"C\"]", dummy);
        assertQuery(1, "//dummyNode[@Enum = \"FOO\"]", dummy);
        assertQuery(0, "//dummyNode[@Enum = \"BAR\"]", dummy);
        assertQuery(1, "//dummyNode[@EnumList = \"FOO\"]", dummy);
        assertQuery(1, "//dummyNode[@EnumList = \"BAR\"]", dummy);
        assertQuery(1, "//dummyNode[@EnumList = (\"FOO\", \"BAR\")]", dummy);
        assertQuery(0, "//dummyNode[@EmptyList = (\"A\")]", dummy);
    }

    private static void assertQuery(int resultSize, String xpath, Node node) {
        SaxonXPathRuleQuery query = createQuery(xpath);
        List<Node> result = query.evaluate(node, new RuleContext());
        Assert.assertEquals(resultSize, result.size());
    }

    private static SaxonXPathRuleQuery createQuery(String xpath) {
        SaxonXPathRuleQuery query = new SaxonXPathRuleQuery();
        query.setVersion("2.0");
        query.setProperties(Collections.<PropertyDescriptor<?>, Object>emptyMap());
        query.setXPath(xpath);
        return query;
    }
}
