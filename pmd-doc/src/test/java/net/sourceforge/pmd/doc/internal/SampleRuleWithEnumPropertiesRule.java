/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.doc.internal;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

public class SampleRuleWithEnumPropertiesRule extends AbstractRule {
    private enum MyEnum {
        OPTION_ONE,
        OPTION_TWO
    }

    private static final PropertyDescriptor<MyEnum> ENUM_PROPERTY = PropertyFactory.enumProperty("enumProperty", MyEnum.class)
            .desc("Description")
            .defaultValue(MyEnum.OPTION_ONE)
            .build();
    private static final PropertyDescriptor<List<MyEnum>> ENUMLIST_PROPERTY = PropertyFactory.enumListProperty("enumListProperty", MyEnum.class, Object::toString)
            .desc("Description")
            .emptyDefaultValue()
            .build();

    public SampleRuleWithEnumPropertiesRule() {
        definePropertyDescriptor(ENUM_PROPERTY);
        definePropertyDescriptor(ENUMLIST_PROPERTY);
    }

    @Override
    public void apply(Node target, RuleContext ctx) {

    }
}
