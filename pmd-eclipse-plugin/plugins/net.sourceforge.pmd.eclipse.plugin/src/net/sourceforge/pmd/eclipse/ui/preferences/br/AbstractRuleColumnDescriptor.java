package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.util.ResourceManager;

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

    private final String            label;
    private final String			tooltip;
    private final int               alignment;
    private final int               width;
    private final RuleFieldAccessor accessor;
    private final boolean           isResizable;
    private final String            imagePath;
    
    protected AbstractRuleColumnDescriptor(String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, String theImagePath) {
        super();
        
        label = SWTUtil.stringFor(labelKey);
        tooltip = SWTUtil.tooltipFor(labelKey);
        alignment = theAlignment;
        width = theWidth;
        accessor = theAccessor;
        isResizable = resizableFlag;
        imagePath = theImagePath;
    }
    
    protected TreeColumn buildTreeColumn(Tree parent, final RuleSortListener sortListener) {
        
        final TreeColumn tc = new TreeColumn(parent, alignment);
        tc.setWidth(width);
        tc.setResizable(isResizable);
        tc.setToolTipText(tooltip);       
        if (imagePath != null) tc.setImage(ResourceManager.imageFor(imagePath));
        
        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
               sortListener.sortBy(accessor(), e.widget);
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
    
    public String detailStringFor(Rule rule) {
    	return accessor.labelFor(rule);
    }
}