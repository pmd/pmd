/*
 * Created on 12.07.2004
 */
package net.sourceforge.pmd.dfa;

import java.util.List;

/**
 * @author raik
 *         <p/>
 *         Links data flow nodes eachother.
 */
public class Linker {

    private List braceStack;
    private List cbrStack;

    public Linker(IProcessableStructure dataFlow) {
        this.braceStack = dataFlow.getBraceStack();
        this.cbrStack = dataFlow.getCBRStack();
    }
    
    /**
     * Creates all the links between the data flow nodes.
     */
    public void computePaths() throws LinkerException, SequenceException {
        if (braceStack == null || cbrStack == null) {
            throw new LinkerException();
        }

        SequenceChecker sc = new SequenceChecker(braceStack);

        /*
         * Returns true if there are more sequences, computes the first and
         * the last index of the sequence.
         * */
        while (!sc.run()) {
            if (sc.getFirstIndex() < 0 || sc.getLastIndex() < 0) {
                throw new SequenceException("computePaths(): return index <  0");
            }

            StackObject firstSO = (StackObject) braceStack.get(sc.getFirstIndex());

            switch (firstSO.getType()) {
                case NodeType.IF_EXPR:
                    int x = sc.getLastIndex() - sc.getFirstIndex();
                    if (x == 2) {
                        this.computeIf(sc.getFirstIndex(), sc.getFirstIndex() + 1, sc.getLastIndex());
                    } else if (x == 1) {
                        this.computeIf(sc.getFirstIndex(), sc.getLastIndex());
                    } else {
                        System.out.println("Error - computePaths 1");
                    }
                    break;

                case NodeType.WHILE_EXPR:
                    this.computeWhile(sc.getFirstIndex(), sc.getLastIndex());
                    break;

                case NodeType.SWITCH_START:
                    this.computeSwitch(sc.getFirstIndex(), sc.getLastIndex());
                    break;

                case NodeType.FOR_INIT:
                case NodeType.FOR_EXPR:
                case NodeType.FOR_UPDATE:
                case NodeType.FOR_BEFORE_FIRST_STATEMENT:
                    this.computeFor(sc.getFirstIndex(), sc.getLastIndex());
                    break;

                case NodeType.DO_BEFORE_FIRST_STATEMENT:
                    this.computeDo(sc.getFirstIndex(), sc.getLastIndex());
                    break;

                default:
            }

            for (int y = sc.getLastIndex(); y >= sc.getFirstIndex(); y--) {
                braceStack.remove(y);
            }
        }

        while (!this.cbrStack.isEmpty()) {
            StackObject so = (StackObject) cbrStack.get(0);
            IDataFlowNode node = so.getDataFlowNode();

            switch (so.getType()) {
                case NodeType.RETURN_STATEMENT:
                    //	remove all children (should contain only one child)
                    node.removePathToChild((IDataFlowNode) node.getChildren().get(0));

                    IDataFlowNode lastNode =
                            (IDataFlowNode) node.getFlow().get(node.getFlow().size() - 1);
                    node.addPathToChild(lastNode);

                    cbrStack.remove(0);
                    break;

                case NodeType.BREAK_STATEMENT:
                    List bList = node.getFlow();
                    for (int i = bList.indexOf(node); i < bList.size(); i++) {
                        IDataFlowNode n = (IDataFlowNode) bList.get(i);

                        if (n.isType(NodeType.WHILE_LAST_STATEMENT) ||
                                n.isType(NodeType.SWITCH_END) ||
                                n.isType(NodeType.FOR_END) ||
                                n.isType(NodeType.DO_EXPR)) {

                            node.removePathToChild((IDataFlowNode) node.getChildren().get(0));

                            IDataFlowNode last = (IDataFlowNode) bList.get(i + 1);
                            node.addPathToChild(last);

                            cbrStack.remove(0);
                            break;
                        }
                    }
                    break;

                case NodeType.CONTINUE_STATEMENT:
                    List cList = node.getFlow();

                    /* traverse up the tree and find the first loop start node
                     *
                     * is StackObject usefull? should'nt the information be stored
                     * in the node itself?
                     *
                     * one node could have several status - that means that the
                     * same node is stored several times in a stack with a different
                     * status
                     *
                     * the node could be stored several times
                     * on the stack too, but the node now contains the status itself
                     * and it is impossible to recognize the order the status
                     * was stored.
                     *
                     * it will be also impossible to determine the status (e.g getType())
                     * of a node, because the node could contain more than one
                     * status
                     */
                    for (int i = cList.indexOf(node) - 1; i >= 0; i--) {
                        IDataFlowNode n = (IDataFlowNode) cList.get(i);

                        if (n.isType(NodeType.FOR_UPDATE) ||
                                n.isType(NodeType.FOR_EXPR) ||
                                n.isType(NodeType.WHILE_EXPR)) {

                            //remove all children (should contain only one child)
                            node.removePathToChild((IDataFlowNode) node.getChildren().get(0));

                            node.addPathToChild(n);
                            cbrStack.remove(0);
                            break;
                        } else if (n.isType(NodeType.DO_BEFORE_FIRST_STATEMENT)) {

                            IDataFlowNode inode = (IDataFlowNode) n.getFlow().get(n.getIndex() + 1);

                            for (int j = 0; j < inode.getParents().size(); j++) {
                                IDataFlowNode parent = (IDataFlowNode) inode.getParents().get(j);

                                if (parent.isType(NodeType.DO_EXPR)) {
                                    node.removePathToChild((IDataFlowNode) node.getChildren().get(0));
                                    node.addPathToChild(parent);

                                    cbrStack.remove(0);
                                    break;
                                }
                            }
                            break;
                        }
                    }
            }
        }
    }
	

    private void computeDo(int first, int last) {
        IDataFlowNode doSt = ((StackObject)braceStack.get(first)).getDataFlowNode();
        IDataFlowNode doExpr = ((StackObject)braceStack.get(last)).getDataFlowNode();

        //IDataFlowNode doFirst = (IDataFlowNode)doSt.getChildren().get(0);
        IDataFlowNode doFirst = (IDataFlowNode) doSt.getFlow().get(doSt.getIndex() + 1);

        if (doFirst.getIndex() != doExpr.getIndex()) {
            doExpr.addPathToChild(doFirst);
        }
    }
	
    private void computeFor(int firstIndex, int lastIndex) {
        IDataFlowNode fExpr = null;
        IDataFlowNode fUpdate = null;
        IDataFlowNode fSt = null;
        IDataFlowNode fEnd = null;
        boolean isInit = false;
        boolean isExpr = false;
        boolean isUpdate = false;

        for (int i = firstIndex; i <= lastIndex; i++) {
            StackObject so = (StackObject) this.braceStack.get(i);
            IDataFlowNode node = so.getDataFlowNode();

            if (so.getType() == NodeType.FOR_INIT) {
                isInit = true;
            }
            if (so.getType() == NodeType.FOR_EXPR) {
                fExpr = node;
                isExpr = true;
            }
            if (so.getType() == NodeType.FOR_UPDATE) {
                fUpdate = node;
                isUpdate = true;
            }
            if (so.getType() == NodeType.FOR_BEFORE_FIRST_STATEMENT) {
                fSt = node;
            }
            if (so.getType() == NodeType.FOR_END) {
                fEnd = node;
            }
        }

        IDataFlowNode end = (IDataFlowNode) fEnd.getFlow().get(fEnd.getIndex() + 1);
        IDataFlowNode firstSt = (IDataFlowNode) fSt.getChildren().get(0);

        if (isUpdate) {
            if (fSt.getIndex() != fEnd.getIndex()) {
                end.reverseParentPathsTo(fUpdate);

                fExpr.removePathToChild(fUpdate);
                fUpdate.addPathToChild(fExpr);

                fUpdate.removePathToChild(firstSt);

                fExpr.addPathToChild(firstSt);
                fExpr.addPathToChild(end);
            } else {
                fSt.removePathToChild(end);

                fExpr.removePathToChild(fUpdate);
                fUpdate.addPathToChild(fExpr);

                fExpr.addPathToChild(fUpdate);
                fExpr.addPathToChild(end);
            }
        } else {
            if (fSt.getIndex() != fEnd.getIndex()) {
                end.reverseParentPathsTo(fExpr);
                fExpr.addPathToChild(end);
            }
        }
    }

    private void computeSwitch(int firstIndex, int lastIndex) {
        int diff = lastIndex - firstIndex;
        boolean defaultStatement = false;
        IDataFlowNode sStart = ((StackObject) this.braceStack.get(firstIndex)).getDataFlowNode();
        IDataFlowNode sEnd = ((StackObject) this.braceStack.get(lastIndex)).getDataFlowNode();
        IDataFlowNode end = (IDataFlowNode) sEnd.getChildren().get(0);
        for (int i = 0; i < diff - 2; i++) {
            StackObject so = (StackObject) this.braceStack.get(firstIndex + 2 + i);
            IDataFlowNode node = so.getDataFlowNode();
            sStart.addPathToChild((IDataFlowNode) node.getChildren().get(0));
            if (so.getType() == NodeType.SWITCH_LAST_DEFAULT_STATEMENT) {
                defaultStatement = true;
            }
        }
        if (!defaultStatement) {
            sStart.addPathToChild(end);
        }
    }
	
    private void computeWhile(int first, int last) {
        IDataFlowNode wStart = ((StackObject) this.braceStack.get(first)).getDataFlowNode();
        IDataFlowNode wEnd = ((StackObject) this.braceStack.get(last)).getDataFlowNode();
        IDataFlowNode end = (IDataFlowNode) wEnd.getFlow().get(wEnd.getIndex() + 1);

        if (wStart.getIndex() != wEnd.getIndex()) {
            end.reverseParentPathsTo(wStart);
            wStart.addPathToChild(end);
        }
    }

    private void computeIf(int first, int second, int last) {
        IDataFlowNode ifStart = ((StackObject) this.braceStack.get(first)).getDataFlowNode();
        IDataFlowNode ifEnd = ((StackObject) this.braceStack.get(second)).getDataFlowNode();
        IDataFlowNode elseEnd = ((StackObject) this.braceStack.get(last)).getDataFlowNode();
        IDataFlowNode elseStart = (IDataFlowNode) ifEnd.getFlow().get(ifEnd.getIndex() + 1);
        IDataFlowNode end = (IDataFlowNode) elseEnd.getFlow().get(elseEnd.getIndex() + 1);

        // if if-statement and else-statement contains statements or expressions
        if (ifStart.getIndex() != ifEnd.getIndex() &&
                ifEnd.getIndex() != elseEnd.getIndex()) {

            elseStart.reverseParentPathsTo(end);
            ifStart.addPathToChild(elseStart);
        }
        // if only if-statement contains no expressions
        else if (ifStart.getIndex() == ifEnd.getIndex() &&
                ifEnd.getIndex() != elseEnd.getIndex()) {

            ifStart.addPathToChild(end);
        }
        // if only else-statement contains no expressions
        else if (ifEnd.getIndex() == elseEnd.getIndex() &&
                ifStart.getIndex() != ifEnd.getIndex()) {

            ifStart.addPathToChild(end);
        }
    }
	
    private void computeIf(int first, int last) {
        IDataFlowNode ifStart = ((StackObject) this.braceStack.get(first)).getDataFlowNode();
        IDataFlowNode ifEnd = ((StackObject) this.braceStack.get(last)).getDataFlowNode();

        // only if the if-statement contains another Statement or Expression
        if (ifStart.getIndex() != ifEnd.getIndex()) {
            IDataFlowNode end = (IDataFlowNode) ifEnd.getFlow().get(ifEnd.getIndex() + 1);
            ifStart.addPathToChild(end);
        }
    }
}
