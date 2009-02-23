package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.EditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.br.PMDPreferencePage;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author Brian Remedios
 */
public class FormArranger {

	private final Composite                     parent;
	private final Map<Class<?>, EditorFactory>	editorFactoriesByValueType;
	private final ValueChangeListener           changeListener;
	private final SizeChangeListener            sizeChangeListener;
	private Rule                                rule;
	private Control[][]                         widgets;
	
	/**
	 * Constructor for FormArranger.
	 * @param theParent Composite
	 * @param factories Map<Class,EditorFactory>
	 */
	public FormArranger(Composite theParent, Map<Class<?>, EditorFactory> factories, ValueChangeListener listener, SizeChangeListener sizeListener) {
		parent = theParent;
		editorFactoriesByValueType = factories;
		changeListener = listener;
		sizeChangeListener = sizeListener;
	}

	/**
	 * Method factoryFor.
	 * @param desc PropertyDescriptor
	 * @return EditorFactory
	 */
	private EditorFactory factoryFor(PropertyDescriptor<?> desc) {
		return editorFactoriesByValueType.get(desc.type());
	}

	public void clearChildren() {
		Control[] kids = parent.getChildren();
		for (Control kid : kids) 
		    kid.dispose();
        parent.pack();
        rule = null;
	}

	/**
	 * Method arrangeFor.
	 * @param theRule Rule
	 */
	public int arrangeFor(Rule theRule) {

	    if (rule == theRule) return -1;
	    
		clearChildren();
		
		rule = theRule;
		
		if (rule == null) return -1;
		
		Map<PropertyDescriptor<?>, Object> valuesByDescriptor = PMDPreferencePage.filteredPropertiesOf(rule);
		if (valuesByDescriptor.isEmpty()) return 0;
		
		PropertyDescriptor<?>[] orderedDescs = (PropertyDescriptor[])valuesByDescriptor.keySet().toArray(new PropertyDescriptor[valuesByDescriptor.size()]);
				
		int maxColumns = 2;
		int rowCount = 0;	// count up the actual rows with widgets needed, not all have editors yet
		for (PropertyDescriptor<?> desc: orderedDescs) {
			EditorFactory factory = factoryFor(desc);
			if (factory == null) {
			    System.out.println("No editor defined for: "  + desc);
			    continue;
			}
			int colsReqd = factory.columnsRequired();
			maxColumns = Math.max(maxColumns, colsReqd);
			rowCount++;
		}
			
        GridLayout layout = new GridLayout(maxColumns, false);
        layout.verticalSpacing = 2;
        layout.marginTop = 1;
        parent.setLayout(layout);
		
		widgets = new Control[rowCount][maxColumns];
		if (maxColumns < 1) return 0;
		
		int rowsAdded = 0;
		for (PropertyDescriptor<?> desc: orderedDescs) {
			if (addRowWidgets(factoryFor(desc), rowsAdded, desc)) rowsAdded++;
		}
				
		if (rowsAdded > 0) {
		    parent.pack();
			}
		
		return rowsAdded;
	}

	/**
	 * @param factory EditorFactory
	 * @param rowIndex int
	 * @param desc PropertyDescriptor
	 * @return boolean
	 */
	private boolean addRowWidgets(EditorFactory factory, int rowIndex, PropertyDescriptor<?> desc) {

		if (factory == null) return false;
		
		int columns = factory.columnsRequired();
		for (int i=0; i<columns; i++) {	// add all the labels & controls necessary on each row
			widgets[rowIndex][i] = factory.newEditorOn(parent, i, desc, rule, changeListener, sizeChangeListener);
		}
		
		return true;
	}
}
