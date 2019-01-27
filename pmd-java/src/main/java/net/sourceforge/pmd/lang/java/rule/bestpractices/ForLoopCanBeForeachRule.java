/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpressionList;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
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

        if (node.isForeach()) {
            return data;
        }

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

        if (TypeHelper.isExactlyAny(index, Iterator.class)) {
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
                   .first(it -> it.children(ASTVariableDeclarator.class).count() == 1)
                   .map(decl -> getInfoAboutForIndexVar(init));
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
                         .filter(it -> it.jjtGetNumChildren() == 1)
                         .children(ASTStatementExpression.class)
                         .children()
                         .filter(
                             it -> it instanceof ASTPostfixExpression && it.hasImageEqualTo("++")
                                 || it instanceof ASTPreIncrementExpression
                         )
                         .children(ASTPrimaryExpression.class)
                         .children(ASTPrimaryPrefix.class)
                         .children(ASTName.class)
                         .first()
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
                         .withImage("0")
                         .filterNot(ASTLiteral::isStringLiteral)
                         .any();
    }


    private static Entry<VariableNameDeclaration, List<NameOccurrence>> getInfoAboutForIndexVar(ASTForInit init) {
        Map<VariableNameDeclaration, List<NameOccurrence>> decls = init.getScope().getDeclarations(VariableNameDeclaration.class);

        for (Entry<VariableNameDeclaration, List<NameOccurrence>> e : decls.entrySet()) {

            ASTForInit declInit = e.getKey().getNode().getFirstParentOfType(ASTForInit.class);
            if (Objects.equals(declInit, init)) {
                return e;
            }
        }

        return null;
    }


    /**
     * Gets the name of the iterable array or list.
     *
     * @param itName The name of the iterator variable
     *
     * @return The name, or null if it couldn't be found or the guard condition is not safe to refactor (then abort)
     */
    private Optional<String> findIterableName(ASTExpression guardCondition, String itName) {


        if (guardCondition.jjtGetNumChildren() > 0
            && guardCondition.jjtGetChild(0) instanceof ASTRelationalExpression) {

            ASTRelationalExpression relationalExpression = (ASTRelationalExpression) guardCondition.jjtGetChild(0);

            if (relationalExpression.hasImageEqualTo("<") || relationalExpression.hasImageEqualTo("<=")) {

                boolean leftIsIndexVarName =
                    guardCondition
                        .singletonStream()
                        .children(ASTRelationalExpression.class)
                        .children(ASTPrimaryExpression.class)
                        .children(ASTPrimaryPrefix.class)
                        .children(ASTName.class)
                        .withImage(itName)
                        .any();

                if (!leftIsIndexVarName) {
                    return Optional.empty();
                }

                return guardCondition.singletonStream()
                                     .children(ASTRelationalExpression.class)
                                     .forkJoin(
                                         rel -> NodeStream.of(rel).withImage("<"),
                                         rel -> NodeStream.of(rel)
                                                          .withImage("<=")
                                                          .children(ASTAdditiveExpression.class)
                                                          .filter(expr ->
                                                               expr.jjtGetNumChildren() == 2
                                                                   && expr.getOperator().equals("-")
                                                                   && expr.singletonStream()
                                                                          .children(ASTPrimaryExpression.class)
                                                                          .children(ASTPrimaryPrefix.class)
                                                                          .children(ASTLiteral.class)
                                                                          .withImage("1")
                                                                          .any()
                                                   )
                                     )
                                     .children(ASTPrimaryExpression.class)
                                     .children(ASTPrimaryPrefix.class)
                                     .children(ASTName.class)
                                     .imageMatching("\\w+\\.(size|length)")
                                     .first()
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
                         .first()             // TODO : This can return null if we are calling a local / statically imported method that returns the iterable - currently unhandled
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

            return suffix.descendants(ASTExpression.class)
                         .children(ASTPrimaryExpression.class)
                         .filter(it -> it.jjtGetNumChildren() == 1)
                         .children(ASTPrimaryPrefix.class)
                         .children(ASTName.class)
                         .withImage(occ.getImage())
                         .any()
                && suffix.singletonStream()
                         .precedingSiblings()
                         .filterIs(ASTPrimaryPrefix.class)
                         .children(ASTName.class)
                         .withImage(arrayName)
                         .any()

                && suffix.jjtGetParent()
                         .jjtGetParent()
                         .singletonStream()
                         .children(ASTAssignmentOperator.class)
                         .none();
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
                && !occurenceIsListGet(occ, listName)) {
                return false;
            }
        }

        return true;
    }


    /** @return true if this occurence is as an argument to List.get on the correct list */
    private static boolean occurenceIsListGet(NameOccurrence occ, String listName) {
        if (occ.getLocation() instanceof ASTName) {
            ASTPrimarySuffix suffix = occ.getLocation().getFirstParentOfType(ASTPrimarySuffix.class);

            if (suffix == null) {
                return false;
            }

            Node prefix = suffix.jjtGetParent().jjtGetChild(0);

            if (!(prefix instanceof ASTPrimaryPrefix) || prefix.jjtGetNumChildren() != 1
                || !(prefix.jjtGetChild(0) instanceof ASTName)) {
                // it's either not a primary prefix, doesn't have children (can happen with this./super.)
                // or first child is not a name
                return false;
            }

            String callImage = prefix.jjtGetChild(0).getImage();

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
                                                          .withImage(indexName + ".hasNext")
                                                          .any())
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
                           .filter(n -> n.ancestorStream()
                                         .first(ASTForStatement.class)
                                         .filter(forParent -> Objects.equals(forParent, stmt))
                                         .isPresent())
                           .filter(it -> it.hasImageEqualTo(iterableInfo.getKey().getName() + ".remove"))
                           .findAny()
                           .isPresent();
    }


}
