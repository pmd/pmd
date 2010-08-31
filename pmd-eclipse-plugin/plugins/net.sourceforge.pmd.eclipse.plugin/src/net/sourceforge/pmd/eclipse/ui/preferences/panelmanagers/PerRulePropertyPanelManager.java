package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.EditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.BooleanEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.CharacterEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.DoubleEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.EnumerationEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.FloatEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.IntegerEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiEnumerationEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiIntegerEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiMethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiStringEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiTypeEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.StringEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.TypeEditorFactory;
import net.sourceforge.pmd.lang.rule.XPathRule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 *
 * @author Brian Remedios
 */
public class PerRulePropertyPanelManager extends AbstractRulePanelManager implements SizeChangeListener {

    private FormArranger        formArranger;
    private Composite           composite;
    private ScrolledComposite   sComposite;
    private int                 widgetRowCount;
    private List<String>        unreferencedVariables;

    private static final int MaxWidgetHeight = 30;  // TODO derive this instead
    public static final String ID = "perRuleProperties";

    public static final Map<Class<?>, EditorFactory> editorFactoriesByPropertyType;

    static {
    	Map<Class<?>, EditorFactory> factoriesByPropertyType = new HashMap<Class<?>, EditorFactory>();

    	factoriesByPropertyType.put(Boolean.class,    BooleanEditorFactory.instance);
    	factoriesByPropertyType.put(String.class,     StringEditorFactory.instance);
    	factoriesByPropertyType.put(Integer.class,    IntegerEditorFactory.instance);
    	factoriesByPropertyType.put(Float.class,      FloatEditorFactory.instance);
    	factoriesByPropertyType.put(Double.class,     DoubleEditorFactory.instance);
    	factoriesByPropertyType.put(Object.class,     EnumerationEditorFactory.instance);
    	factoriesByPropertyType.put(Character.class,  CharacterEditorFactory.instance);

    	factoriesByPropertyType.put(Class.class,      TypeEditorFactory.instance);
    	factoriesByPropertyType.put(Class[].class,    MultiTypeEditorFactory.instance);
    	factoriesByPropertyType.put(Method.class,     MethodEditorFactory.instance);
    	factoriesByPropertyType.put(Method[].class,   MultiMethodEditorFactory.instance);
    	factoriesByPropertyType.put(String[].class,   MultiStringEditorFactory.instance);
    	factoriesByPropertyType.put(Integer[].class,  MultiIntegerEditorFactory.instance);
    	factoriesByPropertyType.put(Object[].class,   MultiEnumerationEditorFactory.instance);

        editorFactoriesByPropertyType = Collections.unmodifiableMap(factoriesByPropertyType);
    }

    public PerRulePropertyPanelManager(String theTitle, EditorUsageMode theMode, ValueChangeListener theListener) {
        super(ID, theTitle, theMode, theListener);
    }

    protected boolean canManageMultipleRules() { return false; }

    protected boolean canWorkWith(Rule rule) {
    	
 //  TODO     if (rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR)) return true;		won't work, need to tweak Rule implementation as map is empty
        
    	// alternate approach for now
    	for (PropertyDescriptor<?> desc : rule.getPropertyDescriptors()) {
    		if (desc.equals(XPathRule.XPATH_DESCRIPTOR)) return true;
    	}
    	
        return !Configuration.filteredPropertiesOf(rule).isEmpty();
    }

    protected void clearControls() {
        formArranger.clearChildren();
    }

    public void showControls(boolean flag) {

        clearControls();
    }

    public Control setupOn(Composite parent) {

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
        validate();

        if (widgetRowCount < 0) return;

        adjustMinimumHeight();
    }

    public boolean validate() {
        if (!super.validate()) return false;
        
        unreferencedVariables = formArranger.updateDeleteButtons();	// any unref'd vars are not real errors
        
        return true;
    }

    protected List<String> fieldWarnings() {

        if (unreferencedVariables == null || unreferencedVariables.isEmpty()) {
        	return Collections.emptyList();
        }
         
        List<String> warnings = new ArrayList<String>(2);
        warnings.add("Unreferences variables: " + unreferencedVariables.toArray(new String[unreferencedVariables.size()]));
        
        return warnings;
    }
}
