/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang.ast;

import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;

public class DummyNode extends AbstractNodeWithTextCoordinates<DummyNode> {

    @Deprecated
    @Override
    public String toString() {
        return "dummyNode";
    }


    @Override
    public String getXPathNodeName() {
        return "dummyNode";
    }
}
