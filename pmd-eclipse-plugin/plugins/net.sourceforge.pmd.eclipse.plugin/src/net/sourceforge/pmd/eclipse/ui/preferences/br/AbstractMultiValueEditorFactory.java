package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractMultiValueEditorFactory extends AbstractEditorFactory {

    protected static final String delimiter = ",";
        
    protected AbstractMultiValueEditorFactory() {
    }

    protected abstract void configure(Text text, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener);
    
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
            
            Composite panel = new Composite(parent, SWT.NONE);
            panel.setLayout(new GridLayout(2, false));
            
            Text textWidget =  new Text(panel, SWT.SINGLE | SWT.BORDER);
  //          final Button butt =  new Button(panel, SWT.BORDER);
  //          butt.setText("...");
            
            textWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
            fillWidget(textWidget, desc, rule);
            configure(textWidget, desc, rule, listener);

            return panel;
        }
        
        return null;
    }
    
    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
        
        Object[] values = (Object[])rule.getProperty(desc);
        textWidget.setText(values == null ? "" : StringUtil.asString(values, delimiter + ' '));
    }
    
    protected String[] textWidgetValues(Text textWidget) {
        
        String values = textWidget.getText().trim();
        
        if (StringUtil.isEmpty(values)) return StringUtil.EMPTY_STRINGS;
        
        String[] valueSet = values.split(delimiter);
        List<String> valueList = new ArrayList<String>(valueSet.length);
        
        for (int i=0; i<valueSet.length; i++) {
            String str = valueSet[i].trim();
            if (str.length() > 0) valueList.add(str);
        }
        
        return (String[])valueList.toArray(new String[valueList.size()]);
    }
}
