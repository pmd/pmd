/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

public class DummyRoot extends DummyNode implements RootNode {

    private final Map<Integer, String> suppressMap;

    public DummyRoot(Map<Integer, String> suppressMap) {
        super(0);
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
    public String toString() {
        return "dummyRootNode";
    }

    @Override
    public String getXPathNodeName() {
        return "dummyRootNode";
    }

}
