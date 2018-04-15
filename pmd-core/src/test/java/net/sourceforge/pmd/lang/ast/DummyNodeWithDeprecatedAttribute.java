/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

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
}
