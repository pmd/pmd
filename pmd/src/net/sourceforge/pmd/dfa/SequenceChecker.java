/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dfa;

import java.util.ArrayList;
import java.util.List;

/**
 * @author raik
 *         <p/>
 *         Computes the first sequence in a list.
 *         <p/>
 *         e.g.
 *         IF_START			0
 *         WHILE_EXPR		1
 *         WHILE_END		2
 *         IF_END			3
 *         <p/>
 *         The first sequence is WHILE_EXPR und WHILE_END. It returns always the
 *         first inner nested scope.
 */
public class SequenceChecker {

    /*
     * Element of logical structure of brace nodes.
     * */
    private static class Status {
        public static final int ROOT = -1;

        private List<Status> nextSteps = new ArrayList<Status>();
        private int type; //NOPMD type is used, but PMD seems to no be able to spot it
        private boolean lastStep;


        public Status(int type) {
            this(type, false);
        }

        public Status(int type, boolean lastStep) {
            this.type = type;
            this.lastStep = lastStep;
        }

        public void addStep(Status type) {
            nextSteps.add(type);
        }

        public Status step(int type) {
            for (int i = 0; i < this.nextSteps.size(); i++) {
                if (type == nextSteps.get(i).type) {
                    return nextSteps.get(i);
                }
            }
            return null;
        }

        public boolean isLastStep() {
            return this.lastStep;
        }

        public boolean hasMoreSteps() {
            return this.nextSteps.size() > 1;
        }
    }

    private static Status root;

    static {
        root = new Status(Status.ROOT);
        Status ifNode = new Status(NodeType.IF_EXPR);
        Status ifSt = new Status(NodeType.IF_LAST_STATEMENT);
        Status ifStWithoutElse = new Status(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, true);
        Status elseSt = new Status(NodeType.ELSE_LAST_STATEMENT, true);
        Status whileNode = new Status(NodeType.WHILE_EXPR);
        Status whileSt = new Status(NodeType.WHILE_LAST_STATEMENT, true);
        Status switchNode = new Status(NodeType.SWITCH_START);
        Status caseSt = new Status(NodeType.CASE_LAST_STATEMENT);
        Status switchDefault = new Status(NodeType.SWITCH_LAST_DEFAULT_STATEMENT);
        Status switchEnd = new Status(NodeType.SWITCH_END, true);

        Status forInit = new Status(NodeType.FOR_INIT);
        Status forExpr = new Status(NodeType.FOR_EXPR);
        Status forUpdate = new Status(NodeType.FOR_UPDATE);
        Status forSt = new Status(NodeType.FOR_BEFORE_FIRST_STATEMENT);
        Status forEnd = new Status(NodeType.FOR_END, true);

        Status doSt = new Status(NodeType.DO_BEFORE_FIRST_STATEMENT);
        Status doExpr = new Status(NodeType.DO_EXPR, true);

        Status labelNode = new Status(NodeType.LABEL_STATEMENT);
        Status labelEnd = new Status(NodeType.LABEL_LAST_STATEMENT, true);

        root.addStep(ifNode);
        root.addStep(whileNode);
        root.addStep(switchNode);
        root.addStep(forInit);
        root.addStep(forExpr);
        root.addStep(forUpdate);
        root.addStep(forSt);
        root.addStep(doSt);
        root.addStep(labelNode);

        ifNode.addStep(ifSt);
        ifNode.addStep(ifStWithoutElse);
        ifSt.addStep(elseSt);
        ifStWithoutElse.addStep(root);
        elseSt.addStep(root);

        labelNode.addStep(labelEnd);
        labelEnd.addStep(root);

        whileNode.addStep(whileSt);
        whileSt.addStep(root);

        switchNode.addStep(caseSt);
        switchNode.addStep(switchDefault);
        switchNode.addStep(switchEnd);
        caseSt.addStep(caseSt);
        caseSt.addStep(switchDefault);
        caseSt.addStep(switchEnd);
        switchDefault.addStep(switchEnd);
        switchDefault.addStep(caseSt);
        switchEnd.addStep(root);

        forInit.addStep(forExpr);
        forInit.addStep(forUpdate);
        forInit.addStep(forSt);
        forExpr.addStep(forUpdate);
        forExpr.addStep(forSt);
        forUpdate.addStep(forSt);
        forSt.addStep(forEnd);
        forEnd.addStep(root);

        doSt.addStep(doExpr);
        doExpr.addStep(root);
    }

    private Status aktStatus;
    private List bracesList;

    private int firstIndex = -1;
    private int lastIndex = -1;

    /*
     * Defines the logical structure.
     * */
    public SequenceChecker(List bracesList) {
        this.aktStatus = root;
        this.bracesList = bracesList;
    }

    /**
     * Finds the first most inner sequence e.g IFStart & IFEnd. If no sequence
     * is found or the list is empty the method returns false.
     */
    public boolean run() {
        this.aktStatus = root;
        this.firstIndex = 0;
        this.lastIndex = 0;
        boolean lookAhead = false;

        for (int i = 0; i < this.bracesList.size(); i++) {
            StackObject so = (StackObject) bracesList.get(i);
            aktStatus = this.aktStatus.step(so.getType());

            if (aktStatus == null) {
                if (lookAhead) {
                    this.lastIndex = i - 1;
                    return false;
                }
                this.aktStatus = root;
                this.firstIndex = i;
                i--;
                continue;
            } else {
                if (aktStatus.isLastStep() && !aktStatus.hasMoreSteps()) {
                    this.lastIndex = i;
                    return false;
                } else if (aktStatus.isLastStep() && aktStatus.hasMoreSteps()) {
                    lookAhead = true;
                    this.lastIndex = i;
                }
            }
        }
        return this.firstIndex == this.lastIndex;
    }

    public int getFirstIndex() {
        return this.firstIndex;
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

}
