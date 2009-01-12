package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public abstract class AbstractRuleColumnDescriptor implements RuleColumnDescriptor {

    private String            label;
    private int               alignment;
    private int               width;
    private RuleFieldAccessor accessor;
    private boolean           isResizable;

    protected AbstractRuleColumnDescriptor(String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag) {
        super();
        
        label = stringFor(labelKey);
        alignment = theAlignment;
        width = theWidth;
        accessor = theAccessor;
        isResizable = resizableFlag;
    }
    
    protected static String stringFor(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
    
    protected TreeColumn buildTreeColumn(Tree parent, final RuleSortListener sortListener) {
        
        TreeColumn tc = new TreeColumn(parent, alignment);
        tc.setWidth(width);
        tc.setResizable(isResizable);

        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
             sortListener.sortBy(accessor());
            }
          });  
        
        return tc;
    }
    
    protected Object valueFor(Rule rule) {
        return accessor.valueFor(rule);      
    }
    
    public String label() { return label; }
    
    public RuleFieldAccessor accessor() { return accessor; }
}