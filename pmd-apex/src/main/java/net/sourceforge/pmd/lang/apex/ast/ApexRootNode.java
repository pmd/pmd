/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.services.Version;

@Deprecated
@InternalApi
public abstract class ApexRootNode<T extends AstNode> extends AbstractApexNode<T> implements RootNode {
    @Deprecated
    @InternalApi
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

    /**
     * Gets the apex version this class has been compiled with.
     * Use {@link Version} to compare, e.g.
     * {@code node.getApexVersion() >= Version.V176.getExternal()}
     * @return the apex version
     */
    public double getApexVersion() {
        return node.getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }
}
