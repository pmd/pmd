/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.ast.NodeStream.forkJoin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpressionList;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

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

        Optional<Entry<VariableNameDeclaration, List<NameOccurrence>>> indexDecl = getIndexVarDeclaration(init, update);

        if (!indexDecl.isPresent()) {
            return data;
        }

        final List<NameOccurrence> occurrences = indexDecl.get().getValue();
        final VariableNameDeclaration index = indexDecl.get().getKey();

        if (TypeTestUtil.isA(Iterator.class, index.getDeclaratorId())) {
            getIterableDeclOfIteratorLoop(index, node.getScope())
                .filter(iterableInfo -> isReplaceableIteratorLoop(indexDecl.get(), guardCondition, iterableInfo, node))
                .ifPresent(ignored -> addViolation(data, node));

            return data;
        }

        if (occurrences == null
            || !"int".equals(index.getTypeImage())
            || !indexStartsAtZero(index)
            || !isForUpdateSimpleEnough(update)) {

            return data;
        }

        findIterableName(guardCondition, index.getName())
            .flatMap(iterableName -> findDeclaration(iterableName, node.getScope()))
            .map(Entry::getKey)
            .filter(iterableDecl ->
                        isReplaceableArrayLoop(node, occurrences, iterableDecl)
                            || isReplaceableListLoop(node, occurrences, iterableDecl))
            .ifPresent(decl -> addViolation(data, node));

        return data;
    }


    /* Finds the declaration of the index variable and its occurrences, null to abort */
    private Optional<Entry<VariableNameDeclaration, List<NameOccurrence>>> getIndexVarDeclaration(ASTForInit init, ASTForUpdate update) {
        if (init == null) {
            return guessIndexVarFromUpdate(update);
        }

        return init.children(ASTLocalVariableDeclaration.class)
                   .filter(it -> it.children(ASTVariableDeclarator.class).count() == 1)
                   .firstOpt()
                   .flatMap(decl -> getInfoAboutForIndexVar(init));
    }



    /** Does a best guess to find the index variable, gives up if the update has several statements */
    private Optional<Entry<VariableNameDeclaration, List<NameOccurrence>>> guessIndexVarFromUpdate(ASTForUpdate update) {
        return simpleForUpdateVarName(update).flatMap(name -> findDeclaration(name, update.getScope().getParent()));
    }


    private boolean isForUpdateSimpleEnough(ASTForUpdate update) {
        return simpleForUpdateVarName(update).isPresent();
    }


    /**
     * @return the variable name if there's only one update statement of the form i++ or ++i.
     */
    private Optional<String> simpleForUpdateVarName(ASTForUpdate base) {
        return NodeStream.of(base)
                         .children(ASTStatementExpressionList.class)
                         .filter(it -> it.getNumChildren() == 1)
                         .children(ASTStatementExpression.class)
                         .children(ASTUnaryExpression.class)
                         .filter(it -> it.getOperator().isIncrement())
                         .map(ASTUnaryExpression::getOperand)
                         .filterIs(ASTVariableAccess.class)
                         .firstOpt()
                         .map(Node::getImage);
    }


    /* We only report loops with int initializers starting at zero. */
    private boolean indexStartsAtZero(VariableNameDeclaration index) {

        return NodeStream.of(index.getNode())
                         .parents()
                         .filterIs(ASTVariableDeclarator.class)
                         .children(ASTVariableInitializer.class)
                         .children(ASTExpression.class)
                         .children(ASTPrimaryExpression.class)
                         .children(ASTPrimaryPrefix.class)
                         .children(ASTLiteral.class)
                         .filterMatching(Node::getImage, "0")
                         .filterNot(ASTLiteral::isStringLiteral)
                         .nonEmpty();
    }


    private static Optional<Entry<VariableNameDeclaration, List<NameOccurrence>>> getInfoAboutForIndexVar(ASTForInit init) {
        Map<VariableNameDeclaration, List<NameOccurrence>> decls = init.getScope().getDeclarations(VariableNameDeclaration.class);

        return decls.entrySet().stream()
                    .filter(e -> e.getKey().getNode()
                                  .ancestors(ASTForInit.class)
                                  .filterMatching(n -> n, init)
                                  .nonEmpty())
                    .findFirst();
    }


    /**
     * Gets the name of the iterable array or list.
     *
     * @param itName The name of the iterator variable
     *
     * @return The name, or null if it couldn't be found or the guard condition is not safe to refactor (then abort)
     */
    @SuppressWarnings("unchecked")
    private Optional<String> findIterableName(ASTExpression guardCondition, String itName) {


        if (guardCondition.getNumChildren() > 0
            && guardCondition.getChild(0) instanceof ASTRelationalExpression) {

            ASTRelationalExpression relationalExpression = (ASTRelationalExpression) guardCondition.getChild(0);

            if (relationalExpression.hasImageEqualTo("<") || relationalExpression.hasImageEqualTo("<=")) {

                boolean leftIsIndexVarName =
                    guardCondition.children(ASTRelationalExpression.class)
                                  .children(ASTPrimaryExpression.class)
                                  .children(ASTPrimaryPrefix.class)
                                  .children(ASTName.class)
                                  .filterMatching(Node::getImage, itName)
                                  .nonEmpty();

                if (!leftIsIndexVarName) {
                    return Optional.empty();
                }

                return forkJoin(
                    guardCondition.children(ASTRelationalExpression.class),
                    rel -> NodeStream.of(rel).filterMatching(Node::getImage, "<"),
                    rel -> NodeStream.of(rel)
                    // TODO fix
                    // .filterMatching(Node::getImage, "<=")
                    // .children(ASTAdditiveExpression.class)
                    // .filter(expr ->
                    //             expr.getNumChildren() == 2
                    //                 && "-".equals(expr.getOperator())
                    //                 && expr.children(ASTPrimaryExpression.class)
                    //                        .children(ASTPrimaryPrefix.class)
                    //                        .children(ASTLiteral.class)
                    //                        .filterMatching(Node::getImage, "1")
                    //                        .nonEmpty()
                    // )
                    )
                    .children(ASTPrimaryExpression.class)
                    .children(ASTPrimaryPrefix.class)
                    .children(ASTName.class)
                    .filter(n -> n.getImage().matches("\\w+\\.(size|length)"))
                    .firstOpt()
                    .map(astName -> astName.getImage().split("\\.")[0]);

            }
        }
        return Optional.empty();
    }


    private Optional<Entry<VariableNameDeclaration, List<NameOccurrence>>> getIterableDeclOfIteratorLoop(VariableNameDeclaration indexDecl, Scope scope) {

        return NodeStream.of(indexDecl.getNode())
                         .followingSiblings()
                         .filterIs(ASTVariableInitializer.class)
                         .descendants(ASTName.class)
                         .firstOpt()             // TODO : This can return null if we are calling a local / statically imported method that returns the iterable - currently unhandled
                         .flatMap(nameNode -> {

                             String name = nameNode.getImage();
                             int dotIndex = name.indexOf('.');

                             if (dotIndex > 0) {
                                 name = name.substring(0, dotIndex);
                             }

                             return findDeclaration(name, scope);
                         });
    }


    private boolean isReplaceableArrayLoop(ASTForStatement stmt, List<NameOccurrence> occurrences,
                                           VariableNameDeclaration arrayDeclaration) {

        if (!arrayDeclaration.isArray()) {
            return false;
        }

        String arrayName = arrayDeclaration.getName();


        for (NameOccurrence occ : occurrences) {

            if (occ.getLocation().getFirstParentOfType(ASTForUpdate.class) == null
                && occ.getLocation().getFirstParentOfType(ASTExpression.class)
                != stmt.getFirstChildOfType(ASTExpression.class)
                && !occurrenceIsArrayAccess(occ, arrayName)) {
                return false;
            }
        }
        return true;
    }


    private boolean occurrenceIsArrayAccess(NameOccurrence occ, String arrayName) {
        if (occ.getLocation() instanceof ASTName) {
            ASTPrimarySuffix suffix = occ.getLocation().getFirstParentOfType(ASTPrimarySuffix.class);

            if (suffix == null || !suffix.isArrayDereference()) {
                return false;
            }

            return suffix.descendants(ASTExpression.class)
                         .children(ASTPrimaryExpression.class)
                         .filter(it -> it.getNumChildren() == 1)
                         .children(ASTPrimaryPrefix.class)
                         .children(ASTName.class)
                         .filterMatching(Node::getImage, occ.getImage())
                         .nonEmpty()
                && suffix.asStream()
                         .precedingSiblings()
                         .filterIs(ASTPrimaryPrefix.class)
                         .children(ASTName.class)
                         .filterMatching(Node::getImage, arrayName)
                         .nonEmpty()

                && suffix.getParent()
                         .getParent()
                         .children(ASTAssignmentOperator.class)
                         .isEmpty();
        }
        return false;
    }


    private boolean isReplaceableListLoop(ASTForStatement stmt, List<NameOccurrence> occurrences,
                                          VariableNameDeclaration listDeclaration) {

        if (listDeclaration.getTypeImage() == null
            || !listDeclaration.getTypeImage().matches("List|ArrayList|LinkedList")) {
            return false;
        }

        String listName = listDeclaration.getName();


        for (NameOccurrence occ : occurrences) {

            if (occ.getLocation().getFirstParentOfType(ASTForUpdate.class) == null
                && occ.getLocation().getFirstParentOfType(ASTExpression.class)
                != stmt.getFirstChildOfType(ASTExpression.class)
                && !occurrenceIsListGet(occ, listName)) {
                return false;
            }
        }

        return true;
    }


    /** @return true if this occurrence is as an argument to List.get on the correct list */
    private static boolean occurrenceIsListGet(NameOccurrence occ, String listName) {
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


    private static Optional<Entry<VariableNameDeclaration, List<NameOccurrence>>> findDeclaration(String varName, Scope innermost) {
        Scope currentScope = innermost;

        while (currentScope != null) {
            for (Entry<VariableNameDeclaration, List<NameOccurrence>> e : currentScope.getDeclarations(VariableNameDeclaration.class).entrySet()) {
                if (e.getKey().getName().equals(varName)) {
                    return Optional.of(e);
                }
            }
            currentScope = currentScope.getParent();
        }

        return Optional.empty();
    }


    private static boolean isReplaceableIteratorLoop(Entry<VariableNameDeclaration, List<NameOccurrence>> indexInfo,
                                                     ASTExpression guardCondition,
                                                     Entry<VariableNameDeclaration, List<NameOccurrence>> iterableInfo,
                                                     ASTForStatement stmt) {

        List<NameOccurrence> occurrences = indexInfo.getValue();

        if (isIterableModifiedInsideLoop(iterableInfo, stmt) || occurrences.size() > 2) {
            return false;
        }

        return Optional.ofNullable(indexInfo.getKey().getName())
                       .filter(indexName -> guardCondition.children(ASTPrimaryExpression.class)
                                                          .children(ASTPrimaryPrefix.class)
                                                          .children(ASTName.class)
                                                          .filterMatching(Node::getImage, indexName + ".hasNext")
                                                          .nonEmpty())
                       .map(indexName -> occurrences.stream()
                                                    .map(NameOccurrence::getLocation)
                                                    .allMatch(n -> isCallingNext(n, indexName)))
                       .orElse(false);
    }


    private static boolean isCallingNext(Node node, String indexName) {
        return node instanceof ASTName
            && (node.hasImageEqualTo(indexName + ".hasNext")
            || node.hasImageEqualTo(indexName + ".next"));
    }


    private static boolean isIterableModifiedInsideLoop(Entry<VariableNameDeclaration, List<NameOccurrence>> iterableInfo,
                                                        ASTForStatement stmt) {

        return iterableInfo.getValue().stream()
                           .map(NameOccurrence::getLocation)
                           .filter(n -> n.ancestors(ASTForStatement.class)
                                         .firstOpt()
                                         .filter(forParent -> Objects.equals(forParent, stmt))
                                         .isPresent())
                           .anyMatch(it -> it.hasImageEqualTo(iterableInfo.getKey().getName() + ".remove"));
    }


}
