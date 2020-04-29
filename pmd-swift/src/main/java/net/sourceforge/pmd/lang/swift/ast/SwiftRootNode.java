/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.TopLevelContext;

public final class SwiftRootNode extends SwiftNodeImpl<TopLevelContext> implements RootNode {

    public SwiftRootNode(TopLevelContext parseTreeNode) {
        super(parseTreeNode);
    }
}
