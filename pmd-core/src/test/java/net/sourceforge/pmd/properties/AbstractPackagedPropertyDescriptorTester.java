/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Map;

import org.junit.Test;

/**
 * @author Clément Fournier
 */
public abstract class AbstractPackagedPropertyDescriptorTester<T> extends AbstractPropertyDescriptorTester<T> {

    /* default */ AbstractPackagedPropertyDescriptorTester(String typeName) {
        super(typeName);
    }


    @Test
    public void testMissingPackageNames() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(PropertyDescriptorField.LEGAL_PACKAGES);
        getMultiFactory().build(attributes); // no exception, null is ok
        getSingleFactory().build(attributes);
    }


    @Override
    protected Map<PropertyDescriptorField, String> getPropertyDescriptorValues() {
        Map<PropertyDescriptorField, String> attributes = super.getPropertyDescriptorValues();
        attributes.put(PropertyDescriptorField.LEGAL_PACKAGES, "java.lang");
        return attributes;
    }
}
