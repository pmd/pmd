package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.io.File;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
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
	
	private void setValue(PropertySource source, FileProperty desc, File value) {

	    if (!source.hasDescriptor(desc)) return;
	    source.setProperty(desc, value);
	}
	
	public static boolean areSemanticEquals(File fileA, File fileB) {
		if (fileA == fileB) return true;
		
		if (fileA == null && fileB != null) return false;
		if (fileA != null && fileB == null) return false;
		
		return fileA.equals(fileB);
	}
	
	protected void fillWidget(FilePicker fileWidget, PropertyDescriptor<?> desc, PropertySource source) {
		File val = (File)valueFor(source, desc);
		fileWidget.setFile(val == null ? null : val);
		adjustRendering(source, desc, fileWidget);
	}
	
	public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final PropertySource source, final ValueChangeListener listener, SizeChangeListener sizeListener) {
       
		final FilePicker picker =  new FilePicker(parent, SWT.SINGLE | SWT.BORDER, "Open", null);
        picker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fillWidget(picker, desc, source);

        final FileProperty fp = filePropertyFrom(desc); // TODO - really necessary?

        picker.addFocusOutListener(new Listener() {
            public void handleEvent(Event event) {
                File newValue = picker.getFile();
                File existingValue = (File)valueFor(source, fp);
                if (areSemanticEquals(existingValue, newValue)) return;

                setValue(source, fp, newValue);
                fillWidget(picker, desc, source);     // redraw
                listener.changed(source, desc, newValue);
            }
        });

        return picker;
    }

	@Override
	protected Object valueFrom(Control valueControl) {
		 return ((FilePicker)valueControl).getFile();
	}

}
