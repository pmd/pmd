package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Retains all values necessary to hydrate a table column for holding a set of rules. Invoke the buildTreeColumn()
 * method to constitute one. As descriptors they can be held as static items since they they're immutable.
 * 
 * @author Brian Remedios
 */
public abstract class AbstractRuleColumnDescriptor implements RuleColumnDescriptor {

    private String            label;
    private String			  tooltip;
    private int               alignment;
    private int               width;
    private RuleFieldAccessor accessor;
    private boolean           isResizable;

    protected AbstractRuleColumnDescriptor(String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag) {
        super();
        
        label = SWTUtil.stringFor(labelKey);
        tooltip = SWTUtil.tooltipFor(labelKey);
        alignment = theAlignment;
        width = theWidth;
        accessor = theAccessor;
        isResizable = resizableFlag;
    }
    
    protected TreeColumn buildTreeColumn(Tree parent, final RuleSortListener sortListener) {
        
        final TreeColumn tc = new TreeColumn(parent, alignment);
        tc.setWidth(width);
        tc.setResizable(isResizable);

        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
             sortListener.sortBy(accessor());
      //       tc.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_LABEL_ARRDN));
            }
          });  
        
        return tc;
    }
    
    protected Object valueFor(Rule rule) {
        return accessor.valueFor(rule);      
    }
    
    public String label() { return label; }
    
    public String tooltip() { return tooltip;  }
    
    public RuleFieldAccessor accessor() { return accessor; }
}