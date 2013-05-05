package net.sourceforge.pmd.eclipse.ui.views.dataflow;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.util.IOUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Viewer for the DataFlowGraph, contains the DataflowGraphTable
 *
 * @author SebastianRaffel ( 30.05.2005 )
 */
public class DataflowGraphViewer extends Composite {

    private Node method;
    private String resourceString;
    private DataflowGraphTable table;
    private DataflowGraph graph;

    protected static final int NODE_RADIUS = 12;
    protected static final int LINE_LENGTH = 25;
    protected static final int ROW_HEIGHT = 2 * NODE_RADIUS + LINE_LENGTH;
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

    @Override
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

        // set Column-widths and header titles
        colWidths = new int[] { 50, 250, 70, 220, 300 };
        String[] headerTitles = { 
        		getString(StringKeys.VIEW_DATAFLOW_GRAPH_COLUMN_LINE),
                getString(StringKeys.VIEW_DATAFLOW_GRAPH_COLUMN_GRAPH),
                getString(StringKeys.VIEW_DATAFLOW_GRAPH_COLUMN_NEXT),
                getString(StringKeys.VIEW_DATAFLOW_GRAPH_COLUMN_VALUES),
                getString(StringKeys.VIEW_DATAFLOW_GRAPH_COLUMN_CODE) };
        dfaTable.setColumns(colWidths, headerTitles, 1);

        Display display = parent.getDisplay();
        // set the Colors
        bgColor = display.getSystemColor(SWT.COLOR_WHITE);	//new Color(null, 255, 255, 255);
        lineColor = new Color(null, 192, 192, 192);
        textColor = display.getSystemColor(SWT.COLOR_BLACK);	//new Color(null, 0, 0, 0);
        dfaTable.setColors(textColor, bgColor, lineColor);

        return dfaTable;
    }

    /**
     * Sets the data for this Viewer, gives the Table Data to show.
     *
     * @param node
     * @param resString the Node's Resource as String
     */
    public void setData(Node node, String resString) {
        if (method != null) {
            table.dispose();
            table = initTable(this, SWT.NONE);
            layout();
        }

        method = node;
        resourceString = resString;

        // set the Data for the Table
        table.setRows(node.getDataFlowNode().getFlow().size(), ROW_HEIGHT);
        table.setTableData(createDataFields(node));

        // create the Graph
        graph = new DataflowGraph(table.getGraphArea(), node, NODE_RADIUS, LINE_LENGTH, ROW_HEIGHT);
    }

    /**
     * @return the DataflowGraph
     */
    public DataflowGraph getGraph() {
        return graph;
    }

    private String nextNodeNumberStringFrom(DataFlowNode dfNode) {
    	
    	 List<DataFlowNode> dfNodes = dfNode.getChildren();
    	 if (dfNodes.isEmpty()) return "";
    	 
         StringBuilder sb = new StringBuilder( Integer.toString(dfNodes.get(0).getIndex()) );
	            
	     for (int j = 1; j < dfNodes.size(); j++) {
	        sb.append(", ").append(dfNodes.get(j).getIndex());
	        }
	     return sb.toString();
    }
    
	private String referenceStringFrom(DataFlowNode dfNode) {

		List<VariableAccess> access = dfNode.getVariableAccess();
		if (access == null)	return null;

		StringBuilder exp = new StringBuilder();
		for (int k = 0; k < access.size(); k++) {
			if (k > 0) {
				exp.append(", ");
			}
			VariableAccess va = access.get(k);
			switch (va.getAccessType()) {
				case VariableAccess.DEFINITION:		exp.append("d(");	break;
				case VariableAccess.REFERENCING:	exp.append("r(");	break;
				case VariableAccess.UNDEFINITION:	exp.append("u(");	break;
				default:							exp.append("?(");
				}
			exp.append(va.getVariableName()).append(')');
			}
		
		return exp.toString();
	}
    
    /**
     * Creates an List (#Rows) of List (#Columns) with TableData in it, provides the Input for the Table
     *
     * @param node
     * @return the DataflowGraphTable's Input-List
     */
    protected List<List<DataflowGraphTableData>> createDataFields(Node node) {
        List<DataFlowNode> flow = node.getDataFlowNode().getFlow();

        // the whole TableData
        List<List<DataflowGraphTableData>> tableData = new ArrayList<List<DataflowGraphTableData>>();

        for (DataFlowNode inode : flow) {
            // one Data-List for a row
            List<DataflowGraphTableData> rowData = new ArrayList<DataflowGraphTableData>();

            // 1. The Nodes Line
            rowData.add(new DataflowGraphTableData(String.valueOf(inode.getLine()), SWT.CENTER));

            // 2. empty, because the Graph is shown in this column
            rowData.add(null);

            // 3. the Numbers of the next Nodes           
            String cellContent = nextNodeNumberStringFrom(inode);
            rowData.add(new DataflowGraphTableData(cellContent, SWT.LEFT | SWT.WRAP));
            
            // 4. The Dataflow occurrences (definition, undefinition, reference) in this Line of Code
            cellContent = referenceStringFrom(inode);
            if (cellContent != null) {
                rowData.add(new DataflowGraphTableData(cellContent, SWT.LEFT | SWT.WRAP));
            } else {
                rowData.add(null);
            }

            // 5. The Line of Code itself
            if (resourceString != null) {
            	cellContent = getCodeLine(resourceString, inode.getLine()).trim();
                rowData.add(new DataflowGraphTableData(cellContent, SWT.LEFT | SWT.WRAP));
            } else {
                rowData.add(null);
            }

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
    	
    	LineNumberReader reader = null;
    	
        try {
            reader = new LineNumberReader(new StringReader(code));
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
            PMDPlugin.getDefault().logError(StringKeys.ERROR_IO_EXCEPTION + this.toString(), ioe);
        } finally {
        	IOUtil.closeQuietly(reader);
        }

        return null;
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
