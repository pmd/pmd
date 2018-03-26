/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * @author Clément Fournier
 * @since 6.2.0
 */
public class DummyNodeWithDeprecatedAttribute extends DummyNode {


    public DummyNodeWithDeprecatedAttribute(int id) {
        super(id);
    }


    @Deprecated
    public int getSize() {
        return 2;
    }
}
