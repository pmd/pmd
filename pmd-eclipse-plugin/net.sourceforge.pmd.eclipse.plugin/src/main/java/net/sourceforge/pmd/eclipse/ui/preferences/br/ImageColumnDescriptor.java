package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.IndexedString;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 *
 * @author Brian Remedios
 */
public class ImageColumnDescriptor extends AbstractRuleColumnDescriptor {

    private final CellPainterBuilder painterBuilder;
    
    public static final RuleFieldAccessor propertiesAcc = new BasicRuleFieldAccessor() {
    	
        public Comparable<?> valueFor(Rule rule) {
           return RuleUIUtil.indexedPropertyStringFrom(rule);	// notes indices of non-default values in the string for emphasis during later rendering
        }
    	public Comparable<?> valueFor(RuleCollection collection) {
    		return IndexedString.Empty;
//    		int count = RuleUtil.countNonOccurrencesOf(collection, this, IndexedString.Empty);
//    		if (count == 0) return IndexedString.Empty;
//    		return new IndexedString("(rules with properties: " + count + ")");
    	}        
    };
	
    public ImageColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, String theImagePath, CellPainterBuilder thePainterBuilder) {
        super(theId, labelKey, theAlignment, theWidth, theAccessor, resizableFlag, theImagePath);

        painterBuilder = thePainterBuilder;
    }
	
    /**
     * @see net.sourceforge.pmd.eclipse.ui.preferences.br.IRuleColumnDescriptor#newTreeColumnFor(org.eclipse.swt.widgets.Tree, int, net.sourceforge.pmd.eclipse.ui.preferences.br.SortListener, java.util.Map)
     */
    public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, final SortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
        TreeColumn tc = buildTreeColumn(parent, sortListener);
        setLabelIfImageMissing(tc);
        if (painterBuilder != null) painterBuilder.addPainterFor(tc.getParent(), columnIndex, accessor(), paintListeners);
        return tc;
    }

    public String stringValueFor(Rule rule) {
        return "";
    }

    public Image imageFor(Rule rule) {
        return null;
    }
}
