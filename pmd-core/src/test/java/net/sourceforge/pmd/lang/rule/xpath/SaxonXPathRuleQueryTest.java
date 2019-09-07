/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class SaxonXPathRuleQueryTest {

    @Test
    public void testListAttribute() {
        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);

        SaxonXPathRuleQuery query = createQuery("//dummyNode[@List = \"A\"]");
        List<Node> result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(1, result.size());

        query = createQuery("//dummyNode[@List = \"B\"]");
        result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(1, result.size());

        query = createQuery("//dummyNode[@List = \"C\"]");
        result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(0, result.size());

        query = createQuery("//dummyNode[@Enum = \"FOO\"]");
        result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(1, result.size());

        query = createQuery("//dummyNode[@Enum = \"BAR\"]");
        result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(0, result.size());

        query = createQuery("//dummyNode[@EnumList = \"FOO\"]");
        result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(1, result.size());

        query = createQuery("//dummyNode[@EnumList = \"BAR\"]");
        result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(1, result.size());

        query = createQuery("//dummyNode[@EnumList = (\"FOO\", \"BAR\")]");
        result = query.evaluate(dummy, new RuleContext());
        Assert.assertEquals(1, result.size());
    }

    private SaxonXPathRuleQuery createQuery(String xpath) {
        SaxonXPathRuleQuery query = new SaxonXPathRuleQuery();
        query.setVersion("2.0");
        query.setProperties(Collections.<PropertyDescriptor<?>, Object>emptyMap());
        query.setXPath(xpath);
        return query;
    }

    public static class DummyNodeWithListAndEnum extends DummyNode {

        public DummyNodeWithListAndEnum(int id) {
            super(id);
        }

        public enum MyEnum {
            FOO, BAR
        }

        public MyEnum getEnum() {
            return MyEnum.FOO;
        }

        public List<String> getList() {
            return Arrays.asList("A", "B");
        }

        public List<MyEnum> getEnumList() {
            return Arrays.asList(MyEnum.FOO, MyEnum.BAR);
        }
    }
}
