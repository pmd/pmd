/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.modelica.resolver.CompositeName;

/**
 * A representation of a Modelica source code file.
 */
public class ASTStoredDefinition extends AbstractModelicaNode implements RootNode {
    private boolean hasBOM = false;
    private AstInfo<ASTStoredDefinition> astInfo;

    ASTStoredDefinition(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void markHasBOM() {
        hasBOM = true;
    }

    @Override
    public AstInfo<ASTStoredDefinition> getAstInfo() {
        return astInfo;
    }

    ASTStoredDefinition makeTaskInfo(ParserTask task) {
        this.astInfo = new AstInfo<>(task, this);
        return this;
    }


    /**
     * Returns whether this source file contains Byte Order Mark.
     */
    public boolean getHasBOM() {
        return hasBOM;
    }

    public String getName() {
        return children(ASTWithinClause.class).toStream()
                .map(ASTWithinClause::getName)
                .collect(Collectors.joining(CompositeName.NAME_COMPONENT_SEPARATOR));
    }
}
