/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class MockRuleWithDeprecatedProperties extends MockRuleWithNoProperties {
    static final PropertyDescriptor<String> STRING_PROPERTY = PropertyFactory.stringProperty("stringProp")
            .desc("deprecated! This should not be used anymore")
            .defaultValue("a")
            .build();

    enum SampleEnum { VALUE_A, VALUE_B }

    static final PropertyDescriptor<SampleEnum> ENUM_PROPERTY = PropertyFactory.enumPropertyTransitional("enumProp",
            SampleEnum.class, getDeprecatedMapping())
            .desc("This prop replaces the old stringProp")
            .defaultValue(SampleEnum.VALUE_A)
            .build();

    private static Map<String, SampleEnum> getDeprecatedMapping() {
        Map<String, SampleEnum> map = new HashMap<>();
        map.put("a", SampleEnum.VALUE_A);
        map.put("b", SampleEnum.VALUE_B);
        return map;
    }

    public MockRuleWithDeprecatedProperties() {
        definePropertyDescriptor(STRING_PROPERTY);
        definePropertyDescriptor(ENUM_PROPERTY);
    }
}
