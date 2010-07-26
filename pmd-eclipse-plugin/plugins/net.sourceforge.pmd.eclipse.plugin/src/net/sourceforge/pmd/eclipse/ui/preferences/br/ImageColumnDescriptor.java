package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.util.FontBuilder;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
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
    
    private static final RuleFieldAccessor propertiesAcc = new BasicRuleFieldAccessor() {
    	
        public Comparable<?> valueFor(Rule rule) {
           return PMDPreferencePage2.indexedPropertyStringFrom(rule);
        }
    	public Comparable<?> valueFor(RuleCollection collection) {
    		return IndexedString.Empty;
//    		int count = RuleUtil.countNonOccurrencesOf(collection, this, IndexedString.Empty);
//    		if (count == 0) return IndexedString.Empty;
//    		return new IndexedString("(rules with properties: " + count + ")");
    	}
        
    };
    
    private static final FontBuilder blueBold11 = new FontBuilder("Tahoma", 11, SWT.BOLD, SWT.COLOR_BLUE);
    
    public static final RuleColumnDescriptor priority  			   = new ImageColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_PRIORITY, SWT.LEFT, 50, RuleFieldAccessor.priority, false, PMDUiConstants.ICON_BUTTON_DIAMOND_WHITE, Util.uniqueItemsAsColorShapeFor(12, 12, Util.shape.diamond, SWT.LEFT, PMDPlugin.colorsByPriority));
    public static final RuleColumnDescriptor filterViolationRegex  = new ImageColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_FILTERS_REGEX, SWT.LEFT, 25, RuleFieldAccessor.violationRegex, false, PMDUiConstants.ICON_FILTER_R, Util.textAsColorShapeFor(16, 16, Util.shape.square));    
    public static final RuleColumnDescriptor filterViolationXPath  = new ImageColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_FILTERS_XPATH, SWT.LEFT, 25, RuleFieldAccessor.violationXPath, false, PMDUiConstants.ICON_FILTER_X, Util.textAsColorShapeFor(16, 16, Util.shape.circle));
	public static final RuleColumnDescriptor properties   		   = new ImageColumnDescriptor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_PROPERTIES, 	SWT.LEFT, 40, propertiesAcc, false, PMDUiConstants.ICON_BUTTON_DIAMOND_WHITE, Util.styledTextBuilder(blueBold11));

	
    public ImageColumnDescriptor(String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, String theImagePath, CellPainterBuilder thePainterBuilder) {
        super(labelKey, theAlignment, theWidth, theAccessor, resizableFlag, theImagePath);

        painterBuilder = thePainterBuilder;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.preferences.br.IRuleColumnDescriptor#newTreeColumnFor(org.eclipse.swt.widgets.Tree, int, net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSortListener, java.util.Map)
     */
    public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, final RuleSortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
        TreeColumn tc = buildTreeColumn(parent, sortListener);

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
