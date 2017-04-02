/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
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
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public class UnnecessaryLocalBeforeReturnRule extends AbstractJavaRule {

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
                || rtn.findDescendantsOfType(ASTPrimaryExpression.class).size() > 1 || isMethodCall(rtn)) {
            return data;
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = name.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration variableDeclaration = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();

            if (usages.size() == 1) { // If there is more than 1 usage, then it's not only returned
                NameOccurrence occ = usages.get(0);

                if (occ.getLocation().equals(name) && isNotAnnotated(variableDeclaration)) {
                    String var = name.getImage();
                    if (var.indexOf('.') != -1) {
                        var = var.substring(0, var.indexOf('.'));
                    }
                    // Is the variable initialized with another member that is later used?
                    if (!isInitDataModifiedAfterInit(variableDeclaration, rtn)) {
                        addViolation(data, rtn, var);
                    }
                }
            }
        }
        return data;
    }

    private boolean isInitDataModifiedAfterInit(final VariableNameDeclaration variableDeclaration,
            final ASTReturnStatement rtn) {
        final ASTVariableInitializer initializer = variableDeclaration.getAccessNodeParent()
                .getFirstDescendantOfType(ASTVariableInitializer.class);
        if (initializer != null) {
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
                                final ScopedNode location = occ.getLocation();
                                // Is it used after initializing our "unnecessary" local but before the return statement?
                                // TODO : should node define isAfter / isBefore helper methods?
                                if ((location.getBeginLine() > initializer.getEndLine()
                                        || (location.getBeginLine() == initializer.getEndLine() && location.getBeginColumn() >= initializer.getEndColumn()))
                                        && (location.getEndLine() < rtn.getBeginLine()
                                                || (location.getEndLine() == rtn.getBeginLine()
                                                        && location.getEndColumn() <= rtn.getEndColumn()))) {
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
