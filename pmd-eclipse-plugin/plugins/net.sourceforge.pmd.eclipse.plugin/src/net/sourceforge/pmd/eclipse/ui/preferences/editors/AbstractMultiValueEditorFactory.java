package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/** 
 * As a stateless factory it is responsible for building editors that manipulating value collections
 * without retaining references to the widgets or values themselves. All necessary references are
 * held in the event handlers and passed onto any new handlers created to manage values newly created
 * by the user - hence the monster method calls with umpteen arguments.
 * 
 * Concrete subclasses are responsible for instantiating the type-appropriate edit widgets, retrieving
 * their values, and updating the rule property. Provided you have widget capable of displaying/editing
 * your value type you can use this class as a base to bring up the appropriate widgets for the 
 * individual values.
 * 
 * The editor is held in a composite divided into three columns. In the collapsed mode, a text field
 * displaying the value collection occupies the first two cells with an expand/collapse button in the 
 * last cell. When the user clicks the button the row beneath is given a label, a type-specific edit
 * widget, and a control button for every value in the collection. The last row is empty and serves as
 * a place the user can enter additional values. When the user enters a value and clicks the control
 * button that row becomes read-only and a new empty row is added to the bottom. 
 * 
 * Note inclusion of the size and value changed callbacks used to let the parent composite resize itself 
 * and update the values in the rule listings respectively.
 * 
 * @author Brian Remedios
 */
public abstract class AbstractMultiValueEditorFactory extends AbstractEditorFactory {

    protected static final String delimiter = ",";
        
    private static final int WidgetsPerRow = 3;     //  numberLabel, valueWidget, +/-button
    
    protected AbstractMultiValueEditorFactory() {
    }

    protected abstract void configure(Text text, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener);
    
    protected abstract void setValue(Control widget, Object value);

    protected abstract void update(Rule rule, PropertyDescriptor<?> desc, List<Object> newValues);

    protected abstract Object addValueIn(Control widget, PropertyDescriptor<?> desc, Rule rule);
    
    protected abstract Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, Rule rule);
    
    /**
     * 
     * @param parent Composite
     * @param desc PropertyDescriptor
     * @param rule Rule
     * @param listener ValueChangeListener
     * @return Control
     * @see net.sourceforge.pmd.ui.preferences.br.EditorFactory#newEditorOn(Composite, PropertyDescriptor, Rule, ValueChangeListener, SizeChangeListener)
     */
    public Control newEditorOn(final Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener changeListener, final SizeChangeListener sizeListener) {
                
        final Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        panel.setLayout(layout);
            
        final Text textWidget = new Text(panel, SWT.SINGLE | SWT.BORDER);
        final Button butt = new Button(panel, SWT.BORDER);
        butt.setText("...");    // TODO use triangle icon & rotate 90deg when clicked
        butt.addListener(SWT.Selection, new Listener() {
           boolean itemsVisible = false;
           List<Control> items = new ArrayList<Control>();
           public void handleEvent(Event event) {
                if (itemsVisible) {
                    hideCollection(items);
                    sizeListener.addedRows(items.size() / -WidgetsPerRow);
                    } else {                            
                      items = openCollection(panel, desc, rule, textWidget, changeListener, sizeListener);
                      sizeListener.addedRows(items.size() / WidgetsPerRow);
                    }
                itemsVisible = !itemsVisible;
                textWidget.setEditable(!itemsVisible);   // no raw editing when individual items are available
                parent.layout();
             }
          });
         GridData data = new GridData(GridData.FILL_HORIZONTAL);
         data.horizontalSpan = 2;            
         textWidget.setLayoutData(data);
         panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                        
         fillWidget(textWidget, desc, rule);
         configure(textWidget, desc, rule, changeListener);

         return panel;
    }
    
    private void hideCollection(List<Control> controls) {
        for (Control control : controls) control.dispose();
    }
    
    private void delete(Control number, Control widget, Control button, List<Control> controlList, Object deleteValue, PropertyDescriptor<?> desc, Rule rule) {
        
        controlList.remove(number); number.dispose();
        controlList.remove(widget); widget.dispose();
        controlList.remove(button); button.dispose();
        renumberLabelsIn(controlList);
        
        Object[] values = (Object[])valueFor(rule, desc);
        List<Object> newValues = new ArrayList<Object>(values.length - 1);
        for (Object value : values) {
            if (value.equals(deleteValue)) continue;
            newValues.add(value);
        }
    
        update(rule, desc, newValues);
    }
        
    private List<Control> openCollection(final Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final Text textWidget, final ValueChangeListener changeListener, final SizeChangeListener sizeListener) {
        
        final List<Control> newControls = new ArrayList<Control>();
        
        int i=0;
        Object[] values = (Object[])valueFor(rule, desc);
        for (i=0; i<values.length; i++) {
            final Label number = new Label(parent, SWT.NONE);
            number.setText(Integer.toString(i+1));
            final Control widget = addWidget(parent, values[i], desc, rule);
            widget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            widget.setEnabled(false);
            final Button butt = new Button(parent, SWT.BORDER);
            butt.setText("-");  // TODO use icon for consistent width
            final Object value = values[i];
            butt.addListener(SWT.Selection, new Listener() {  // remove value handler            
                public void handleEvent(Event event) {
                   delete(number, widget, butt, newControls, value, desc, rule);        
                   fillWidget(textWidget, desc, rule);
                   sizeListener.addedRows(-1);
                   changeListener.changed(rule, desc, null);
                   parent.getParent().layout();
                }
            } );
            newControls.add(number); newControls.add(widget); newControls.add(butt);
        }
        
        addNewValueRow(parent, desc, rule, textWidget, changeListener, sizeListener, newControls, i);        
        
        return newControls;
    }

    /**
     * Override in subclasses as necessary
     * @param desc
     * @param rule
     * @return
     */
    protected boolean canAddNewRowFor(final PropertyDescriptor<?> desc, final Rule rule) {
    	return true;
    }
        
    private void addNewValueRow(final Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final Text parentWidget, final ValueChangeListener changeListener, final SizeChangeListener sizeListener, final List<Control> newControls, int i) {
        
    	if (!canAddNewRowFor(desc, rule)) return;
    	
        final Label number = new Label(parent, SWT.NONE);
        number.setText(Integer.toString(i+1));
        newControls.add(number);
        final Control widget = addWidget(parent, null, desc, rule);
        widget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        newControls.add(widget);
        final Button butt = new Button(parent, SWT.BORDER);
        butt.setText("+");  // TODO use icon for consistent width
        newControls.add(butt);
        Listener addListener = new Listener() {                
            public void handleEvent(Event event) {      // add new value handler
                // add the new value to the property set
                // set the value in the widget to the cleaned up one, disable it
                // remove old listener from button, add new (delete) one, update text/icon
                // add new row widgets: label, value widget, button
                // add listener for new button
               Object newValue = addValueIn(widget, desc, rule);
               if (newValue == null) return;
               
               addNewValueRow(parent, desc, rule, parentWidget, changeListener, sizeListener, newControls, -1);
               convertToDelete(butt, newValue, parent, newControls, desc, rule, parentWidget, number, widget, changeListener, sizeListener);
               widget.setEnabled(false);
               setValue(widget, newValue);
                 
               renumberLabelsIn(newControls);
               fillWidget(parentWidget, desc, rule);
               adjustRendering(rule, desc, parentWidget);
               sizeListener.addedRows(1);
               changeListener.changed(rule, desc, newValue);
               parent.getParent().layout();
            }
        };
        butt.addListener(SWT.Selection, addListener);
        widget.addListener(SWT.DefaultSelection, addListener);	// allow for CR on entry widgets themselves, no need to click the '+' button
        widget.setFocus();
    }
        
    private void convertToDelete(final Button button, final Object toDeleteValue, final Composite parent, final List<Control> newControls, final PropertyDescriptor<?> desc, final Rule rule, final Text parentWidget, final Label number, final Control widget, final ValueChangeListener changeListener, final SizeChangeListener sizeListener) {
        button.setText("-");
        Util.removeListeners(button,SWT.Selection);
        button.addListener(SWT.Selection, new Listener() {                
            public void handleEvent(Event event) {
                delete(number, widget, button, newControls, toDeleteValue, desc, rule);
                fillWidget(parentWidget, desc, rule);
                sizeListener.addedRows(-1);
                changeListener.changed(rule, desc, null);
                parent.getParent().layout();
             }
         } );
    }
        
    private static void renumberLabelsIn(List<Control> controls) {
        int i=1;
        for (Control control : controls) {
            if (control instanceof Label) {
                ((Label)control).setText(Integer.toString(i++));
            }
        }
    }
    
    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
        
        Object[] values = (Object[])valueFor(rule, desc);
        textWidget.setText(values == null ? "" : StringUtil.asString(values, delimiter + ' '));
        adjustRendering(rule, desc, textWidget);
    }
    
    protected String[] textWidgetValues(Text textWidget) {
        
        String values = textWidget.getText().trim();
        
        if (StringUtil.isEmpty(values)) return StringUtil.EMPTY_STRINGS;
        
        String[] valueSet = values.split(delimiter);
        List<String> valueList = new ArrayList<String>(valueSet.length);
        
        for (String value : valueSet) {
            String str = value.trim();
            if (str.length() > 0) valueList.add(str);
        }
        
        return (String[])valueList.toArray(new String[valueList.size()]);
    }
}
