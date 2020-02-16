/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Searches for special nodes and computes based on the sequence, the type of
 * access of a variable.
 *
 * @since Created on 14.07.2004
 * @author raik, Sven Jacob
 */
public class VariableAccessVisitor extends JavaParserVisitorAdapter {

    public void compute(ASTMethodDeclaration node) {
        if (node.getParent() instanceof ASTClassOrInterfaceBodyDeclaration) {
            this.computeNow(node);
        }
    }

    public void compute(ASTConstructorDeclaration node) {
        this.computeNow(node);
    }

    private void computeNow(Node node) {


        DataFlowNode inode = node.getDataFlowNode();

        List<VariableAccess> undefinitions = markUsages(inode);

        // all variables are first in state undefinition
        DataFlowNode firstINode = inode.getFlow().get(0);
        firstINode.setVariableAccess(undefinitions);

        // all variables are getting undefined when leaving scope
        DataFlowNode lastINode = inode.getFlow().get(inode.getFlow().size() - 1);
        lastINode.setVariableAccess(undefinitions);
    }

    private List<VariableAccess> markUsages(DataFlowNode inode) {
        // undefinitions was once a field... seems like it works fine as a local
        List<VariableAccess> undefinitions = new ArrayList<>();
        Set<Map<VariableNameDeclaration, List<NameOccurrence>>> variableDeclarations = collectDeclarations(inode);
        for (Map<VariableNameDeclaration, List<NameOccurrence>> declarations : variableDeclarations) {
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : declarations.entrySet()) {
                VariableNameDeclaration vnd = entry.getKey();
                if (vnd.getAccessNodeParent() instanceof ASTFormalParameter) {
                    // no definition/undefinition/references for parameters
                    continue;
                } else if (vnd.getAccessNodeParent().getFirstDescendantOfType(ASTVariableInitializer.class) != null) {
                    // add definition for initialized variables
                    addVariableAccess(vnd.getNode(), new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()),
                            inode.getFlow());
                }
                undefinitions.add(new VariableAccess(VariableAccess.UNDEFINITION, vnd.getImage()));

                //Map the name occurrences to their assignment expressions, if any
                List<SimpleEntry<Node, NameOccurrence>> occurrencesWithAssignmentExp = new ArrayList<>();
                for (NameOccurrence occurrence : entry.getValue()) {
                    // find the nearest assignment, if any
                    Node potentialAssignment = occurrence.getLocation().getFirstParentOfAnyType(ASTStatementExpression.class,
                                                                                            ASTExpression.class);
                    while (potentialAssignment != null
                            && (potentialAssignment.getNumChildren() < 2
                                    || !(potentialAssignment.getChild(1) instanceof ASTAssignmentOperator))) {
                        potentialAssignment = potentialAssignment.getFirstParentOfAnyType(ASTStatementExpression.class,
                                ASTExpression.class);
                    }
                    // at this point, potentialAssignment is either a assignment or null
                    occurrencesWithAssignmentExp.add(new SimpleEntry<>(potentialAssignment, occurrence));
                }

                //The name occurrences are in source code order, the means, the left hand side of
                //the assignment is first. But this is not the order in which the data flows: first the
                //right hand side is evaluated before the left hand side is assigned.
                //Therefore move the name occurrences backwards if they belong to the same assignment expression.
                for (int i = 0; i < occurrencesWithAssignmentExp.size() - 1; i++) {
                    SimpleEntry<Node, NameOccurrence> oc = occurrencesWithAssignmentExp.get(i);
                    SimpleEntry<Node, NameOccurrence> nextOc = occurrencesWithAssignmentExp.get(i + 1);

                    if (oc.getKey() != null && oc.getKey().equals(nextOc.getKey())) {
                        Collections.swap(occurrencesWithAssignmentExp, i, i + 1);
                    }
                }

                for (SimpleEntry<Node, NameOccurrence> oc : occurrencesWithAssignmentExp) {
                    addAccess((JavaNameOccurrence) oc.getValue(), inode);
                }
            }
        }
        return undefinitions;
    }

    private Set<Map<VariableNameDeclaration, List<NameOccurrence>>> collectDeclarations(DataFlowNode inode) {
        Set<Map<VariableNameDeclaration, List<NameOccurrence>>> decls = new HashSet<>();
        Map<VariableNameDeclaration, List<NameOccurrence>> varDecls;
        for (int i = 0; i < inode.getFlow().size(); i++) {
            DataFlowNode n = inode.getFlow().get(i);
            if (n instanceof StartOrEndDataFlowNode) {
                continue;
            }
            varDecls = ((JavaNode) n.getNode()).getScope().getDeclarations(VariableNameDeclaration.class);
            if (!decls.contains(varDecls)) {
                decls.add(varDecls);
            }
        }
        return decls;
    }

    private void addAccess(JavaNameOccurrence occurrence, DataFlowNode inode) {
        if (occurrence.isOnLeftHandSide()) {
            this.addVariableAccess(occurrence.getLocation(),
                    new VariableAccess(VariableAccess.DEFINITION, occurrence.getImage()), inode.getFlow());
        } else if (occurrence.isOnRightHandSide()
                || !occurrence.isOnLeftHandSide() && !occurrence.isOnRightHandSide()) {
            this.addVariableAccess(occurrence.getLocation(),
                    new VariableAccess(VariableAccess.REFERENCING, occurrence.getImage()), inode.getFlow());
        }
    }

    /**
     * Adds a VariableAccess to a dataflow node.
     *
     * @param node
     *            location of the access of a variable
     * @param va
     *            variable access to add
     * @param flow
     *            dataflownodes that can contain the node.
     */
    private void addVariableAccess(Node node, VariableAccess va, List<DataFlowNode> flow) {
        // backwards to find the right inode (not a method declaration)
        for (int i = flow.size() - 1; i > 0; i--) {
            DataFlowNode inode = flow.get(i);
            if (inode.getNode() == null) {
                continue;
            }

            List<? extends Node> children = inode.getNode().findDescendantsOfType(node.getClass());
            for (Node n : children) {
                if (node.equals(n)) {
                    List<VariableAccess> v = new ArrayList<>();
                    v.add(va);
                    inode.setVariableAccess(v);
                    return;
                }
            }
        }
    }

}
