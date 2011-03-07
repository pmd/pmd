package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.io.File;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.FileProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
*
* @author Brian Remedios
*/
public class FileEditorFactory extends AbstractEditorFactory {


	public static final FileEditorFactory instance = new FileEditorFactory();
	
	protected FileEditorFactory() {	}

    public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {

        return new FileProperty(
                name,
                description,
                new File(""),
                0.0f
                );
    }

	private static FileProperty filePropertyFrom(PropertyDescriptor<?> desc) {

	    if (desc instanceof PropertyDescriptorWrapper<?>) {
	       return (FileProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
	       } else {
	        return (FileProperty)desc;
	     }
	}
	
	private void setValue(Rule rule, FileProperty desc, File value) {

	    if (!rule.hasDescriptor(desc)) return;
	    rule.setProperty(desc, value);
	}
	
	public static boolean areSemanticEquals(File fileA, File fileB) {
		if (fileA == fileB) return true;
		
		if (fileA == null && fileB != null) return false;
		if (fileA != null && fileB == null) return false;
		
		return fileA.equals(fileB);
	}
	
	protected void fillWidget(FilePicker fileWidget, PropertyDescriptor<?> desc, Rule rule) {
		File val = (File)valueFor(rule, desc);
		fileWidget.setFile(val == null ? null : val);
		adjustRendering(rule, desc, fileWidget);
	}
	
	public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
       
		final FilePicker picker =  new FilePicker(parent, SWT.SINGLE | SWT.BORDER, "Open", null);
        picker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fillWidget(picker, desc, rule);

        final FileProperty fp = filePropertyFrom(desc); // TODO - really necessary?

        picker.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                File newValue = picker.getFile();
                File existingValue = (File)valueFor(rule, fp);
                if (areSemanticEquals(existingValue, newValue)) return;

                setValue(rule, fp, newValue);
                fillWidget(picker, desc, rule);     // redraw
                listener.changed(rule, desc, newValue);
            }
        });

        return picker;
    }

	@Override
	protected Object valueFrom(Control valueControl) {
		 return ((FilePicker)valueControl).getFile();
	}

}
