package net.sourceforge.pmd.ui.views;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Viewer for the DataFlowGraph, contains the DataflowGraphTable
 * 
 * @author SebastianRaffel ( 30.05.2005 )
 */
public class DataflowGraphViewer extends Composite {

    private SimpleNode method;
    private String resourceString;
    private DataflowGraphTable table;
    private DataflowGraph graph;

    protected static final int NODE_RADIUS = 12;
    protected static final int LINE_LENGTH = 25;
    protected static final int rowHeight = 2 * NODE_RADIUS + LINE_LENGTH;
    protected int[] colWidths;
    protected Color bgColor;
    protected Color lineColor;
    protected Color textColor;

    /**
     * Constructor
     * 
     * @param parent the parent Composite
     * @param style the SWT Style
     */
    public DataflowGraphViewer(Composite parent, int style) {
        super(parent, style);
        setLayoutData(new GridData(GridData.FILL_BOTH));

        table = initTable(this, style);

        GridLayout mainLayout = new GridLayout(1, false);
        mainLayout.marginHeight = mainLayout.marginWidth = 0;
        mainLayout.horizontalSpacing = mainLayout.verticalSpacing = 0;
        setLayout(mainLayout);
    }
    
    public void addMouseListener(MouseListener mouseListener) {
        if (graph != null) {
            graph.addMouseListener(mouseListener);
        }
    }

    /**
     * Inits the Table.
     * 
     * @param parent
     * @param style
     * @return the DataflowGraphTable
     */
    private DataflowGraphTable initTable(Composite parent, int style) {
        DataflowGraphTable dfaTable = new DataflowGraphTable(parent, style);

        // set Column-Widths ans Header-Titles
        colWidths = new int[] { 50, 250, 70, 200, 200 };
        String[] headerTitles = { getString(StringKeys.MSGKEY_VIEW_DATAFLOW_GRAPH_COLUMN_LINE),
                getString(StringKeys.MSGKEY_VIEW_DATAFLOW_GRAPH_COLUMN_GRAPH),
                getString(StringKeys.MSGKEY_VIEW_DATAFLOW_GRAPH_COLUMN_NEXT),
                getString(StringKeys.MSGKEY_VIEW_DATAFLOW_GRAPH_COLUMN_VALUES),
                getString(StringKeys.MSGKEY_VIEW_DATAFLOW_GRAPH_COLUMN_CODE) };
        dfaTable.setColumns(colWidths, headerTitles, 1);

        // set the Colors
        bgColor = new Color(null, 255, 255, 255);
        lineColor = new Color(null, 192, 192, 192);
        textColor = new Color(null, 0, 0, 0);
        dfaTable.setColors(textColor, bgColor, lineColor);

        return dfaTable;
    }

    /**
     * Sets the data for this Viewer, gives the Table Data to show.
     * 
     * @param node 
     * @param resString the Node's Resource as String
     */
    public void setData(SimpleNode node, String resString) {
        if (method != null) {
            table.dispose();
            table = initTable(this, SWT.NONE);
            layout();
        }

        method = node;
        resourceString = resString;

        // set the Data for the Table
        table.setRows(node.getDataFlowNode().getFlow().size(), rowHeight);
        table.setTableData(createDataFields(node));

        // create the Graph
        graph = new DataflowGraph(table.getGraphArea(), node, NODE_RADIUS, LINE_LENGTH, rowHeight);
    }

    /**
     * @return the DataflowGraph
     */
    public DataflowGraph getGraph() {
        return graph;
    }

    /**
     * Creates an ArrayList (#Rows) of ArrayList (#Columns) with TableData in it, provides the Input for the Table
     * 
     * @param node
     * @return the DataflowGraphTable's Input-ArrayList
     */
    protected ArrayList createDataFields(SimpleNode node) {
        List flow = node.getDataFlowNode().getFlow();

        // the whole TableData
        ArrayList tableData = new ArrayList();

        for (int i = 0; i < flow.size(); i++) {
            // one Data-List for a Row
            ArrayList rowData = new ArrayList();

            // 1. The Nodes Line
            IDataFlowNode inode = (IDataFlowNode) flow.get(i);
            rowData.add(new DataflowGraphTableData(String.valueOf(inode.getLine()), SWT.CENTER));

            // 2. empty, because the Graph is shown in this Column
            rowData.add(null);

            // 3. the Numbers of the next Nodes
            String nextNodes = "";
            for (int j = 0; j < inode.getChildren().size(); j++) {
                IDataFlowNode n = (IDataFlowNode) inode.getChildren().get(j);
                if (j > 0)
                    nextNodes += ", ";
                nextNodes += String.valueOf(n.getIndex());
            }
            rowData.add(new DataflowGraphTableData(nextNodes, SWT.LEFT | SWT.WRAP));

            // 4. The Dataflow occurencies (definiton, undefinition,
            // reference) in this Line of Code
            List access = inode.getVariableAccess();
            if (access != null) {
                StringBuffer exp = new StringBuffer();
                for (int k = 0; k < access.size(); k++) {
                    if (k > 0)
                        exp.append(", ");
                    VariableAccess va = (VariableAccess) access.get(k);
                    switch (va.getAccessType()) {
                    case VariableAccess.DEFINITION:
                        exp.append("d(");
                        break;
                    case VariableAccess.REFERENCING:
                        exp.append("r(");
                        break;
                    case VariableAccess.UNDEFINITION:
                        exp.append("u(");
                        break;
                    default:
                        exp.append("?(");
                    }
                    exp.append(va.getVariableName() + ")");
                }
                rowData.add(new DataflowGraphTableData(exp.toString(), SWT.LEFT | SWT.WRAP));
            } else {
                rowData.add(null);
            }

            // 5. The Line of Code itself
            if (resourceString != null) {
                String codeLine = getCodeLine(resourceString, inode.getLine());
                rowData.add(new DataflowGraphTableData(codeLine.trim(), SWT.LEFT | SWT.WRAP));
            } else {
                rowData.add(null);
            }

            // add the row to the TableData
            tableData.add(rowData);
        }

        return tableData;
    }

    /**
     * Simply returns the given Line from the String
     * 
     * @param code, in general a Text representing a Java-File
     * @param line, the Line of Code to return
     * @return the Line of Code or null, if not found
     */
    protected String getCodeLine(String code, int line) {
        try {
            LineNumberReader reader = new LineNumberReader(new StringReader(code));
            String retString;

            // read the Code (File) line-wise
            while (reader.ready()) {
                retString = reader.readLine();
                // when the line is reached
                // return the read String
                if (reader.getLineNumber() == line) {
                    return retString;
                }
            }
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        }

        return null;
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDUiPlugin.getDefault().getStringTable().getString(key);
    }
}
