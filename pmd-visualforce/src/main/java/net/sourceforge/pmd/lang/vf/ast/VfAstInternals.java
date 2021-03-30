/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.vf.DataType;

/**
 * This is internal API, and can be changed at any time.
 */
@InternalApi
public final class VfAstInternals {

    private VfAstInternals() {
        // utility class
    }

    public static void setDataType(VfTypedNode node, DataType dataType) {
        ((AbstractVFDataNode) node).setDataType(dataType);
    }
}
