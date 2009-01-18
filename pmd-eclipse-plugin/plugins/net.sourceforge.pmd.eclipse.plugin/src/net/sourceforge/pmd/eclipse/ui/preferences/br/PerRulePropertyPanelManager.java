package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.eclipse.ui.preferences.editors.BooleanEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.CharacterEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.DoubleEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.EnumerationEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.FloatEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.IntegerEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiIntegerEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiMethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiStringEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiTypeEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.StringEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.TypeEditorFactory2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * @author Brian Remedios
 */
public class PerRulePropertyPanelManager extends AbstractRulePanelManager implements SizeChangeListener {

    private FormArranger        formArranger;
    private Composite           composite;
    private ScrolledComposite   sComposite;
    private int                 widgetRowCount;
    
    private static final int MaxWidgetHeight = 30;  // TODO derive this instead
    
    private static final Map<Class<?>, EditorFactory> editorFactoriesByPropertyType;
    
    static {
        editorFactoriesByPropertyType = new HashMap<Class<?>, EditorFactory>();        

        editorFactoriesByPropertyType.put(Boolean.class,    BooleanEditorFactory.instance);
        editorFactoriesByPropertyType.put(String.class,     StringEditorFactory.instance);
        editorFactoriesByPropertyType.put(Integer.class,    IntegerEditorFactory.instance);
        editorFactoriesByPropertyType.put(Float.class,      FloatEditorFactory.instance);
        editorFactoriesByPropertyType.put(Double.class,     DoubleEditorFactory.instance);
        editorFactoriesByPropertyType.put(Object.class,     EnumerationEditorFactory.instance);
        editorFactoriesByPropertyType.put(Character.class,  CharacterEditorFactory.instance);
        
        editorFactoriesByPropertyType.put(Class.class,      TypeEditorFactory2.instance);
        editorFactoriesByPropertyType.put(Class[].class,    MultiTypeEditorFactory.instance);
        editorFactoriesByPropertyType.put(Method.class,     MethodEditorFactory.instance);
        editorFactoriesByPropertyType.put(Method[].class,   MultiMethodEditorFactory.instance);
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
                      
        sComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);        
        composite = new Composite(sComposite, SWT.NONE);   

        sComposite.setContent(composite);
        sComposite.setExpandHorizontal(true);
        sComposite.setExpandVertical(true);
        
        formArranger = new FormArranger(composite, editorFactoriesByPropertyType, changeListener, this);
        
        return sComposite;
    }
        
    public void addedRows(int rowCountDelta) {
        widgetRowCount += rowCountDelta;
        adjustMinimumHeight();
    }
    
    private void adjustMinimumHeight() {
        sComposite.setMinSize(composite.computeSize(500, widgetRowCount * MaxWidgetHeight));
    }
    
    protected void adapt() {
                            
        widgetRowCount = formArranger.arrangeFor(soleRule());
        if (widgetRowCount < 0) return;
        
        adjustMinimumHeight();
    }
}
