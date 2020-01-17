/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;

/**
 * @author Clément Fournier
 * @since 6.3.0
 */
public class DummyNodeWithDeprecatedAttribute extends DummyNode {


    public DummyNodeWithDeprecatedAttribute(int id) {
        super(id);
    }

    // this is the deprecated attribute
    @Deprecated
    public int getSize() {
        return 2;
    }

    // this is a attribute that is deprecated for xpath, because it will be removed.
    // it should still be available via Java.
    @DeprecatedAttribute
    public String getName() {
        return "foo";
    }
}
