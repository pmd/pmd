/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.vf.DataType;

/**
 * Represents a node that displays a piece of data.
 */
class AbstractVFDataNode extends AbstractVFNode implements VfTypedNode {

    private DataType dataType;

    public AbstractVFDataNode(int id) {
        super(id);
    }

    public AbstractVFDataNode(VfParser parser, int id) {
        super(id);
        this.parser = parser;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
