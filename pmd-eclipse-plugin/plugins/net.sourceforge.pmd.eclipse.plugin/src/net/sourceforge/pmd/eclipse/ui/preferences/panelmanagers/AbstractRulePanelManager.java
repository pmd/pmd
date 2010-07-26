package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.TypeText;
import net.sourceforge.pmd.eclipse.util.ColourManager;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * Concrete subclasses can also be used as tab folders outside of a wizard dialog via the setupOn(Composite) method.
 *
 * @author Brian Remedios
 */
public abstract class AbstractRulePanelManager extends WizardPage implements RulePropertyManager {

    private TabItem                     tab;
    private String                      tabText;
    private boolean                     isActive;
    protected RuleSelection             rules;
    protected final ValueChangeListener changeListener;
    protected final EditorUsageMode		usageMode;

    protected static Color textColour;
    protected static Color errorColour;
    protected static Color disabledColour;
    protected static Color overridenColour;

    private static final String disabledTabText = "-------";

    public static final RGB overridenColourValues = new RGB(236, 236, 255);  // light blue

    public AbstractRulePanelManager(String theId, String theTitle, EditorUsageMode theMode, ValueChangeListener theListener) {
    	super(theId, theTitle, null);

        changeListener = theListener;
        usageMode = theMode;
    }

    public abstract Control setupOn(Composite panel);

    public EditorUsageMode mode() { return usageMode; }

    /**
     * For use by wizards only..
     */
    public void createControl(Composite panel) {

    	Control childPanel = setupOn(panel);

        setControl(childPanel);

        setPageComplete(true);
    }

    public void tab(TabItem theTab) {
        tab = theTab;
        tabText = theTab.getText();
    }

    public boolean isActive() {
        return isActive;
    }

    protected String asCleanString(String original) {
    	if (original == null) return "";
    	return original.trim();
    }

    public void manage(RuleSelection theRules) {

        rules = theRules;

        isActive =
             (rules.hasOneRule() && canWorkWith(rules.soleRule())) ||
             (rules.hasMultipleRules() && canManageMultipleRules());

        showControls(isActive);
        if (tab != null) tab.setText(isActive ? tabText : disabledTabText);

        if (isActive) {
            adapt();
        } else {
            clearControls();
        }

        updateUI();
    }

    protected void valueChanged(PropertyDescriptor<?> optionalDesc, Object newValue) {
        changeListener.changed(rules, optionalDesc, newValue);
        updateUI();
    }

    protected void updateUI() {
    	List<String> warnings = fieldWarnings();
    	List<String> errors = fieldErrors();
        if (tab != null) updateTabUI(warnings, errors);
        updateOverridenFields();
    }

    protected void updateOverridenFields() {

    }

    protected boolean canWorkWith(Rule rule) { return true; }   // override as necessary

    protected List<String> fieldErrors() {                          // override as necessary
        return Collections.emptyList();
    }

    protected List<String> fieldWarnings() {                        // override as necessary
        return Collections.emptyList();
    }

    public boolean validate() {
    	
    	List<String> warnings = fieldWarnings();
    	List<String> errors = fieldErrors();
    	
        if (tab != null) {
        	updateTabUI(warnings, errors);
        }
        
        String errorText = errors.isEmpty() ? null : StringUtil.asString(errors.toArray(), ", ");
        
        setErrorMessage(errorText);
        
    	setPageComplete(StringUtil.isEmpty(errorText));
        
        return errorText == null;
    }

    protected void updateTabUI(List<String> warnings, List<String> errors) {

        if (!isActive) {
            tab.setToolTipText("");
            tab.setImage(null);
            return;
        }

        boolean hasIssues = updateTab(errors, PMDUiConstants.ICON_ERROR);
        if (hasIssues) return;

        updateTab(fieldWarnings(), PMDUiConstants.ICON_WARN);
    }

    private boolean updateTab(List<String> issues, String iconName) {

        boolean hasIssues = !issues.isEmpty();

        tab.setImage(
                hasIssues ? ResourceManager.imageFor(iconName) : null
                );

        tab.setToolTipText(hasIssues ? issues.toString() : "");

        return hasIssues;
    }

    protected abstract boolean canManageMultipleRules();

    protected abstract void adapt();

    protected abstract void clearControls();

    public abstract void showControls(boolean flag);

    protected Rule soleRule() {
        return rules.soleRule();
    }

    protected void addTextListeners(final Text control, final StringProperty desc) {

        control.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                changed(desc, control.getText());
            }
        });
    }

    protected void initializeOn(Composite parent) {

        if (errorColour != null) return;

        ColourManager clrMgr = ColourManager.managerFor(parent.getDisplay());
        errorColour = clrMgr.colourFor(new RGB( 255, 0, 0 ));        // red
        textColour = clrMgr.colourFor(new RGB( 0, 0, 0 ));           // black
        disabledColour = clrMgr.colourFor(new RGB( 128, 128, 128));   // grey
        overridenColour = clrMgr.colourFor(overridenColourValues);
    }

//    protected void valueChanged() {
//    	 changeListener.changed(rules, null, "");
//    }

    /**
     * @param property StringProperty
     * @param newValue String
     */
    protected void changed(StringProperty property, String newValue) {

        if (rules == null) return;

        String cleanValue = newValue.trim();
        String existingValue = rules.commonStringValue(property);

        if (StringUtil.areSemanticEquals(existingValue, cleanValue)) return;

        rules.setValue(property, cleanValue);
        valueChanged(property, newValue);
    }

    protected void shutdown(Text control) {
        control.setText("");
        control.setEnabled(false);
    }

    protected void shutdown(StyledText control) {
        control.setText("");
        control.setEnabled(false);
    }

    protected void shutdown(Link control) {
        control.setText("");
        control.setEnabled(false);
    }

    protected void show(TypeText control, Class<?> type) {
    	control.setType(type);
//    	control.setEnabled(usageMode == EditorUsageMode.CreateNew);
    }

    protected void show(Button checkbox, boolean checked) {
    	checkbox.setSelection(checked);
//    	checkbox.setEnabled(usageMode == EditorUsageMode.CreateNew);
    }

    protected void show(Text control, String value) {
        control.setText(value == null ? "" : value);
//        control.setEnabled(usageMode == EditorUsageMode.CreateNew);
    }

    protected void show(StyledText control, String value) {
        control.setText(value == null ? "" : value);
//        control.setEnabled(usageMode == EditorUsageMode.CreateNew);
    }

    protected void show(Combo control, String value) {
    	
//    	control.setEnabled(usageMode == EditorUsageMode.CreateNew);
    	
    	if (StringUtil.isEmpty(value)) {
    		control.deselectAll();
    		return;
    	}

    	String[] choices = control.getItems();

    	int index = -1;
    	for (int i=0; i<choices.length; i++) {
    		if (StringUtil.areSemanticEquals(choices[i], value)) {
    			index = i;
    			break;
    		}
    	}

        if (index < 0) {
        	control.deselectAll();
        } else {
        	control.select(index);
        }
    }
    
    protected void show(CCombo control, String value) {
    	
//    	control.setEnabled(usageMode == EditorUsageMode.CreateNew);
    	
    	if (StringUtil.isEmpty(value)) {
    		control.deselectAll();
    		return;
    	}

    	String[] choices = control.getItems();

    	int index = -1;
    	for (int i=0; i<choices.length; i++) {
    		if (StringUtil.areSemanticEquals(choices[i], value)) {
    			index = i;
    			break;
    		}
    	}

        if (index < 0) {
        	control.deselectAll();
        } else {
        	control.select(index);
        }
    }

    protected void show(Link control, String value) {
        control.setText(value == null ? "" : value);
//        control.setEnabled(true);
    }

    /**
     * Method newTextField.
     * @param parent Composite
     * @return Text
     */
    protected Text newTextField(Composite parent) {

        return new Text(parent, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    }
}
