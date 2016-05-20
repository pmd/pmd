/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author raik
 *         <p/>
 *         Computes the first sequence in a list.
 *         <p/>
 *         e.g. IF_START 0 WHILE_EXPR 1 WHILE_END 2 IF_END 3
 *         <p/>
 *         The first sequence is WHILE_EXPR and WHILE_END. It returns always the
 *         first inner nested scope.
 */
public class SequenceChecker {
    private static final Logger LOGGER = Logger.getLogger(SequenceChecker.class.getName());

    /*
     * Element of logical structure of brace nodes.
     */
    private static class Status {

        public static final int ROOT = -1;

        private List<Status> nextSteps = new ArrayList<>();
        // NodeType
        private int type;
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

        /**
         * 
         * @param type candidate
         * @return valid Status or null if NodeType is not a valid transition
         *         NodeType
         */
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

        @Override
        public String toString() {
            return "NodeType=" + NodeType.stringFromType(type) + "(" + type + "), lastStep=" + lastStep;
        }
    }

    private static Status root;

    /**
     * Create State transition map for the control structures
     */
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
    private List<StackObject> bracesList;

    private int firstIndex = -1;
    private int lastIndex = -1;

    /*
     * Defines the logical structure.
     */
    public SequenceChecker(List<StackObject> bracesList) {
        this.aktStatus = root;
        this.bracesList = bracesList;
    }

    /**
     * Finds the first innermost sequence e.g IFStart & IFEnd. If the list has
     * been exhausted (firstIndex==lastIndex) the method returns true.
     */
    public boolean run() {
        LOGGER.entering(this.getClass().getCanonicalName(), "run");
        this.aktStatus = root;
        this.firstIndex = 0;
        this.lastIndex = 0;
        boolean lookAhead = false;

        /*
         * Walk through the bracesList attempting to identify the first
         * contiguous graph of Nodes from the initial Status to a final Status.
         * 
         * There are 2 loop indexes:- i which ranges through the bracesList:
         * this may be reset l serves as a control to cope with invalid lists of
         * StackObjects, preventing infinite loops within the SequenceChecker.
         */
        int maximumIterations = this.bracesList.size() * this.bracesList.size();
        int l = -1;
        for (int i = 0; i < this.bracesList.size(); i++) {
            l++;
            StackObject so = bracesList.get(i);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("Processing bracesList(l,i)=(" + l + "," + i + ") of Type "
                        + NodeType.stringFromType(so.getType()) + " with State (aktStatus) = " + aktStatus);
                // LOGGER.finest("StackObject of Type="+so.getType());
                LOGGER.finest("DataFlowNode @ line " + so.getDataFlowNode().getLine() + " and index="
                        + so.getDataFlowNode().getIndex());
            }

            // Attempt to get to this StackObject's NodeType from the current
            // State
            aktStatus = this.aktStatus.step(so.getType());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("Transition aktStatus=" + aktStatus);
            }

            if (aktStatus == null) { // Not a valid Node from the current State
                if (lookAhead) {
                    this.lastIndex = i - 1;
                    LOGGER.finer("aktStatus is NULL (lookAhead): Invalid transition");
                    LOGGER.exiting(this.getClass().getCanonicalName(), "run", false);
                    return false;
                }
                // Cope with incorrect bracesList contents
                else if (l > maximumIterations) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.severe("aktStatus is NULL: maximum Iterations exceeded, abort " + i);
                    }
                    LOGGER.exiting(this.getClass().getCanonicalName(), "run", false);
                    return false;
                } else {
                    this.aktStatus = root;
                    this.firstIndex = i;
                    i--;
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.finest("aktStatus is NULL: Restarting search continue i==" + i + ", firstIndex="
                                + this.firstIndex);
                    }
                    continue;
                }
            } else { // This NodeType _is_ a valid transition from the previous
                     // State
                if (aktStatus.isLastStep() && !aktStatus.hasMoreSteps()) { // Terminal
                                                                           // State
                    this.lastIndex = i;
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.finest("aktStatus is NOT NULL: lastStep reached and no moreSteps - firstIndex="
                                + firstIndex + ", lastIndex=" + lastIndex);
                    }
                    LOGGER.exiting(this.getClass().getCanonicalName(), "run", false);
                    return false;
                } else if (aktStatus.isLastStep() && aktStatus.hasMoreSteps()) {
                    lookAhead = true;
                    this.lastIndex = i;
                    LOGGER.finest("aktStatus is NOT NULL: set lookAhead on");
                }
            }
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finer("Completed search: firstIndex=" + firstIndex + ", lastIndex=" + lastIndex);
        }
        LOGGER.exiting(this.getClass().getCanonicalName(), "run", this.firstIndex == this.lastIndex);
        return this.firstIndex == this.lastIndex;
    }

    public int getFirstIndex() {
        return this.firstIndex;
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

}
