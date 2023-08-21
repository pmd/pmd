/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author Brian Remedios
 */
class BooleanPropertyTest extends AbstractPropertyDescriptorTester<Boolean> {

    BooleanPropertyTest() {
        super("Boolean");
    }


    @Override
    protected Boolean createValue() {
        return randomBool();
    }


    @Override
    @Test
    void testErrorForBadSingle() {
        // override, cannot create a 'bad' boolean per se
    }


    @Override
    @Test
    void testErrorForBadMulti() {
        // override, cannot create a 'bad' boolean per se
    }


    @Override
    protected Boolean createBadValue() {
        return null;
    }


    @Override
    protected PropertyDescriptor<Boolean> createProperty() {
        return new BooleanProperty("testBoolean", "Test boolean property", false, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Boolean>> createMultiProperty() {
        return new BooleanMultiProperty("testBoolean", "Test boolean property",
                                        new Boolean[] {false, true, true}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Boolean>> createBadMultiProperty() {
        return new BooleanMultiProperty("", "Test boolean property", new Boolean[] {false, true, true}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Boolean> createBadProperty() {
        return new BooleanProperty("testBoolean", "", false, 1.0f);
    }

}
