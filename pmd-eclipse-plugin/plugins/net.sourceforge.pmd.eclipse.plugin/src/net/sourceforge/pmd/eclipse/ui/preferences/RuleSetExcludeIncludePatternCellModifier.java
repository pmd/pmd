package net.sourceforge.pmd.eclipse.ui.preferences;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

/**
 * This class allows the modifications of the element of the ruleset exclude/include
 * pattern tables of the PMD Preference page
 * 
 * @version $Revision$
 * 
 * $Log$
 */
public class RuleSetExcludeIncludePatternCellModifier implements ICellModifier {
	private final TableViewer tableViewer;

	/**
	 * Constructor
	 */
	public RuleSetExcludeIncludePatternCellModifier(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(Object, String)
	 */
	public boolean canModify(Object element, String property) {
		return property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_PATTERN);
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(Object, String)
	 */
	public Object getValue(Object element, String property) {
		Object result = null;
		if (element instanceof RuleSetExcludeIncludePattern) {
			RuleSetExcludeIncludePattern pattern = (RuleSetExcludeIncludePattern)element;
			if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_PATTERN)) {
				result = pattern.getPattern();
			}
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(Object, String, Object)
	 */
	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;

		if (item.getData() instanceof RuleSetExcludeIncludePattern) {
			RuleSetExcludeIncludePattern pattern = (RuleSetExcludeIncludePattern)item.getData();
			if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_PATTERN)) {
				pattern.setPattern((String)value);
				tableViewer.update(pattern, new String[] { PMDPreferencePage.PROPERTY_PATTERN });
				PMDPreferencePage.getActiveInstance().setModified(true);
			}
		}
	}
}
