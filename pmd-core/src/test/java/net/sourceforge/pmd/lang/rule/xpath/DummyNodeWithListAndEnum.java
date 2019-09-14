/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.DummyNode;

public class DummyNodeWithListAndEnum extends DummyNode {
    public DummyNodeWithListAndEnum(int id) {
        super(id);
        beginLine = 1;
        beginColumn = 1;
        endLine = 1;
        endColumn = 2;
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

    public List<String> getEmptyList() {
        return Collections.emptyList();
    }

    public String getSimpleAtt() {
        return "foo";
    }
}
