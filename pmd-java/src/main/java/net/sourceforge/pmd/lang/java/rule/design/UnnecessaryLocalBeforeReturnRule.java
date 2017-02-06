/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

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
        if (rtn.findDescendantsOfType(ASTExpression.class).size() > 1 || rtn.findDescendantsOfType(ASTPrimaryExpression.class).size() > 1 || isMethodCall(rtn)) {
            return data;
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = name.getScope().getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: vars.entrySet()) {
            VariableNameDeclaration key = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();

            // skip, if there is an assert between declaration and return
            if (hasAssertStatement(key, rtn)) {
                continue;
            }

            for (NameOccurrence occ: usages) {
                if (occ.getLocation().equals(name)) {
                    // only check declarations that occur one line earlier
                    if (key.getNode().getBeginLine() == name.getBeginLine() - 1) {
                        String var = name.getImage();
                        if (var.indexOf('.') != -1) {
                            var = var.substring(0, var.indexOf('.'));
                        }
                        addViolation(data, rtn, var);
                    }
                }
            }
        }
        return data;
    }

    /**
     * Determine if the given return statement has any embedded method calls.
     *
     * @param rtn
     *          return statement to analyze
     * @return true if any method calls are made within the given return
     */
    private boolean isMethodCall(ASTReturnStatement rtn) {
     List<ASTPrimarySuffix> suffix = rtn.findDescendantsOfType( ASTPrimarySuffix.class );
     for ( ASTPrimarySuffix element: suffix ) {
        if ( element.isArguments() ) {
          return true;
        }
      }
      return false;
    }

    /**
     * Checks whether there is an assert statement between the variable declaration
     * and the return statement, that uses the variable.
     * @param variableDeclaration
     * @param rtn
     * @return
     */
    private boolean hasAssertStatement(VariableNameDeclaration variableDeclaration, ASTReturnStatement rtn) {
        ASTBlockStatement blockStatement = variableDeclaration.getAccessNodeParent().getFirstParentOfType(ASTBlockStatement.class);
        int startIndex = blockStatement.jjtGetChildIndex() + 1;
        int endIndex = rtn.getFirstParentOfType(ASTBlockStatement.class).jjtGetChildIndex();
        Node block = blockStatement.jjtGetParent();
        for (int i = startIndex; i < endIndex; i++) {
            List<ASTAssertStatement> asserts = block.jjtGetChild(i).findDescendantsOfType(ASTAssertStatement.class);
            for (ASTAssertStatement assertStatement : asserts) {
                List<ASTName> names = assertStatement.findDescendantsOfType(ASTName.class);
                for (ASTName n : names) {
                    if (n.hasImageEqualTo(variableDeclaration.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
