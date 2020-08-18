/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;

/**
 *
 */
abstract class AbstractInvocationExpr extends AbstractJavaExpr implements InvocationNode {

    private OverloadSelectionResult result;

    AbstractInvocationExpr(int i) {
        super(i);
    }

    void setOverload(OverloadSelectionResult result) {
        assert result != null;
        this.result = result;
    }

    @Override
    public OverloadSelectionResult getOverloadSelectionInfo() {
        getTypeMirror(); // force evaluation
        assert result != null : "Something went wrong during overload resolution for " + this;
        return result;
    }
}
