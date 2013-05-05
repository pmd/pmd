package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.dialogs.NewPropertyDialog;
import net.sourceforge.pmd.eclipse.ui.preferences.br.EditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.factories.PropertyDescriptorUtil;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Takes in a property source instance, extracts its properties, creates a series of type-specific editors for each, and then populates 
 * them with the current values.  As some types can hold multiple values the vertical span can grow to accommodate additional widgets 
 * and does so by broadcasting this through the SizeChange listener.  The ValueChange listener can be used to update any outside UIs as 
 * necessary.
 *
 * @author Brian Remedios
 */
public class FormArranger implements ValueChangeListener {

	private final Composite                     parent;
	private final Map<Class<?>, EditorFactory>	editorFactoriesByValueType;
	private final ValueChangeListener           changeListener;
	private final SizeChangeListener            sizeChangeListener;
	private PropertySource                      propertySource;
	private Control[][]                         widgets;

    private Map<PropertyDescriptor<?>, Control[]> controlsByProperty;
    
    /**
     * Echo the change to the second listener after notifying the primary one
     * 
     * @param primaryListener
     * @param secondListener
     * @return
     */
    public static ValueChangeListener chain(final ValueChangeListener primaryListener, final ValueChangeListener secondaryListener) {
    	return new ValueChangeListener() {

			public void changed(RuleSelection rule, PropertyDescriptor<?> desc,	Object newValue) {
				primaryListener.changed(rule, desc, newValue);
				secondaryListener.changed(rule, desc, newValue);				
			}

			public void changed(PropertySource source, PropertyDescriptor<?> desc,	Object newValue) {
				primaryListener.changed(source, desc, newValue);
				secondaryListener.changed(source, desc, newValue);	
			}    		
    	};
    }
    
	/**
	 * Constructor for FormArranger.
	 * @param theParent Composite
	 * @param factories Map<Class,EditorFactory>
	 */
	public FormArranger(Composite theParent, Map<Class<?>, EditorFactory> factories, ValueChangeListener listener, SizeChangeListener sizeListener) {
		parent = theParent;
		editorFactoriesByValueType = factories;
		changeListener = chain(listener, this);
		sizeChangeListener = sizeListener;

        controlsByProperty = new HashMap<PropertyDescriptor<?>, Control[]>();
	}

    protected void register(PropertyDescriptor<?> property, Control[] controls) {
    	controlsByProperty.put(property, controls);
    }
    
	/**
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
        propertySource = null;
	}

	/**
	 * @param theRule Rule
	 */
	public int arrangeFor(PropertySource theSource) {

	    if (propertySource == theSource) return -1;
	    return rearrangeFor(theSource);
	}

	public void loadValues() {
		rearrangeFor(propertySource);
	}
	
	private int rearrangeFor(PropertySource theSource) {

		clearChildren();

		propertySource = theSource;

		if (propertySource == null) return -1;

		Map<PropertyDescriptor<?>, Object> valuesByDescriptor = Configuration.filteredPropertiesOf(propertySource);

		if (valuesByDescriptor.isEmpty()) {
		    if (RuleUtil.isXPathRule(propertySource)) {
	            addAddButton();
	            parent.pack();
	            return 1;
	        }
		    return 0;
		}

		PropertyDescriptor<?>[] orderedDescs = valuesByDescriptor.keySet().toArray(new PropertyDescriptor[valuesByDescriptor.size()]);
		Arrays.sort(orderedDescs, PropertyDescriptorUtil.ComparatorByOrder);
		
		int rowCount = 0;	// count up the actual rows with widgets needed, not all have editors yet
		for (PropertyDescriptor<?> desc: orderedDescs) {
			EditorFactory factory = factoryFor(desc);
			if (factory == null) {
			    System.out.println("No editor defined for: "  + desc.getClass().getSimpleName());
			    continue;
			}
			rowCount++;
		}

        boolean isXPathRule = RuleUtil.isXPathRule(propertySource);
        int columnCount = isXPathRule ? 3 : 2;  // xpath descriptors have a column of delete buttons

        GridLayout layout = new GridLayout(columnCount, false);
        layout.verticalSpacing = 2;
        layout.marginTop = 1;
        parent.setLayout(layout);

		widgets = new Control[rowCount][columnCount];

		int rowsAdded = 0;

		for (PropertyDescriptor<?> desc: orderedDescs) {
			if (addRowWidgets(
					factoryFor(desc), rowsAdded, desc, isXPathRule)
					) rowsAdded++;
		}

		if (RuleUtil.isXPathRule(propertySource)) {
		    addAddButton();
	        rowsAdded++;
		}

		if (rowsAdded > 0) {
		    parent.pack();
			}

		adjustEnabledStates();
		
		return rowsAdded;
	}

    private void addAddButton() {

        Button button = new Button(parent, SWT.PUSH);
	    button.setText("Add new...");
	    button.addSelectionListener( new SelectionListener(){
            public void widgetDefaultSelected(SelectionEvent e) {  }
            public void widgetSelected(SelectionEvent e) {
                NewPropertyDialog dialog = new NewPropertyDialog(parent.getShell(), editorFactoriesByValueType, propertySource, changeListener);
                if (dialog.open() == Window.OK) {
                    PropertyDescriptor<?> desc = dialog.descriptor();
                    propertySource.definePropertyDescriptor(desc);
                    rearrangeFor(propertySource);
                    }
                }
	        });
    }

	/**
	 * @param factory EditorFactory
	 * @param rowIndex int
	 * @param desc PropertyDescriptor
	 * @return boolean
	 */
	private boolean addRowWidgets(EditorFactory factory, int rowIndex, PropertyDescriptor<?> desc, boolean isXPathRule) {

		if (factory == null) return false;

		// add all the labels & controls necessary on each row
		widgets[rowIndex][0] = factory.addLabel(parent, desc);
		widgets[rowIndex][1] = factory.newEditorOn(parent, desc, propertySource, changeListener, sizeChangeListener);

		if (isXPathRule) {
		    widgets[rowIndex][2] = addDeleteButton(parent, desc,  propertySource, sizeChangeListener);
		}

		register(desc, widgets[rowIndex]);
		
		return true;
	}

    private Control addDeleteButton(Composite parent, final PropertyDescriptor<?> desc, final PropertySource source, final SizeChangeListener sizeChangeListener) {

        Button button = new Button(parent, SWT.PUSH);
        button.setData(desc.name());    // for later reference
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_DELETE));

        button.addSelectionListener( new SelectionListener(){
            public void widgetDefaultSelected(SelectionEvent e) {  }
            public void widgetSelected(SelectionEvent e) {
      //          rule.undefine(desc);
                rearrangeFor(source);
                updateDeleteButtons();
      //          sizeChangeListener.addedRows(-1);     not necessary apres rearrange?
            }});

        return button;
    }

    /**
     * Flag the delete buttons linked to property variables that are not referenced in the
     * Xpath source or clear any images they may have. Returns the names of any unreferenced
     * variables are found;
     */
    public List<String> updateDeleteButtons() {

        if (propertySource == null || !RuleUtil.isXPathRule(propertySource)) {
            return Collections.emptyList();
        }

        String source = propertySource.getProperty(XPathRule.XPATH_DESCRIPTOR);
        List<int[]> refPositions = Util.referencedNamePositionsIn(source, '$');
        if (refPositions.isEmpty()) return Collections.emptyList();

        List<String> unreferencedOnes = new ArrayList<String>(refPositions.size());
        List<String> varNames = Util.fragmentsWithin(source, refPositions);

        for (Control[] widgetRow : widgets)  {
            Button butt = (Button)widgetRow[2];
            String buttonName = (String)butt.getData();
            boolean isReferenced = varNames.contains(buttonName);

            butt.setToolTipText(
                isReferenced ?
                		"Delete variable: $" + buttonName :
                		"Delete unreferenced variable: $" + buttonName
                );
            if (!isReferenced) unreferencedOnes.add((String) butt.getData());
            }

        return unreferencedOnes;
     }
	
    private void adjustEnabledStates() {
    	
    	Set<PropertyDescriptor<?>> ignoreds = propertySource.ignoredProperties();
		
		for (Map.Entry<PropertyDescriptor<?>, Control[]> entry : controlsByProperty.entrySet()) {
			if (ignoreds.contains( entry.getKey() )) {
				SWTUtil.setEnabled(entry.getValue(), false);
			} else {
				SWTUtil.setEnabled(entry.getValue(), true);
			}
		}
    }
    
	public void changed(RuleSelection rule, PropertyDescriptor<?> desc,	Object newValue) {	}

	public void changed(PropertySource source, PropertyDescriptor<?> desc, Object newValue) {
		
		adjustEnabledStates();
	};
}
