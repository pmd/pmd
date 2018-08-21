/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.AstNode;

public abstract class ApexRootNode<T extends AstNode> extends AbstractApexNode<T> implements RootNode {
    private String source;

    public ApexRootNode(T node) {
        super(node);
    }

    // For top level classes, the end is the end of file.
    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        super.calculateLineNumbers(positioner);
        this.endLine = positioner.getLastLine();
        this.endColumn = positioner.getLastLineColumn();
    }

    @Override
    protected void handleSourceCode(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
