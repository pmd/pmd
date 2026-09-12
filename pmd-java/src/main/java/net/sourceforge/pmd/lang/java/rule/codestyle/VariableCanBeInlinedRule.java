/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.DEPRECATED_RULE_PROPERTY_MARKER;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * @since 7.17.0
 */
public class VariableCanBeInlinedRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> STATEMENT_ORDER_MATTERS = booleanProperty("statementOrderMatters")
            .defaultValue(true)
            .desc(DEPRECATED_RULE_PROPERTY_MARKER + "Setting this to false relaxes the rule to report any variable "
                    + "that is used solely in a return/throw statement, under the assumption that the statements "
                    + "between the declaration and the return have no side effects. That assumption is unsafe and "
                    + "following the suggestion could introduce an application bug (#3124). The property will be "
                    + "removed; statement order will then always be considered.")
            .build();

    public VariableCanBeInlinedRule() {
        super(ASTReturnStatement.class, ASTThrowStatement.class, ASTYieldStatement.class);
        definePropertyDescriptor(STATEMENT_ORDER_MATTERS);
    }

    @Override
    public Object visit(ASTReturnStatement statement, Object data) {
        checkUnnecessaryLocal(statement, statement.getExpr(), data);
        return null;
    }

    @Override
    public Object visit(ASTYieldStatement statement, Object data) {
        checkUnnecessaryLocal(statement, statement.getExpr(), data);
        return null;
    }

    @Override
    public Object visit(ASTThrowStatement statement, Object data) {
        checkUnnecessaryLocal(statement, statement.getExpr(), data);
        return null;
    }

    private void checkUnnecessaryLocal(JavaNode statement, ASTExpression expr, Object data) {
        if (!(expr instanceof ASTVariableAccess)) {
            return;
        }

        JVariableSymbol sym = ((ASTVariableAccess) expr).getReferencedSym();
        if (sym == null) {
            return;
        }

        ASTVariableId varDecl = sym.tryGetNode();
        if (varDecl == null || !varDecl.isLocalVariable() || varDecl.getDeclaredAnnotations().nonEmpty()) {
            return;
        }

        if (varDecl.getLocalUsages().size() != 1) {
            return;
        }

        if (!getProperty(STATEMENT_ORDER_MATTERS)
                || varDecl.ancestors(ASTLocalVariableDeclaration.class).firstOrThrow().getNextSibling() == statement) {
            asCtx(data).addViolation(varDecl, varDecl.getName());
        }
    }
}
