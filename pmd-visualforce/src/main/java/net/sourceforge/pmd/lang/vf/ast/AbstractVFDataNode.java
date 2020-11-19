/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.vf.DataType;

/**
 * Represents a node that displays a piece of data.
 */
@Deprecated
@InternalApi
public class AbstractVFDataNode extends AbstractVFNode {
    private DataType dataType;

    public AbstractVFDataNode(int id) {
        super(id);
    }

    public AbstractVFDataNode(VfParser parser, int id) {
        super(id);
        this.parser = parser;
    }

    /**
     * Example XPath 1.0 and 2.0: {@code //Identifier[@DataType='DateTime']}
     *
     * @return data type that this node refers to. A null value indicates that no matching Metadata was found for this
     * node. null differs from {@link DataType#Unknown} which indicates that Metadata was found but it wasn't mappable
     * to one of the enums.
     */
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
