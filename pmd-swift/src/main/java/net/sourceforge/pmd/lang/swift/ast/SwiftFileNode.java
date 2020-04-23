/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.TopLevelContext;

/**
 * Root node for swift files.
 */
public class SwiftFileNode extends SwiftNode implements RootNode {

    public SwiftFileNode(TopLevelContext toplevel) {
        addChild(toplevel);
        toplevel.setParent(this);
        this.start = toplevel.start;
        this.stop = toplevel.stop;
    }

    @Override
    public String getXPathNodeName() {
        return "SwiftFile";
    }
}
