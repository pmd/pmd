package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.TypeProperty;
import net.sourceforge.pmd.util.ClassUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class TypeEditorFactory extends AbstractEditorFactory {

	public static final TypeEditorFactory instance = new TypeEditorFactory();

	private TypeEditorFactory() { }

	public static Class typeFor(String typeName) {
	        
	    Class newType = ClassUtil.getTypeFor(typeName);    // try for well-known types first
	    if (newType != null) return newType;

	    try {
	        return Class.forName(typeName);
	        } catch (ClassNotFoundException e) {
	           return null;
	        }
	}
	   
	protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
		
		Class type = (Class)rule.getProperty(desc);
		textWidget.setText(type == null ? "" : ClassUtil.asShortestName(type));
	}
	
	private Class currentType(Text textWidget) {
	    
	    String typeName = textWidget.getText().trim();
	    if (typeName.length() == 0) return null;
	    
	    return typeFor(typeName);
	}
	
    /**
     * 
     * @param parent Composite
     * @param columnIndex int
     * @param desc PropertyDescriptor
     * @param rule Rule
     * @param listener ValueChangeListener
     * @return Control
     * @see net.sourceforge.pmd.ui.preferences.br.EditorFactory#newEditorOn(Composite, int, PropertyDescriptor, Rule)
     */
    public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
        
        if (columnIndex == 0) return addLabel(parent, desc);
        
        if (columnIndex == 1) {
            
            final Text text =  new Text(parent, SWT.SINGLE | SWT.BORDER);
            GridData gridData = new GridData();
            gridData.horizontalAlignment = SWT.FILL;
            gridData.grabExcessHorizontalSpace = true;
            text.setLayoutData(gridData);

            fillWidget(text, desc, rule);
            
            if (desc instanceof PropertyDescriptorWrapper) {
                
                final PropertyDescriptorWrapper descWrapper = (PropertyDescriptorWrapper)desc;
                
                text.addListener(SWT.FocusOut, new Listener() {
                    public void handleEvent(Event event) {
                        Class newValue = currentType(text);                    
                        Class existingValue = (Class)rule.getProperty(descWrapper);                
                        if (existingValue == newValue) return;             
                        
                        rule.setProperty(descWrapper, newValue);
                        fillWidget(text, desc, rule);       // redraw 
                        listener.changed(desc, newValue);
                    }
                });               
                
                return text;
            }
            
            final TypeProperty tp = (TypeProperty)desc; // TODO - really necessary?
            
            text.addListener(SWT.FocusOut, new Listener() {
                public void handleEvent(Event event) {
                    Class newValue = currentType(text);                    
                    Class existingValue = rule.getProperty(tp);                
                    if (existingValue == newValue) return;              
                    
                    rule.setProperty(tp, newValue);
                    fillWidget(text, desc, rule);     // redraw
                    listener.changed(desc, newValue);
                }
            });

            return text;
        }
        
        return null;
    }

}
