package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * @author Brian Remedios
 */
public class PerRulePropertyPanelManager extends AbstractRulePanelManager {

    private Composite       composite;
    private FormArranger    formArranger;

    private static final Map<Class, EditorFactory> editorFactoriesByPropertyType;
    
    static {
        editorFactoriesByPropertyType = new HashMap<Class, EditorFactory>();        

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

    public Control setupOn(TabFolder parent, ValueChangeListener changeListener) {
                      
        composite = new Composite(parent, SWT.NONE);   

        formArranger = new FormArranger(composite, editorFactoriesByPropertyType, changeListener);
        
        return composite;
    }
    
    public void showRule(Rule rule) {
     
        currentRule = rule;
                       
        formArranger.arrangeFor(rule);
    }
}
