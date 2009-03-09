package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Brian Remedios
 */
public interface EditorFactory {
    
    /**
     * Have the factory create a descriptor using the name and description provided and use any
     * of the values in the otherData widgets provided by the createOtherControlsOn method called
     * earlier.
     * 
     * @param name
     * @param optionalDescription
     * @param otherData
     * @return PropertyDescriptor<?>
     */
    PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData);
        
    /**
     * Instantiate and return a label for the descriptor on the parent provided.
     * 
     * @param parent
     * @param desc
     * @return
     */
    Label addLabel(Composite parent, PropertyDescriptor<?> desc);
        
    /**
     * Creates a property value editor widget(s) on the parent for the specified descriptor
     * and rule. It does not perform any layout operations or set form attachments.
     * 
     * @param parent Composite
     * @param desc PropertyDescriptor
     * @param rule Rule
     * @param listener ValueChangeListener
     * @return Control
     */
    Control newEditorOn(Composite parent, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener, SizeChangeListener sizeListener);
        
    /**
     * Create an array of label-widget pairs on the parent composite for the 
     * type managed by the factory. In most cases this will just be a single 
     * widget that captures the default value. Numeric types may also provide
     * min/max limit widgets.
     * 
     * @param parent
     * @param desc
     * @param rule
     * @param listener
     * @param sizeListener
     * @return Control[]
     */
    Control[] createOtherControlsOn(Composite parent, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener, SizeChangeListener sizeListener);
}
