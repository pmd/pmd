package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.TypeProperty;
import net.sourceforge.pmd.util.ClassUtil;

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
public class TypeEditorFactory extends AbstractEditorFactory {

	public static final TypeEditorFactory instance = new TypeEditorFactory();

	private TypeEditorFactory() { }

    public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {
        
        return new TypeProperty(
                name, 
                description,
                String.class, 
                new String[] { "java.lang" },
                0.0f
                );
    }
	
	public static Class<?> typeFor(String typeName) {
        
        Class<?> newType = ClassUtil.getTypeFor(typeName);    // try for well-known types first
        if (newType != null) return newType;

        try {
            return Class.forName(typeName);
            } catch (ClassNotFoundException e) {
               return null;
            }
    }
    	   
    protected Object valueFrom(Control valueControl) {        
        return ((TypeText)valueControl).getType(false);
    }
	
	protected void fillWidget(TypeText textWidget, PropertyDescriptor<?> desc, Rule rule) {
		
		Class<?> type = (Class<?>)valueFor(rule, desc);
		textWidget.setType(type);
	}
		
    private static TypeProperty typePropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (TypeProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (TypeProperty)desc;
        }
    }

    public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
        
         final TypeText typeText = new TypeText(parent, SWT.SINGLE | SWT.BORDER, true, "Enter a type name");  // TODO  i18l
         typeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         fillWidget(typeText, desc, rule);
                        
         final TypeProperty tp = typePropertyFrom(desc);
            
         Listener wereDoneListener = new Listener() {
             public void handleEvent(Event event) {
                 Class<?> newValue = typeText.getType(true);
                 if (newValue == null) return;
                     
                 Class<?> existingValue = (Class<?>)valueFor(rule, tp);                
                 if (existingValue == newValue) return;              
                     
                 rule.setProperty(tp, newValue);
                 listener.changed(rule, desc, newValue);

                 adjustRendering(rule, desc, typeText);
                 }
          	};
          	
        typeText.addListener(SWT.FocusOut, wereDoneListener);
        typeText.addListener(SWT.DefaultSelection, wereDoneListener);
        return typeText;
     }
        
}
