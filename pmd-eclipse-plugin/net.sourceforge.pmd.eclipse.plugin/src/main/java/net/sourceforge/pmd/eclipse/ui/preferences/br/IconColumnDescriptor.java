package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.util.CollectionUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
/**
 *
 * @author Brian Remedios
 */
public class IconColumnDescriptor extends AbstractRuleColumnDescriptor {

	private Map<Object, Image> iconsByValue;

	private static final Map<Object, String> iconNamesByPriority = CollectionUtil.mapFrom(
			new Object[] { RulePriority.LOW, RulePriority.MEDIUM_LOW, RulePriority.MEDIUM, RulePriority.MEDIUM_HIGH, RulePriority.HIGH },
			new String[] {PMDUiConstants.ICON_BUTTON_PRIO5, PMDUiConstants.ICON_BUTTON_PRIO4, PMDUiConstants.ICON_BUTTON_PRIO3, PMDUiConstants.ICON_BUTTON_PRIO2, PMDUiConstants.ICON_BUTTON_PRIO1}
			);

	private static final Map<Object, String> iconNamesByBoolean = CollectionUtil.mapFrom(
			new Object[] { Boolean.TRUE, Boolean.FALSE },
			new String[] { PMDUiConstants.ICON_GREEN_CHECK, PMDUiConstants.ICON_EMPTY}
			);

	public static final RuleColumnDescriptor priority = new IconColumnDescriptor("iPriority", StringKeys.PREF_RULESET_COLUMN_PRIORITY, SWT.RIGHT, 53, RuleFieldAccessor.priority, true, PMDUiConstants.ICON_BUTTON_PRIO0, iconNamesByPriority);

	public IconColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, String theImagePath, Map<Object, String> imageNamesByValue) {
		super(theId, labelKey, theAlignment, theWidth, theAccessor, resizableFlag, theImagePath);

		iconsByValue = iconsFor(imageNamesByValue);
	}

	private static Map<Object, Image> iconsFor(Map<Object, String> imageNamesByValue) {

		Map<Object, Image> imagesByValue = new HashMap<Object, Image>(imageNamesByValue.size());
		for (Map.Entry<Object, String> entry : imageNamesByValue.entrySet()) {
			imagesByValue.put(entry.getKey(), ResourceManager.imageFor(entry.getValue()));
			}
		return imagesByValue;
	}

	public Image imageFor(Rule rule) {
		Object value = valueFor(rule);
		return iconsByValue.get(value);
	}

	public Image imageFor(RuleCollection collection) {
		Object value = valueFor(collection);
		return iconsByValue.get(value);
	}
	
	public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, SortListener sortListener,	Map<Integer, List<Listener>> paintListeners) {
		TreeColumn tc = buildTreeColumn(parent, sortListener);
		return tc;
	}

	public String stringValueFor(Rule rule) {
		return null;
	}

}
