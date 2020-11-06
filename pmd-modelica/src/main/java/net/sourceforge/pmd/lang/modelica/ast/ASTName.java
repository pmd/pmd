/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.CompositeName;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionState;
import net.sourceforge.pmd.lang.modelica.resolver.ResolvableEntity;

public final class ASTName extends AbstractModelicaNode implements ResolvableModelicaNode {
    private String[] nameComponents;
    private ResolutionResult<ResolvableEntity> resolutionCandidates;
    private boolean absolute = false;

    ASTName(int id) {
        super(id);
    }

    ASTName(ModelicaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void jjtClose() {
        super.jjtClose();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumChildren(); ++i) {
            if (i != 0 || absolute) {
                sb.append('.');
            }
            sb.append(((ASTSimpleName) getChild(i)).getImage());
        }
        setImage(sb.toString());

        nameComponents = new String[getNumChildren()];
        for (int i = 0; i < getNumChildren(); ++i) {
            nameComponents[i] = getChild(i).getImage();
        }
    }

    void markAbsolute() {
        absolute = true;
    }

    /**
     * Returns whether this reference is absolute (starts with a dot), such as
     * <code>.Modelica.Blocks.Continuous.Filter</code>.
     */
    public boolean isAbsolute() {
        return absolute;
    }

    /**
     * Returns a {@link CompositeName} object representing a lexical reference contained in this node.
     */
    public CompositeName getCompositeName() {
        return CompositeName.create(absolute, nameComponents);
    }

    /**
     * Returns resolution candidates for the referred entity.
     *
     * We do not decide on entity type on behalf of the rule code, since this may introduce false negatives.
     */
    @Override
    public ResolutionResult<ResolvableEntity> getResolutionCandidates() {
        if (resolutionCandidates == null) {
            resolutionCandidates = getMostSpecificScope().safeResolveLexically(ResolvableEntity.class, ResolutionState.forType(), getCompositeName());
        }
        return resolutionCandidates;
    }

    // For Rule Designer
    public String getResolvedTo() {
        return Helper.getResolvedTo(getResolutionCandidates());
    }
}
