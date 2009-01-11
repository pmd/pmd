package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * @author Brian Remedios
 */
public class PerRulePropertyPanelManager extends AbstractRulePanelManager {

    private FormArranger formArranger;

    private static final Map<Class<?>, EditorFactory> editorFactoriesByPropertyType;
    
    static {
        editorFactoriesByPropertyType = new HashMap<Class<?>, EditorFactory>();        

        editorFactoriesByPropertyType.put(Boolean.class,    BooleanEditorFactory.instance);
        editorFactoriesByPropertyType.put(String.class,     StringEditorFactory.instance);
        editorFactoriesByPropertyType.put(Integer.class,    IntegerEditorFactory.instance);
        editorFactoriesByPropertyType.put(Float.class,      RealNumberEditorFactory.instance);
        editorFactoriesByPropertyType.put(Double.class,     RealNumberEditorFactory.instance);
        editorFactoriesByPropertyType.put(Object.class,     EnumerationEditorFactory.instance);
        editorFactoriesByPropertyType.put(Character.class,  CharacterEditorFactory.instance);
        
        editorFactoriesByPropertyType.put(Class.class,      TypeEditorFactory.instance);
        editorFactoriesByPropertyType.put(Class[].class,    MultiTypeEditorFactory.instance);
        editorFactoriesByPropertyType.put(String[].class,   MultiStringEditorFactory.instance);
        editorFactoriesByPropertyType.put(Integer[].class,  MultiIntegerEditorFactory.instance);
    }
    
    public PerRulePropertyPanelManager(ValueChangeListener theListener) {
        super(theListener);
    }

    protected boolean canManageMultipleRules() { return false; }
    
    protected void clearControls() {
        formArranger.clearChildren();
    }
    
    public Control setupOn(TabFolder parent, ValueChangeListener changeListener) {
                      
        ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        
        Composite composite = new Composite(sc, SWT.NONE);   

        sc.setContent(composite);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(composite.computeSize(500, 250)); //TODO a best guess..could be made adaptive?
        
        formArranger = new FormArranger(composite, editorFactoriesByPropertyType, changeListener);
        
        return sc;
    }
    
    protected void adapt() {
                            
        formArranger.arrangeFor(soleRule());
    }
}
