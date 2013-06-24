package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.AbstractColumnDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Retains all values necessary to hydrate a table column for holding a set of rules. Invoke the buildTreeColumn()
 * method to constitute one. As descriptors they can be held as static items since they they're immutable.
 *
 * @author Brian Remedios
 */
public abstract class AbstractRuleColumnDescriptor extends AbstractColumnDescriptor implements RuleColumnDescriptor {

    private final RuleFieldAccessor accessor;

    protected AbstractRuleColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, String theImagePath) {
        super(theId, labelKey, theAlignment, theWidth, resizableFlag, theImagePath);

        accessor = theAccessor;
    }

    protected TreeColumn buildTreeColumn(Tree parent, final SortListener sortListener) {

        TreeColumn tc = super.buildTreeColumn(parent);

        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
               sortListener.sortBy(accessor(), e.widget);
            }
          });

        return tc;
    }

    protected TableColumn buildTableColumn(Table parent, final SortListener sortListener) {

        TableColumn tc = super.buildTableColumn(parent);

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

    protected Object valueFor(RuleCollection collection) {
        return accessor.valueFor(collection);
    }
    
    public RuleFieldAccessor accessor() { return accessor; }

    public String detailStringFor(Rule rule) {
    	return accessor.labelFor(rule);
    }
    
    public String detailStringFor(RuleGroup group) {
    	return "TODO in AbstractRuleColumnDescriptor";
    }
    
    public Image imageFor(RuleCollection collection) {
    	return null;	// override in subclasses
    }
    
    public String stringValueFor(RuleCollection collection) {
    	return null;	// override in subclasses
    }
    
	public TableColumn newTableColumnFor(Table parent, int columnIndex, SortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
		TableColumn tc = buildTableColumn(parent, sortListener);
        tc.setText(label());
       
        return tc;
	}
}