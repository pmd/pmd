package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
	private String             imagePath;
	private int                alignment;
	private int                width;
	private RuleFieldAccessor  accessor;
	private boolean            isResizeable;
	private CellPainterBuilder painterBuilder;

	private static final RuleFieldAccessor ruleSetNameAcc = new BasicRuleFieldAccessor() {
           public Comparable<?> valueFor(Rule rule) {
               return PMDPreferencePage.ruleSetNameFrom(rule);
           }
       };
       
    private static final RuleFieldAccessor propertiesAcc = new BasicRuleFieldAccessor() {
            public Comparable<?> valueFor(Rule rule) {
               return PMDPreferencePage.propertyStringFrom(rule);
            }
      };
	
      // TODO externalize remaining title strings
	public static final RuleColumnDescriptor name 		  = new RuleColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_RULE_NAME, null, SWT.LEFT, 210, RuleFieldAccessor.name, true);	
	public static final RuleColumnDescriptor ruleSetName  = new RuleColumnDescriptor("Rule set", null, SWT.LEFT, 160, ruleSetNameAcc, true);
	public static final RuleColumnDescriptor priority	  = new RuleColumnDescriptor("Priority", null, SWT.RIGHT, 53, RuleFieldAccessor.priority, false);		
	public static final RuleColumnDescriptor priorityName = new RuleColumnDescriptor("Priority", null, SWT.LEFT, 80, RuleFieldAccessor.priorityName, true);
	public static final RuleColumnDescriptor since 		  = new RuleColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_SINCE, null, SWT.RIGHT, 46, RuleFieldAccessor.since, false);
	public static final RuleColumnDescriptor usesDFA 	  = new RuleColumnDescriptor("DFA", null, SWT.LEFT, 60, RuleFieldAccessor.usesDFA, false);	
	public static final RuleColumnDescriptor externalURL  = new RuleColumnDescriptor("URL", null, SWT.LEFT, 100, RuleFieldAccessor.url, true);
	public static final RuleColumnDescriptor properties   = new RuleColumnDescriptor("Properties", null, SWT.LEFT, 40, propertiesAcc, true);
	public static final RuleColumnDescriptor minLangVers  = new RuleColumnDescriptor("Min Ver", null, SWT.LEFT, 30, RuleFieldAccessor.minLanguageVersion, false);

	public static final RuleColumnDescriptor exampleCount      = new RuleColumnDescriptor("Examples", null, SWT.RIGHT, 20, RuleFieldAccessor.exampleCount, false);
	public static final RuleColumnDescriptor ruleType	       = new RuleColumnDescriptor("Type", 	null, SWT.LEFT, 20, RuleFieldAccessor.ruleType, false);
	public static final RuleColumnDescriptor filterExpression  = new RuleColumnDescriptor("Exclusion rule", null, SWT.LEFT, 20, RuleFieldAccessor.violationRegex, true, Util.regexBuilderFor(16, 16));
	public static final RuleColumnDescriptor violateXPath      = new RuleColumnDescriptor("Filter", null, SWT.RIGHT, 20, RuleFieldAccessor.violationXPath, true);
		
	private static String stringFor(String key) {
	    return PMDPlugin.getDefault().getStringTable().getString(key);
	}
	
	/**
	 * @param theLabel String
	 * @param theAlignment int
	 * @param theWidth int
	 * @param theAccessor RuleFieldAccessor
	 * @param resizableFlag boolean
	 */
	public RuleColumnDescriptor(String theLabel, String theImagePath, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag) {
		this(theLabel,theImagePath,theAlignment,theWidth,theAccessor,resizableFlag, null);
	}
	
	   /**
     * @param labelKey String
     * @param theAlignment int
     * @param theWidth int
     * @param theAccessor RuleFieldAccessor
     * @param resizableFlag boolean
     * @param thePainterBuilder CellPainterBuilder
     */
    public RuleColumnDescriptor(String labelKey, String theImagePath, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, CellPainterBuilder thePainterBuilder) {
        label = stringFor(labelKey);
        imagePath = theImagePath;
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
	public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, final RuleSortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
		TreeColumn tc = new TreeColumn(parent, alignment);
        tc.setText(label);
        tc.setWidth(width);
        tc.setResizable(isResizeable);
        if (imagePath != null) {
            tc.setImage(ResourceManager.imageFor(imagePath));
        }
        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
             sortListener.sortBy(accessor);
            }
          });     
        
        if (painterBuilder != null) {
            painterBuilder.addPainterFor(tc.getParent(), columnIndex, accessor, paintListeners);
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
	
	public Image imageFor(Object value) {
	    return null;       // TODO
	}
}
