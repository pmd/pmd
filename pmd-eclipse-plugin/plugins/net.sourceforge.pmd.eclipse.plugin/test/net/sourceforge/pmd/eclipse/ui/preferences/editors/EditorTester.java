package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.EditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.FormArranger;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.PerRulePropertyPanelManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import test.net.sourceforge.pmd.properties.NonRuleWithAllPropertyTypes;
/**
 * 
 * @author Brian Remedios
 */
public class EditorTester implements ValueChangeListener, SizeChangeListener {
    
	  // these are the ones we've tested, the others may work but might not make sense in the xpath source context...
    private static final Class<?>[] validEditorTypes = new Class[] { String.class, Integer.class, Boolean.class };
       
    public static Map<Class<?>, EditorFactory> withOnly(Map<Class<?>, EditorFactory> factoriesByType, Class<?>[] legalTypeKeys) {
        Map<Class<?>, EditorFactory> results = new HashMap<Class<?>, EditorFactory>(legalTypeKeys.length);
        
        for (Class<?> type : legalTypeKeys) {
            if (factoriesByType.containsKey(type)) {
                results.put(type, factoriesByType.get(type));
            }
        }
        return results;
    }

	public EditorTester() {
				
		Display d = new Display();
		Shell s = new Shell(d);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		s.setLayout(gl);
		s.setSize(850, 595);

		s.setText("Type Editor Tester");
		s.setLayout(gl);
		Composite gc = new Composite(s, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 4;
		gc.setLayoutData(gd);
		gd = new GridData();

		Composite c1 = new Composite(s, SWT.NO_FOCUS);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		c1.setLayoutData(gd);
		Composite c2 = new Composite(s, SWT.NO_FOCUS);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		c2.setLayoutData(gd);

		Composite c = new Composite(s, SWT.NO_FOCUS);		c.setLayout(new RowLayout());
		Button b1 = new Button(c, SWT.PUSH | SWT.BORDER);	b1.setText("OK");
		Button b2 = new Button(c, SWT.PUSH | SWT.BORDER);	b2.setText("Cancel");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		c.setLayoutData(gd);

		FormArranger formArranger = new FormArranger(gc, PerRulePropertyPanelManager.editorFactoriesByPropertyType, this, this);
		formArranger.arrangeFor(new NonRuleWithAllPropertyTypes());
		s.open();
		while (!s.isDisposed()) {
			if (!d.readAndDispatch())
				d.sleep();
		}
		d.dispose();
	}

	public static void main(String[] arg) {
		new EditorTester();
	}

	// ignore these callbacks
	public void changed(RuleSelection rule, PropertyDescriptor<?> desc,	Object newValue) {	}

	public void changed(Rule rule, PropertyDescriptor<?> desc, Object newValue) { }

	public void addedRows(int newRowCount) { }

}