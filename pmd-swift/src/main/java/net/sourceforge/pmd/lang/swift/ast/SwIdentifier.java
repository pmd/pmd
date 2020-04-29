/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.IdentifierContext;

public final class SwIdentifier extends SwiftInnerNode<IdentifierContext> {

    SwIdentifier(IdentifierContext parseTreeNode) {
        super(parseTreeNode);
    }

    public String getName() {
        return getParseTree().getText(); // single token
    }

    @Override
    public <P, R> R acceptVisitor(SwiftVisitor<P, R> visitor, P data) {
        return visitor.visitIdent(this, data);
    }
}
