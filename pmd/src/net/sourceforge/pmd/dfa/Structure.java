/*
 * Created on 11.07.2004
 */
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * @author raik
 *         <p/>
 *         Structure contains only raw data. A set of nodes wich represent a data flow
 *         and 2 stacks to link the nodes eachother.
 */
public class Structure implements IProcessableStructure {

    private LinkedList dataFlow;
    private Stack braceStack;
    private Stack cbrStack;

    public Structure() {
        this.dataFlow = new LinkedList();
        this.braceStack = new Stack();
        this.cbrStack = new Stack();
    }

    /**
     * This class capsulate the access to the DataFlowNode class. meaningfull?
     */
    public IDataFlowNode addNewNode(SimpleNode node) {
        return new DataFlowNode(node, this.dataFlow);
    }

    public IDataFlowNode addStartOrEndNode(int line) {
        return new DataFlowNode(this.dataFlow, line);
    }

    public IDataFlowNode getLast() {
        return (IDataFlowNode) this.dataFlow.getLast();
    }

    public IDataFlowNode getFirst() {
        return (IDataFlowNode) this.dataFlow.getFirst();
    }

//  ----------------------------------------------------------------------------
//	STACK FUNCTIONS

    /**
     * The braceStack contains all nodes which are important to link the data
     * flow nodes. The cbrStack contains continue,- break- and return nodes.
     * There are 2 Stacks because the have to process differently.
     */
    protected void pushOnStack(int type, IDataFlowNode node) {
        if (type == NodeType.RETURN_STATEMENT ||
                type == NodeType.BREAK_STATEMENT ||
                type == NodeType.CONTINUE_STATEMENT) {

            // ugly solution - stores the type information in two ways
            this.cbrStack.push(new StackObject(type, node));
            ((DataFlowNode) node).setType(type);
        } else {
            this.braceStack.push(new StackObject(type, node));
            ((DataFlowNode) node).setType(type);
        }
    }

    public List getBraceStack() {
        return this.braceStack;
    }

    public List getCBRStack() {
        return this.cbrStack;
    }

}
