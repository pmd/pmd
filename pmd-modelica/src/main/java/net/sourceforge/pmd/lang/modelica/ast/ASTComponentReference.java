/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.CompositeName;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionState;
import net.sourceforge.pmd.lang.modelica.resolver.ResolvableEntity;

public final class ASTComponentReference extends AbstractModelicaNode implements ResolvableModelicaNode {
    private String[] nameComponentsWithoutSubscripts;
    private boolean absolute;
    private ResolutionResult<ResolvableEntity> resolutionCandidates;

    ASTComponentReference(int id) {
        super(id);
    }

    ASTComponentReference(ModelicaParser p, int id) {
        super(p, id);
    }

    void markAbsolute() {
        absolute = true;
    }

    /**
     * Returns whether this reference is absolute (starts with a dot), such as
     * <code>y = .Modelica.Math.cos(x)</code>.
     */
    boolean isAbsolute() {
        return absolute;
    }

    /**
     * Returns a {@link CompositeName} object representing the lexical reference with subscripts being ignored, if any.
     */
    public CompositeName getCompositeNameWithoutSubscripts() {
        return CompositeName.create(absolute, nameComponentsWithoutSubscripts);
    }

    /**
     * Returns resolution candidates for the referred component (and <b>not</b> dereferencing its type, etc.).
     *
     * We do not decide on entity type on behalf of the rule code, since this may introduce false negatives.
     */
    @Override
    public ResolutionResult<ResolvableEntity> getResolutionCandidates() {
        if (resolutionCandidates == null) {
            resolutionCandidates = getMostSpecificScope().safeResolveLexically(ResolvableEntity.class, ResolutionState.forComponentReference(), getCompositeNameWithoutSubscripts());
        }
        return resolutionCandidates;
    }

    // For Rule Designer
    public String getResolvedTo() {
        return Helper.getResolvedTo(getResolutionCandidates());
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void jjtClose() {
        super.jjtClose();

        nameComponentsWithoutSubscripts = new String[getNumChildren()];
        for (int i = 0; i < nameComponentsWithoutSubscripts.length; ++i) {
            String name = getChild(i).getFirstChildOfType(ASTSimpleName.class).getImage();
            nameComponentsWithoutSubscripts[i] = name;
        }
    }
}
