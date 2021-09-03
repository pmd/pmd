/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import net.sourceforge.pmd.lang.modelica.ast.ASTComponentClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTComponentDeclaration;
import net.sourceforge.pmd.lang.modelica.ast.ASTConditionAttribute;
import net.sourceforge.pmd.lang.modelica.ast.ASTConstantClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTDeclaration;
import net.sourceforge.pmd.lang.modelica.ast.ASTDiscreteClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTFlowClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTInputClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTName;
import net.sourceforge.pmd.lang.modelica.ast.ASTOutputClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTParameterClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTSimpleName;
import net.sourceforge.pmd.lang.modelica.ast.ASTStreamClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTTypePrefix;
import net.sourceforge.pmd.lang.modelica.ast.ASTTypeSpecifier;

public class ModelicaComponentDeclaration extends AbstractModelicaDeclaration implements ModelicaDeclaration {
    public enum ComponentKind {
        FLOW("flow"),
        STREAM("stream"),
        NOTHING_SPECIAL("");

        private String name;

        ComponentKind(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ComponentVariability {
        DISCRETE("discrete"),
        PARAMETER("parameter"),
        CONSTANT("constant"),
        CONTINUOUS("continuous");

        private String name;

        ComponentVariability(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ComponentCausality {
        INPUT("input"),
        OUTPUT("output"),
        ACAUSAL("acausal");

        private String name;

        ComponentCausality(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private ModelicaClassScope containingScope;
    private ComponentKind kind;
    private ComponentVariability variability;
    private ComponentCausality causality;
    private final ASTName typeName;
    private ResolutionResult<ModelicaType> typeCandidates;
    private final String declarationName;
    private final ASTConditionAttribute condition;

    public ModelicaComponentDeclaration(ASTComponentDeclaration node) {
        declarationName = node.getFirstChildOfType(ASTDeclaration.class).getFirstChildOfType(ASTSimpleName.class).getImage();
        condition = node.getFirstChildOfType(ASTConditionAttribute.class);
        ASTComponentClause declarationRoot = node.getFirstParentOfType(ASTComponentClause.class);
        ASTTypePrefix prefixes = declarationRoot.getFirstChildOfType(ASTTypePrefix.class);
        parseTypePrefix(prefixes);
        typeName = declarationRoot
                .getFirstChildOfType(ASTTypeSpecifier.class)
                .getFirstChildOfType(ASTName.class);
    }

    void setContainingScope(ModelicaClassScope scope) {
        containingScope = scope;
    }

    @Override
    public ModelicaClassScope getContainingScope() {
        return containingScope;
    }

    private void parseTypePrefix(ASTTypePrefix prefix) {
        if (prefix.getFirstChildOfType(ASTFlowClause.class) != null) {
            kind = ComponentKind.FLOW;
        } else if (prefix.getFirstChildOfType(ASTStreamClause.class) != null) {
            kind = ComponentKind.STREAM;
        } else {
            kind = ComponentKind.NOTHING_SPECIAL;
        }

        if (prefix.getFirstChildOfType(ASTDiscreteClause.class) != null) {
            variability = ComponentVariability.DISCRETE;
        } else if (prefix.getFirstChildOfType(ASTParameterClause.class) != null) {
            variability = ComponentVariability.PARAMETER;
        } else if (prefix.getFirstChildOfType(ASTConstantClause.class) != null) {
            variability = ComponentVariability.CONSTANT;
        } else {
            variability = ComponentVariability.CONTINUOUS;
        }

        if (prefix.getFirstChildOfType(ASTInputClause.class) != null) {
            causality = ComponentCausality.INPUT;
        } else if (prefix.getFirstChildOfType(ASTOutputClause.class) != null) {
            causality = ComponentCausality.OUTPUT;
        } else {
            causality = ComponentCausality.ACAUSAL;
        }
    }

    public ASTConditionAttribute getCondition() {
        return condition;
    }

    /**
     * Whether this component is declared as <code>flow</code>, <code>stream</code> or nothing special.
     */
    public ComponentKind getKind() {
        return kind;
    }

    /**
     * Whether this component is a constant, a parameter, a discrete or a continuous variable.
     */
    public ComponentVariability getVariability() {
        return variability;
    }

    /**
     * Whether this component is input, output or acausal.
     */
    public ComponentCausality getCausality() {
        return causality;
    }

    @Override
    public String getSimpleDeclarationName() {
        return declarationName;
    }

    @Override
    public String getDescriptiveName() {
        return declarationName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (kind != null) {
            sb.append(kind.toString());
            sb.append(' ');
        }
        if (variability != null) {
            sb.append(variability.toString());
            sb.append(' ');
        }
        if (causality != null) {
            sb.append(causality.toString());
            sb.append(' ');
        }
        sb.append(typeName);
        sb.append(' ');
        sb.append(declarationName);
        return sb.toString();
    }

    public ResolutionResult<ModelicaType> getTypeCandidates() {
        if (typeCandidates == null) {
            ResolutionContext ctx = ResolutionState.forComponentReference().createContext();
            try {
                getContainingScope().resolveLexically(ctx, typeName.getCompositeName());
            } catch (Watchdog.CountdownException e) {
                ctx.markTtlExceeded();
            }
            typeCandidates = ctx.getTypes();
        }
        return typeCandidates;
    }

    @Override
    void resolveFurtherNameComponents(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        if (name.isEmpty()) {
            result.addCandidate(this);
            return;
        }

        ResolutionResult<ModelicaType> resolvedType = getTypeCandidates();
        for (ModelicaType decl: resolvedType.getBestCandidates()) {
            ((AbstractModelicaDeclaration) decl).resolveFurtherNameComponents(result, name);
        }
        result.markHidingPoint();
        for (ModelicaType decl: resolvedType.getHiddenCandidates()) {
            ((AbstractModelicaDeclaration) decl).resolveFurtherNameComponents(result, name);
        }
    }
}
