/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassSpecialization;

public class ASTClassDefinition extends AbstractModelicaNode {
    private ASTClassPrefixes prefixes;
    private ModelicaClassSpecialization specialization;
    private ModelicaClassSpecifierNode specifier;

    ASTClassDefinition(int id) {
        super(id);
    }

    public boolean isPartial() {
        return prefixes.firstChild(ASTPartialClause.class) != null;
    }

    public boolean isEncapsulated() {
        return firstChild(ASTEncapsulatedClause.class) != null;
    }

    public ModelicaClassSpecialization getSpecialization() {
        return specialization;
    }

    public ModelicaClassSpecifierNode getClassSpecifier() {
        return specifier;
    }

    private void checkSpecialization(Class<? extends ModelicaNode> clauseClass, ModelicaClassSpecialization restriction) {
        if (prefixes.firstChild(clauseClass) != null) {
            assert specialization == null;
            specialization = restriction;
        }
    }

    private void detectSpecialization() {
        checkSpecialization(ASTClassClause.class, ModelicaClassSpecialization.CLASS);
        checkSpecialization(ASTModelClause.class, ModelicaClassSpecialization.MODEL);
        checkSpecialization(ASTRecordClause.class, ModelicaClassSpecialization.RECORD);
        checkSpecialization(ASTOperatorRecordClause.class, ModelicaClassSpecialization.OPERATOR_RECORD);
        checkSpecialization(ASTBlockClause.class, ModelicaClassSpecialization.BLOCK);
        checkSpecialization(ASTConnectorClause.class, ModelicaClassSpecialization.CONNECTOR);
        checkSpecialization(ASTExpandableConnectorClause.class, ModelicaClassSpecialization.EXPANDABLE_CONNECTOR);
        checkSpecialization(ASTTypeClause.class, ModelicaClassSpecialization.TYPE);
        checkSpecialization(ASTPackageClause.class, ModelicaClassSpecialization.PACKAGE);
        checkSpecialization(ASTOperatorClause.class, ModelicaClassSpecialization.OPERATOR);
        ASTFunctionClause functionOrNull = prefixes.firstChild(ASTFunctionClause.class);
        if (functionOrNull != null) {
            boolean isPure = functionOrNull.firstChild(ASTPureClause.class) != null;
            boolean isOperator = functionOrNull.firstChild(ASTOperatorClause.class) != null;
            assert specialization == null;
            specialization = ModelicaClassSpecialization.getFunctionSpecialization(isPure, isOperator);
        }
        assert specialization != null;
    }

    @Override
    public void jjtClose() {
        super.jjtClose();
        prefixes = firstChild(ASTClassPrefixes.class);
        specifier = firstChild(ASTClassSpecifier.class).firstChild(ModelicaClassSpecifierNode.class);
        detectSpecialization();
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
