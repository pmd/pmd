/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.ast;

import net.sourceforge.pmd.lang.visualforce.DataType;

/**
 * Represents a node that displays a piece of data.
 */
abstract class AbstractVFDataNode extends AbstractVfNode implements VfTypedNode {

    private DataType dataType;

    AbstractVFDataNode(int id) {
        super(id);
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
