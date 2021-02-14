/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.Compilation;

public final class ASTApexFile extends AbstractApexNode<AstNode> implements RootNode {

    private final AstInfo<ASTApexFile> astInfo;

    ASTApexFile(SourceCodePositioner source,
                ParserTask task,
                AbstractApexNode<? extends Compilation> child,
                Map<Integer, String> suppressMap) {
        super(child.getNode());
        this.astInfo = new AstInfo<>(task, this, suppressMap);
        addChild(child, 0);
        this.beginLine = 1;
        this.endLine = source.getLastLine();
        this.beginColumn = 1;
        this.endColumn = source.getLastLineColumn();
        child.setCoords(child.getBeginLine(), child.getBeginColumn(), source.getLastLine(), source.getLastLineColumn());
    }

    @Override
    public AstInfo<ASTApexFile> getAstInfo() {
        return astInfo;
    }

    @Override
    public double getApexVersion() {
        return getNode().getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }

    public ApexNode<Compilation> getMainNode() {
        return (ApexNode<Compilation>) getChild(0);
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
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
