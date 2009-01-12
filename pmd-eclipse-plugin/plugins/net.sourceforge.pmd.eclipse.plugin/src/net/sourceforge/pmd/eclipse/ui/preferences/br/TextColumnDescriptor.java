package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 * @author Brian Remedios
 */
public class TextColumnDescriptor extends AbstractRuleColumnDescriptor {

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
	public static final RuleColumnDescriptor name 		  = new TextColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_RULE_NAME, SWT.LEFT, 210, RuleFieldAccessor.name, true);	
	public static final RuleColumnDescriptor ruleSetName  = new TextColumnDescriptor("Rule set", SWT.LEFT, 160, ruleSetNameAcc, true);
	public static final RuleColumnDescriptor priority	  = new TextColumnDescriptor("Priority", SWT.RIGHT, 53, RuleFieldAccessor.priority, false);		
	public static final RuleColumnDescriptor priorityName = new TextColumnDescriptor("Priority", SWT.LEFT, 80, RuleFieldAccessor.priorityName, true);
	public static final RuleColumnDescriptor since 		  = new TextColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_SINCE, SWT.RIGHT, 46, RuleFieldAccessor.since, false);
	public static final RuleColumnDescriptor usesDFA 	  = new TextColumnDescriptor("DFA", SWT.LEFT, 60, RuleFieldAccessor.usesDFA, false);	
	public static final RuleColumnDescriptor externalURL  = new TextColumnDescriptor("URL", SWT.LEFT, 100, RuleFieldAccessor.url, true);
	public static final RuleColumnDescriptor properties   = new TextColumnDescriptor("Properties", SWT.LEFT, 40, propertiesAcc, true);
	public static final RuleColumnDescriptor minLangVers  = new TextColumnDescriptor("Min Ver", SWT.LEFT, 30, RuleFieldAccessor.minLanguageVersion, false);

	public static final RuleColumnDescriptor exampleCount = new TextColumnDescriptor("Examples", SWT.RIGHT, 20, RuleFieldAccessor.exampleCount, false);
	public static final RuleColumnDescriptor ruleType	  = new TextColumnDescriptor("Type", 	SWT.LEFT, 20, RuleFieldAccessor.ruleType, false);
	public static final RuleColumnDescriptor violateXPath = new TextColumnDescriptor("Filter", SWT.RIGHT, 20, RuleFieldAccessor.violationXPath, true);
			
	/**
	 * @param theLabel String
	 * @param theAlignment int
	 * @param theWidth int
	 * @param theAccessor RuleFieldAccessor
	 * @param resizableFlag boolean
	 */
	public TextColumnDescriptor(String theLabel, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag) {
		super(theLabel, theAlignment,theWidth,theAccessor,resizableFlag);
	}
	
	/* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.preferences.br.IRuleColumnDescriptor#newTreeColumnFor(org.eclipse.swt.widgets.Tree, int, net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSortListener, java.util.Map)
     */
	public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, RuleSortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
		TreeColumn tc = buildTreeColumn(parent, sortListener);
        tc.setText(label());        
        return tc;
	}
	
	public String stringValueFor(Rule rule) {
	    Object value = valueFor(rule);
        return value == null ? "" : value.toString();       
	}
    
    public Image imageFor(Rule rule) {
        return null;
    }
}
