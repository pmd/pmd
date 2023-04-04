/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;

/**
 * @author Clément Fournier
 * @since 6.3.0
 */
public class DummyNodeWithDeprecatedAttribute extends DummyNode {

    // this is the deprecated attribute
    @Deprecated
    public int getSize() {
        return 2;
    }

    // this is a attribute that is deprecated for xpath, because it will be removed.
    // it should still be available via Java.
    @DeprecatedAttribute(replaceWith = "@Image")
    public String getName() {
        return "foo";
    }

    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        return new AttributeAxisIterator(this);
    }
}
