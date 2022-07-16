/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;
import java.util.regex.Pattern;


/**
 * Since there's no RegexMultiProperty the base class is only partially implemented,
 * and some tests are overridden with no-op ones.
 *
 * @author Clément Fournier
 * @since 6.2.0
 */
@Deprecated
class RegexPropertyTest extends AbstractPropertyDescriptorTester<Pattern> {
    RegexPropertyTest() {
        super("Regex");
    }


    @Override
    protected Pattern createValue() {
        return Pattern.compile("abc++");
    }


    @Override
    protected Pattern createBadValue() {
        return null;
    }


    @Override
    protected PropertyDescriptor<Pattern> createProperty() {
        return RegexProperty.named("foo").defaultValue("(ec|sa)+").desc("the description").build();
    }


    @Override
    protected PropertyDescriptor<Pattern> createBadProperty() {
        return RegexProperty.named("foo").defaultValue("(ec|sa").desc("the description").build();
    }


    // The following are deliberately unimplemented, since they are only relevant to the tests of the multiproperty

    @Override
    protected PropertyDescriptor<List<Pattern>> createMultiProperty() {
        throw new UnsupportedOperationException();
    }


    @Override
    protected PropertyDescriptor<List<Pattern>> createBadMultiProperty() {
        throw new UnsupportedOperationException();
    }

    @Override
    void testAddAttributesMulti() {
    }


    @Override
    void testAsDelimitedString() {
    }


    @Override
    void testErrorForBadMulti() {
    }


    @Override
    void testErrorForCorrectMulti() {
    }


    @Override
    void testFactoryMultiValueDefaultDelimiter() {
    }


    @Override
    void testFactoryMultiValueCustomDelimiter() {
    }


    @Override
    void testTypeMulti() {
    }


    @Override
    void testIsMultiValueMulti() {
    }


}
