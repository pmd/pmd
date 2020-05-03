/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.Compilation;
import apex.jorje.services.Version;

public final class ASTApexFile extends AbstractApexNode<AstNode> implements RootNode {

    private Map<Integer, String> suppressMap = Collections.emptyMap();

    ASTApexFile(AbstractApexNode<? extends Compilation> child) {
        super(child.getNode());
        addChild(child, 0);
        this.beginLine = child.getBeginLine();
        this.endLine = child.getEndLine();
        this.beginColumn = child.getBeginColumn();
        this.endColumn = child.getEndColumn();
    }

    /**
     * Gets the apex version this class has been compiled with.
     * Use {@link Version} to compare, e.g.
     * {@code node.getApexVersion() >= Version.V176.getExternal()}
     *
     * @return the apex version
     */
    public double getApexVersion() {
        return getNode().getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }

    public ApexNode<? extends Compilation> getMainNode() {
        return (ApexNode<? extends Compilation>) getChild(0);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        this.beginLine = 1;
        this.beginColumn = 1;
        this.endLine = positioner.getLastLine();
        this.endColumn = positioner.getLastLineColumn();
    }

    @Override
    public @NonNull ASTApexFile getRoot() {
        return this;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Map<Integer, String> getNoPmdComments() {
        return suppressMap;
    }

    void setNoPmdComments(Map<Integer, String> suppressMap) {
        this.suppressMap = suppressMap;
    }
}
