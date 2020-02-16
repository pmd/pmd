/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMemberSelector;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class UnnecessaryLocalBeforeReturnRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Boolean> STATEMENT_ORDER_MATTERS = booleanProperty("statementOrderMatters").defaultValue(true).desc("If set to false this rule no longer requires the variable declaration and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be reported.").build();


    public UnnecessaryLocalBeforeReturnRule() {
        definePropertyDescriptor(STATEMENT_ORDER_MATTERS);
    }

    @Override
    public Object visit(ASTMethodDeclaration meth, Object data) {
        // skip void/abstract/native method
        if (meth.isVoid() || meth.isAbstract() || meth.isNative()) {
            return data;
        }
        return super.visit(meth, data);
    }

    @Override
    public Object visit(ASTReturnStatement rtn, Object data) {
        // skip returns of literals
        ASTName name = rtn.getFirstDescendantOfType(ASTName.class);
        if (name == null) {
            return data;
        }

        // skip 'complicated' expressions
        if (rtn.findDescendantsOfType(ASTExpression.class).size() > 1
                || rtn.getFirstDescendantOfType(ASTMemberSelector.class) != null
                || rtn.findDescendantsOfType(ASTPrimaryExpression.class).size() > 1 || isMethodCall(rtn)) {
            return data;
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = name.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration variableDeclaration = entry.getKey();
            if (variableDeclaration.getDeclaratorId().isFormalParameter()) {
                continue;
            }

            List<NameOccurrence> usages = entry.getValue();

            if (usages.size() == 1) { // If there is more than 1 usage, then it's not only returned
                NameOccurrence occ = usages.get(0);

                if (occ.getLocation().equals(name) && isNotAnnotated(variableDeclaration)) {
                    String var = name.getImage();
                    if (var.indexOf('.') != -1) {
                        var = var.substring(0, var.indexOf('.'));
                    }

                    // Is the variable initialized with another member that is later used?
                    if (!isInitDataModifiedAfterInit(variableDeclaration, rtn)
                            && !statementsBeforeReturn(variableDeclaration, rtn)) {
                        addViolation(data, rtn, var);
                    }
                }
            }
        }
        return data;
    }

    private boolean statementsBeforeReturn(VariableNameDeclaration variableDeclaration, ASTReturnStatement returnStatement) {
        if (!getProperty(STATEMENT_ORDER_MATTERS)) {
            return false;
        }

        ASTBlockStatement declarationStatement = variableDeclaration.getAccessNodeParent().getFirstParentOfType(ASTBlockStatement.class);
        ASTBlockStatement returnBlockStatement = returnStatement.getFirstParentOfType(ASTBlockStatement.class);

        // double check: we should now be at the same level in the AST - both block statements are children of the same parent
        if (declarationStatement.getParent() == returnBlockStatement.getParent()) {
            return returnBlockStatement.getIndexInParent() - declarationStatement.getIndexInParent() > 1;
        }
        return false;
    }

    // TODO : should node define isAfter / isBefore helper methods for Nodes?
    private static boolean isAfter(Node n1, Node n2) {
        return n1.getBeginLine() > n2.getBeginLine()
                || n1.getBeginLine() == n2.getBeginLine() && n1.getBeginColumn() >= n2.getEndColumn();
    }

    private boolean isInitDataModifiedAfterInit(final VariableNameDeclaration variableDeclaration,
            final ASTReturnStatement rtn) {
        final ASTVariableInitializer initializer = variableDeclaration.getAccessNodeParent()
                .getFirstDescendantOfType(ASTVariableInitializer.class);

        if (initializer != null) {
            // Get the block statements for each, so we can compare apples to apples
            final ASTBlockStatement initializerStmt = variableDeclaration.getAccessNodeParent()
                    .getFirstParentOfType(ASTBlockStatement.class);
            final ASTBlockStatement rtnStmt = rtn.getFirstParentOfType(ASTBlockStatement.class);

            final List<ASTName> referencedNames = initializer.findDescendantsOfType(ASTName.class);
            for (final ASTName refName : referencedNames) {
                // TODO : Shouldn't the scope allow us to search for a var name occurrences directly, moving up through parent scopes?
                Scope scope = refName.getScope();
                do {
                    final Map<VariableNameDeclaration, List<NameOccurrence>> declarations = scope
                            .getDeclarations(VariableNameDeclaration.class);
                    for (final Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : declarations
                            .entrySet()) {
                        if (entry.getKey().getName().equals(refName.getImage())) {
                            // Variable found! Check usage locations
                            for (final NameOccurrence occ : entry.getValue()) {
                                final ASTBlockStatement location = occ.getLocation().getFirstParentOfType(ASTBlockStatement.class);

                                // Is it used after initializing our "unnecessary" local but before the return statement?
                                if (location != null && isAfter(location, initializerStmt) && isAfter(rtnStmt, location)) {
                                    return true;
                                }
                            }

                            return false;
                        }
                    }
                    scope = scope.getParent();
                } while (scope != null);
            }
        }

        return false;
    }

    private boolean isNotAnnotated(VariableNameDeclaration variableDeclaration) {
        AccessNode accessNodeParent = variableDeclaration.getAccessNodeParent();
        return !accessNodeParent.hasDescendantOfType(ASTAnnotation.class);
    }

    /**
     * Determine if the given return statement has any embedded method calls.
     *
     * @param rtn
     *            return statement to analyze
     * @return true if any method calls are made within the given return
     */
    private boolean isMethodCall(ASTReturnStatement rtn) {
        List<ASTPrimarySuffix> suffix = rtn.findDescendantsOfType(ASTPrimarySuffix.class);
        for (ASTPrimarySuffix element : suffix) {
            if (element.isArguments()) {
                return true;
            }
        }
        return false;
    }
}
