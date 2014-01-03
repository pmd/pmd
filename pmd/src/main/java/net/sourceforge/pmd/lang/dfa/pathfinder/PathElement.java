/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.pathfinder;

import net.sourceforge.pmd.lang.dfa.DataFlowNode;

public class PathElement {

    public int currentChild;
    public DataFlowNode node;
    public DataFlowNode pseudoRef;

    PathElement(DataFlowNode node) {
        this.node = node;
    }

    PathElement(DataFlowNode node, DataFlowNode ref) {
        this.node = node;
        this.pseudoRef = ref;
    }

    public boolean isPseudoPathElement() {
        return pseudoRef != null;
    }
}

