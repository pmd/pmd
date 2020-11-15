/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

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

    @Override
    public void jjtClose() {
        super.jjtClose();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumChildren(); ++i) {
            AbstractModelicaNode child = (AbstractModelicaNode) getChild(i);
            if (child instanceof ASTWithinClause) {
                if (sb.length() > 0) {
                    sb.append(CompositeName.NAME_COMPONENT_SEPARATOR);
                }
                sb.append(child.getImage());
            }
        }
        setImage(sb.toString());
    }
}
