package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
/**
*
* @author Brian Remedios
*/
public class SimpleColumnDescriptor extends	AbstractRuleColumnDescriptor {

	public SimpleColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor,	boolean resizableFlag, String theImagePath) {
		super(theId, labelKey, theAlignment, theWidth, theAccessor,	resizableFlag, theImagePath);
	}

	/* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.preferences.br.IRuleColumnDescriptor#newTreeColumnFor(org.eclipse.swt.widgets.Tree, int, net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSortListener, java.util.Map)
     */
	public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, SortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
		TreeColumn tc = buildTreeColumn(parent, sortListener);
        tc.setText(label());
       
        return tc;
	}
	
	private String asString(Object value) {
		 if (value == null) return "";
		 if (value instanceof String) return value.toString();
		 ValueFormatter formatter = FormatManager.formatterFor(value.getClass());
	     return formatter == null ? value.toString() : formatter.format(value);
	}
	
	public String stringValueFor(Rule rule) {
	    Object value = valueFor(rule);
	    return asString(value);
	}

	public String stringValueFor(RuleCollection collection) {
	    Object value = valueFor(collection);
	    return asString(value);
	}
	
   public Image imageFor(Rule rule) {
       return null;
   }
}