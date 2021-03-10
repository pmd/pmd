/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.vf.DataType;

/**
 * Represents a node that displays a piece of data.
 */
public interface VfTypedNode extends VfNode {

    /**
     * Returns the data type this node refers to. A null value indicates that no matching Metadata was found for this
     * node. null differs from {@link DataType#Unknown} which indicates that Metadata was found but it wasn't mappable
     * to one of the enums.
     *
     * <p>Example XPath 1.0 and 2.0: {@code //Identifier[@DataType='DateTime']}
     */
    DataType getDataType();
}
