/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

import scala.meta.Source;

/**
 * The ASTSource node implementation.
 */
public final class ASTSource extends AbstractScalaNode<Source> implements RootNode {

    private AstInfo<ASTSource> astInfo;

    ASTSource(Source scalaNode) {
        super(scalaNode);
    }


    @Override
    public AstInfo<ASTSource> getAstInfo() {
        return astInfo;
    }

    void addTaskInfo(ParserTask task) {
        this.astInfo = new AstInfo<>(task, this);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
