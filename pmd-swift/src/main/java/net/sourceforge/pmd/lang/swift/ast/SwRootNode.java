/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.TopLevelContext;

public final class SwRootNode extends SwiftInnerNode<TopLevelContext> implements RootNode {

    SwRootNode(TopLevelContext parseTreeNode) {
        super(parseTreeNode);
    }

    @Override
    public <P, R> R acceptVisitor(SwiftVisitor<P, R> visitor, P data) {
        return visitor.visitRoot(this, data);
    }
}
