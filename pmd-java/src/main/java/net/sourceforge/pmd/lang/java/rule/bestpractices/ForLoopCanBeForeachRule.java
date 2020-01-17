/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ForLoopCanBeForeachRule extends AbstractJavaRule {

    public ForLoopCanBeForeachRule() {
        addRuleChainVisit(ASTForStatement.class);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {

        final ASTForInit init = node.getFirstChildOfType(ASTForInit.class);
        final ASTForUpdate update = node.getFirstChildOfType(ASTForUpdate.class);
        final ASTExpression guardCondition = node.getFirstChildOfType(ASTExpression.class);

        if (init == null && update == null || guardCondition == null) {
            return data;
        }

        Entry<VariableNameDeclaration, List<NameOccurrence>> indexDecl = getIndexVarDeclaration(init, update);

        if (indexDecl == null) {
            return data;
        }


        List<NameOccurrence> occurrences = indexDecl.getValue();
        VariableNameDeclaration index = indexDecl.getKey();

        if (TypeHelper.isExactlyAny(index, Iterator.class)) {
            Entry<VariableNameDeclaration, List<NameOccurrence>> iterableInfo = getIterableDeclOfIteratorLoop(index, node.getScope());

            if (iterableInfo != null && isReplaceableIteratorLoop(indexDecl, guardCondition, iterableInfo, node)) {
                addViolation(data, node);
            }
            return data;
        }


        if (occurrences == null || !"int".equals(index.getTypeImage()) || !indexStartsAtZero(index)) {
            return data;
        }


        String itName = index.getName();
        String iterableName = getIterableNameOrNullToAbort(guardCondition, itName);


        if (!isForUpdateSimpleEnough(update, itName) || iterableName == null) {
            return data;
        }

        Entry<VariableNameDeclaration, List<NameOccurrence>> iterableInfo = findDeclaration(iterableName, node.getScope());
        VariableNameDeclaration iterableDeclaration = iterableInfo == null ? null : iterableInfo.getKey();

        if (iterableDeclaration == null) {
            return data;
        }

        if (iterableDeclaration.isArray() && isReplaceableArrayLoop(node, occurrences, iterableDeclaration)) {
            addViolation(data, node);
        } else if (iterableDeclaration.getTypeImage() != null && iterableDeclaration.getTypeImage()
                                                                                    .matches("List|ArrayList|LinkedList")
            && isReplaceableListLoop(node, occurrences, iterableDeclaration)) {
            addViolation(data, node);
        }

        return data;
    }


    /* Finds the declaration of the index variable and its occurrences, null to abort */
    private Entry<VariableNameDeclaration, List<NameOccurrence>> getIndexVarDeclaration(ASTForInit init, ASTForUpdate update) {
        if (init == null) {
            return guessIndexVarFromUpdate(update);
        }

        ASTLocalVariableDeclaration decl = init.getFirstChildOfType(ASTLocalVariableDeclaration.class);
        if (decl == null) {
            return null;
        }

        int numDeclaredVars = decl.findChildrenOfType(ASTVariableDeclarator.class).size();
        if (numDeclaredVars > 1) {
            return null; // will abort in the calling function
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> decls = init.getScope().getDeclarations(VariableNameDeclaration.class);
        Entry<VariableNameDeclaration, List<NameOccurrence>> indexVarAndOccurrences = null;

        for (Entry<VariableNameDeclaration, List<NameOccurrence>> e : decls.entrySet()) {

            ASTForInit declInit = e.getKey().getNode().getFirstParentOfType(ASTForInit.class);
            if (Objects.equals(declInit, init)) {
                indexVarAndOccurrences = e;
                break;
            }
        }

        return indexVarAndOccurrences;

    }


    /** Does a best guess to find the index variable, gives up if the update has several statements */
    private Entry<VariableNameDeclaration, List<NameOccurrence>> guessIndexVarFromUpdate(ASTForUpdate update) {

        Node name = null;
        try {
            List<Node> match = update.findChildNodesWithXPath(getSimpleForUpdateXpath(null));
            if (!match.isEmpty()) {
                name = match.get(0);
            }
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
        return update != null && update.hasDescendantMatchingXPath(getSimpleForUpdateXpath(itName));
    }


    private String getSimpleForUpdateXpath(String itName) {
        return "./StatementExpressionList[count(*)=1]"
            + "/StatementExpression"
            + "/*[self::PostfixExpression and @Image='++' or self::PreIncrementExpression]"
            + "/PrimaryExpression"
            + "/PrimaryPrefix"
            + "/Name"
            + (itName == null ? "" : "[@Image='" + itName + "']");
    }


    /* We only report loops with int initializers starting at zero. */
    private boolean indexStartsAtZero(VariableNameDeclaration index) {
        ASTVariableDeclaratorId name = (ASTVariableDeclaratorId) index.getNode();
        ASTVariableDeclarator declarator = name.getFirstParentOfType(ASTVariableDeclarator.class);

        if (declarator == null) {
            return false;
        }

        try {
            List<Node> zeroLiteral = declarator.findChildNodesWithXPath(
                "./VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix/Literal[@Image='0' and "
                    + "@StringLiteral='false']");
            if (!zeroLiteral.isEmpty()) {
                return true;
            }
        } catch (JaxenException je) {
            throw new RuntimeException(je);
        }

        return false;

    }


    /**
     * Gets the name of the iterable array or list.
     *
     * @param itName The name of the iterator variable
     *
     * @return The name, or null if it couldn't be found or the guard condition is not safe to refactor (then abort)
     */
    private String getIterableNameOrNullToAbort(ASTExpression guardCondition, String itName) {


        if (guardCondition.getNumChildren() > 0
            && guardCondition.getChild(0) instanceof ASTRelationalExpression) {

            ASTRelationalExpression relationalExpression = (ASTRelationalExpression) guardCondition.getChild(0);

            if (relationalExpression.hasImageEqualTo("<") || relationalExpression.hasImageEqualTo("<=")) {

                try {
                    List<Node> left = guardCondition.findChildNodesWithXPath(
                        "./RelationalExpression/PrimaryExpression/PrimaryPrefix/Name[@Image='" + itName + "']");

                    List<Node> right = guardCondition.findChildNodesWithXPath(
                        "./RelationalExpression[@Image='<']/PrimaryExpression/PrimaryPrefix"
                            + "/Name[matches(@Image,'\\w+\\.(size|length)')]"
                            + "|"
                            + "./RelationalExpression[@Image='<=']/AdditiveExpression[count(*)=2 and "
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


    private Entry<VariableNameDeclaration, List<NameOccurrence>> getIterableDeclOfIteratorLoop(VariableNameDeclaration indexDecl, Scope scope) {
        Node initializer = indexDecl.getNode().getFirstParentOfType(ASTVariableDeclarator.class)
                                    .getFirstChildOfType(ASTVariableInitializer.class);

        if (initializer == null) {
            return null;
        }

        ASTName nameNode = initializer.getFirstDescendantOfType(ASTName.class);
        if (nameNode == null) {
            // TODO : This can happen if we are calling a local / statically imported method that returns the iterable - currently unhandled
            return null;
        }

        String name = nameNode.getImage();
        int dotIndex = name.indexOf('.');

        if (dotIndex > 0) {
            name = name.substring(0, dotIndex);
        }

        return findDeclaration(name, scope);
    }


    private boolean isReplaceableArrayLoop(ASTForStatement stmt, List<NameOccurrence> occurrences,
                                           VariableNameDeclaration arrayDeclaration) {
        String arrayName = arrayDeclaration.getName();


        for (NameOccurrence occ : occurrences) {

            if (occ.getLocation().getFirstParentOfType(ASTForUpdate.class) == null
                && occ.getLocation().getFirstParentOfType(ASTExpression.class)
                != stmt.getFirstChildOfType(ASTExpression.class)
                && !occurenceIsArrayAccess(occ, arrayName)) {
                return false;
            }
        }
        return true;
    }


    private boolean occurenceIsArrayAccess(NameOccurrence occ, String arrayName) {
        if (occ.getLocation() instanceof ASTName) {
            ASTPrimarySuffix suffix = occ.getLocation().getFirstParentOfType(ASTPrimarySuffix.class);

            if (suffix == null || !suffix.isArrayDereference()) {
                return false;
            }

            return suffix.hasDescendantMatchingXPath("./Expression/PrimaryExpression[count(*)"
                                                         + "=1]/PrimaryPrefix/Name[@Image='" + occ.getImage() + "']")
                && suffix.hasDescendantMatchingXPath("../PrimaryPrefix/Name[@Image='" + arrayName + "']")
                && !suffix.hasDescendantMatchingXPath("../../AssignmentOperator");
        }
        return false;
    }


    private boolean isReplaceableListLoop(ASTForStatement stmt, List<NameOccurrence> occurrences,
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

            Node prefix = suffix.getParent().getChild(0);

            if (!(prefix instanceof ASTPrimaryPrefix) || prefix.getNumChildren() != 1
                || !(prefix.getChild(0) instanceof ASTName)) {
                // it's either not a primary prefix, doesn't have children (can happen with this./super.)
                // or first child is not a name
                return false;
            }

            String callImage = prefix.getChild(0).getImage();

            return (listName + ".get").equals(callImage);

        }
        return false;
    }


    private Entry<VariableNameDeclaration, List<NameOccurrence>> findDeclaration(String varName, Scope innermost) {
        Scope currentScope = innermost;

        while (currentScope != null) {
            for (Entry<VariableNameDeclaration, List<NameOccurrence>> e : currentScope.getDeclarations(VariableNameDeclaration.class).entrySet()) {
                if (e.getKey().getName().equals(varName)) {
                    return e;
                }
            }
            currentScope = currentScope.getParent();
        }

        return null;
    }


    private boolean isReplaceableIteratorLoop(Entry<VariableNameDeclaration, List<NameOccurrence>> indexInfo,
                                              ASTExpression guardCondition,
                                              Entry<VariableNameDeclaration, List<NameOccurrence>> iterableInfo,
                                              ASTForStatement stmt) {

        if (isIterableModifiedInsideLoop(iterableInfo, stmt)) {
            return false;
        }


        String indexName = indexInfo.getKey().getName();

        if (indexName == null) {
            return false;
        }

        if (!guardCondition.hasDescendantMatchingXPath(
            "./PrimaryExpression/PrimaryPrefix/Name[@Image='" + indexName + ".hasNext']")) {
            return false;
        }

        List<NameOccurrence> occurrences = indexInfo.getValue();

        if (occurrences.size() > 2) {
            return false;
        }

        for (NameOccurrence occ : indexInfo.getValue()) {
            ScopedNode location = occ.getLocation();
            boolean isCallingNext = location instanceof ASTName
                    && (location.hasImageEqualTo(indexName + ".hasNext")
                            || location.hasImageEqualTo(indexName + ".next"));

            if (!isCallingNext) {
                return false;
            }
        }
        return true;
    }

    private boolean isIterableModifiedInsideLoop(Entry<VariableNameDeclaration, List<NameOccurrence>> iterableInfo,
                                                 ASTForStatement stmt) {

        String iterableName = iterableInfo.getKey().getName();
        for (NameOccurrence occ : iterableInfo.getValue()) {
            ASTForStatement forParent = occ.getLocation().getFirstParentOfType(ASTForStatement.class);
            if (Objects.equals(forParent, stmt)) {
                String image = occ.getLocation().getImage();
                if (image.startsWith(iterableName + ".remove")) {
                    return true;
                }
            }
        }

        return false;
    }


}
