/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.TopLevelContext;

/**
 * Root node for swift files.
 */
public class SwiftFileNode extends SwiftInnerNode implements RootNode {

    SwiftFileNode(TopLevelContext toplevel) {
        addOnlyChild(toplevel, toplevel.start, toplevel.stop);
    }

    @Override
    public String getXPathNodeName() {
        return "SwiftFile";
    }
}
