package net.sourceforge.pmd.eclipse.ui.preferences.br;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 * @author Brian Remedios
 */
public class RuleColumnDescriptor {

	private String             label;
	private int                alignment;
	private int                width;
	private RuleFieldAccessor  accessor;
	private boolean            isResizeable;
	private CellPainterBuilder painterBuilder;

	public static final RuleColumnDescriptor name 		  = new RuleColumnDescriptor("Name", SWT.LEFT, 210, RuleFieldAccessor.name, true);	
	public static final RuleColumnDescriptor ruleSetName  = new RuleColumnDescriptor("Rule set", SWT.LEFT, 160, RuleFieldAccessor.ruleSetName, true);
	public static final RuleColumnDescriptor priority	  = new RuleColumnDescriptor("Priority", SWT.RIGHT, 53, RuleFieldAccessor.priority, false);		
	public static final RuleColumnDescriptor priorityName = new RuleColumnDescriptor("Priority Name", SWT.LEFT, 80, RuleFieldAccessor.priorityName, true);
	public static final RuleColumnDescriptor since 		  = new RuleColumnDescriptor("Since", SWT.RIGHT, 46, RuleFieldAccessor.since, false);
	public static final RuleColumnDescriptor usesDFA 	  = new RuleColumnDescriptor("DFA", SWT.LEFT, 60, RuleFieldAccessor.usesDFA, false);	
	public static final RuleColumnDescriptor externalURL  = new RuleColumnDescriptor("URL", SWT.LEFT, 100, RuleFieldAccessor.url, true);
	public static final RuleColumnDescriptor properties   = new RuleColumnDescriptor("Properties", SWT.LEFT, 100, RuleFieldAccessor.properties, true);
	public static final RuleColumnDescriptor minLangVers  = new RuleColumnDescriptor("Min Ver", SWT.LEFT, 30, RuleFieldAccessor.minLanguageVersion, false);

	public static final RuleColumnDescriptor exampleCount      = new RuleColumnDescriptor("Examples", SWT.RIGHT, 20, RuleFieldAccessor.exampleCount, false);
	public static final RuleColumnDescriptor ruleType	       = new RuleColumnDescriptor("Type", 	SWT.LEFT, 20, RuleFieldAccessor.ruleType, false);
	public static final RuleColumnDescriptor filterExpression  = new RuleColumnDescriptor("V.Regex", SWT.RIGHT, 20, RuleFieldAccessor.violationRegex, true);
	public static final RuleColumnDescriptor violateXPath      = new RuleColumnDescriptor("Filter", SWT.RIGHT, 20, RuleFieldAccessor.violationXPath, true);
	
	/**
	 * @param theLabel String
	 * @param theAlignment int
	 * @param theWidth int
	 * @param theAccessor RuleFieldAccessor
	 * @param resizableFlag boolean
	 */
	public RuleColumnDescriptor(String theLabel, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag) {
		this(theLabel,theAlignment,theWidth,theAccessor,resizableFlag, null);
	}
	
	   /**
     * @param theLabel String
     * @param theAlignment int
     * @param theWidth int
     * @param theAccessor RuleFieldAccessor
     * @param resizableFlag boolean
     * @param thePainterBuilder CellPainterBuilder
     */
    public RuleColumnDescriptor(String theLabel, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, CellPainterBuilder thePainterBuilder) {
        label = theLabel;
        alignment = theAlignment;
        width = theWidth;
        accessor = theAccessor;
        isResizeable = resizableFlag;
        painterBuilder = thePainterBuilder;
    }
	
	/**
	 * @param parent Tree
	 * @return TreeColumn
	 */
	public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, final RuleSortListener sortListener) {
		TreeColumn tc = new TreeColumn(parent, alignment);
        tc.setText(label);
        tc.setWidth(width);
        tc.setResizable(isResizeable);
        
        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
             sortListener.sortBy(accessor);
            }
          });     
        
        if (painterBuilder != null) {
            painterBuilder.addPainterFor(tc.getParent(), columnIndex, accessor);
        }
        
        return tc;
	}
	
	/**
	 * @return String
	 */
	public String label() { return label; }
	
	/**
	 * @return RuleFieldAccessor
	 */
	public RuleFieldAccessor accessor() { return accessor; }
}
