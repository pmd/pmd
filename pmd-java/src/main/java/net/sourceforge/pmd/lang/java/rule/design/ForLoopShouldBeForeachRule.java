/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;
import java.util.Map;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * @author Clément Fournier
 */
public class ForLoopShouldBeForeachRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTForStatement node, Object data) {

        final ASTForInit init = node.getFirstChildOfType(ASTForInit.class);
        final ASTForUpdate update = node.getFirstChildOfType(ASTForUpdate.class);
        final ASTExpression guardCondition = node.getFirstChildOfType(ASTExpression.class);

        if (init == null && update == null || guardCondition == null) { // The loop may be replaced with a while
            return super.visit(node, data);
        }

        VariableNameDeclaration index = getIndexVarDeclaration(init, update);
        if (index == null || !"int".equals(index.getTypeImage())) {
            return super.visit(node, data);
        }

        String itName = index.getName();
        String iterableName = getIterableNameOrNullToAbort(guardCondition, itName);


        if (!isForUpdateSimpleEnough(update, itName) || iterableName == null) {
            return super.visit(node, data);
        }


        List<NameOccurrence> occurrences = guardCondition.getScope().getDeclarations(VariableNameDeclaration.class).get(index);

        if (occurrences == null) {
            return super.visit(node, data);
        }

        VariableNameDeclaration iterableDeclaration = findDeclaration(iterableName, node.getScope());

        if (iterableDeclaration == null) {
            return super.visit(node, data);
        }

        if (iterableDeclaration.isArray() && loopOverArrayCanBeReplaced(node)) {
            addViolation(data, node);
        } else if ("List".equals(iterableDeclaration.getTypeImage())
            && loopOverListCanBeReplaced(node, occurrences, iterableDeclaration)) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }


    /* Finds the declaration of the index variable to find its name occurrences */
    private VariableNameDeclaration getIndexVarDeclaration(ASTForInit init, ASTForUpdate update) {
        if (init == null) {
            return guessIndexVarFromUpdate(update);
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> decls = init.getScope().getDeclarations(VariableNameDeclaration.class);
        VariableNameDeclaration theIterator = null;

        for (VariableNameDeclaration decl : decls.keySet()) {
            ASTForInit declInit = decl.getNode().getFirstParentOfType(ASTForInit.class);
            if (declInit == init) {
                theIterator = decl;
                break;
            }
        }

        return theIterator;

    }


    /** Does a best guess to find the index variable, gives up if the update has several statements */
    private VariableNameDeclaration guessIndexVarFromUpdate(ASTForUpdate update) {

        Node name;
        try {
            name = update.findChildNodesWithXPath(getSimpleForStatementXpath(null)).get(0);
        } catch (JaxenException je) {
            throw new RuntimeException(je);
        }

        if (name == null || name.getImage() == null) {
            return null;
        }

        return findDeclaration(name.getImage(), update.getScope().getParent());
    }


    /**
     * @return true if there's only one update statement of the form i++ or ++i.
     */
    private boolean isForUpdateSimpleEnough(ASTForUpdate update, String itName) {
        return update.hasDescendantMatchingXPath(getSimpleForStatementXpath(itName));
    }


    private String getSimpleForStatementXpath(String itName) {
        return "//StatementExpressionList[count(*)=1]"
            + "/StatementExpression"
            + "/*[self::PostfixExpression and @Image='++' or self::PreIncrementExpression]"
            + "/PrimaryExpression"
            + "/PrimaryPrefix"
            + "/Name"
            + (itName == null ? "" : ("[@Image='" + itName + "']"));
    }


    /**
     * Gets the name of the iterable array or list.
     *
     * @param itName The name of the iterator variable
     *
     * @return The name, or null if it couldn't be found or the guard condition is not safe to refactor (then abort)
     */
    private String getIterableNameOrNullToAbort(ASTExpression guardCondition, String itName) {


        if (guardCondition.jjtGetNumChildren() > 0
            && guardCondition.jjtGetChild(0) instanceof ASTRelationalExpression) {

            ASTRelationalExpression relationalExpression = (ASTRelationalExpression) guardCondition.jjtGetChild(0);

            if (relationalExpression.hasImageEqualTo("<") || relationalExpression.hasImageEqualTo("<=")) {

                try {
                    List<Node> left = guardCondition.findChildNodesWithXPath(
                        "//RelationalExpression/PrimaryExpression/PrimaryPrefix/Name[@Image='" + itName + "']");

                    List<Node> right = guardCondition.findChildNodesWithXPath(
                        "//RelationalExpression[@Image='<']/PrimaryExpression/PrimaryPrefix"
                            + "/Name[matches(@Image,'\\w+\\.(size|length)')]"
                            + "|"
                            + "//RelationalExpression[@Image='<=']/AdditiveExpression[count(*)=2 and "
                            + "@Image='-' and PrimaryExpression/PrimaryPrefix/Literal[@Image='1']]"
                            + "/PrimaryExpression/PrimaryPrefix/Name[matches(@Image,'\\w+\\.(size|length)')]");

                    if (left.isEmpty()) {
                        return null;
                    } else if (!right.isEmpty()) {
                        return right.get(0).getImage().split("\\.")[0];
                    } else {
                        return null;
                    }

                } catch (JaxenException je) {
                    throw new RuntimeException(je);
                }
            }
        }
        return null;
    }


    private boolean loopOverArrayCanBeReplaced(ASTForStatement node) {
        // TODO
        return false;
    }


    private boolean loopOverListCanBeReplaced(ASTForStatement stmt, List<NameOccurrence> occurrences,
                                              VariableNameDeclaration listDeclaration) {

        String listName = listDeclaration.getName();


        for (NameOccurrence occ : occurrences) {

            if (occ.getLocation().getFirstParentOfType(ASTForUpdate.class) == null
                && occ.getLocation().getFirstParentOfType(ASTExpression.class)
                != stmt.getFirstChildOfType(ASTExpression.class)
                && !occurenceIsListGet(occ, listName)) {
                return false;
            }
        }

        return true;
    }


    /** @return true if this occurence is as an argument to List.get on the correct list */
    private boolean occurenceIsListGet(NameOccurrence occ, String listName) {
        if (occ.getLocation() instanceof ASTName) {
            ASTPrimarySuffix suffix = occ.getLocation().getFirstParentOfType(ASTPrimarySuffix.class);

            if (suffix == null) {
                return false;
            }

            Node prefix = suffix.jjtGetParent().jjtGetChild(0);

            if (!(prefix instanceof ASTPrimaryPrefix) && prefix.jjtGetNumChildren() != 1
                && !(prefix.jjtGetChild(0) instanceof ASTName)) {
                return false;
            }

            String callImage = prefix.jjtGetChild(0).getImage();

            return (listName + ".get").equals(callImage);

        }
        return false;
    }


    private VariableNameDeclaration findDeclaration(String varName, Scope innermost) {
        Scope currentScope = innermost;

        while (currentScope != null) {
            for (VariableNameDeclaration decl : currentScope.getDeclarations(VariableNameDeclaration.class).keySet()) {
                if (decl.getImage().equals(varName)) {
                    return decl;
                }
            }
            currentScope = currentScope.getParent();
        }

        return null;
    }
}
