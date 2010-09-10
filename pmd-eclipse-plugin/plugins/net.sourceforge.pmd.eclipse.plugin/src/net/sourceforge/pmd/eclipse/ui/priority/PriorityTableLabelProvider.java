package net.sourceforge.pmd.eclipse.ui.priority;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.ui.preferences.AbstractTableLabelProvider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;

/**
 * 
 * @author Brian Remedios
 */
public class PriorityTableLabelProvider extends AbstractTableLabelProvider {

	private final PriorityColumnDescriptor[] columns;
	
	public PriorityTableLabelProvider(PriorityColumnDescriptor[] theColumns) {
		columns = theColumns;
	}


	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		
		return columns[columnIndex].imageFor((RulePriority)element);
	}

	public String getColumnText(Object element, int columnIndex) {
		
		Object value = columns[columnIndex].valueFor((RulePriority)element);
		return value == null ? null : value.toString();
	}

	public void addColumnsTo(Table table) {
		
		for (PriorityColumnDescriptor desc : columns) {
			desc.buildTableColumn(table);
		}
	}
}
