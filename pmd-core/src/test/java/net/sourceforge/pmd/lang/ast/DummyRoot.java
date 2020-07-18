/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

public class DummyRoot extends DummyNode implements GenericNode<DummyNode>, RootNode {

    private final Map<Integer, String> suppressMap;

    public DummyRoot(Map<Integer, String> suppressMap) {
        super();
        this.suppressMap = suppressMap;
    }

    public DummyRoot() {
        this(Collections.emptyMap());
    }

    @Override
    public Map<Integer, String> getNoPmdComments() {
        return suppressMap;
    }

    @Override
    public String getXPathNodeName() {
        return "dummyRootNode";
    }

}
