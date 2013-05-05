package net.sourceforge.pmd.eclipse.ui;


import net.sourceforge.pmd.eclipse.ui.preferences.br.SortListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueFormatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * @author Brian Remedios
 */
public class ItemColumnDescriptor<T extends Object, V extends Object> extends AbstractColumnDescriptor {
	
	private final ItemFieldAccessor<T, V> accessor;
	private final ValueFormatter[] formatters;
	
	public ItemColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, boolean resizableFlag, ItemFieldAccessor<T, V> theAccessor) {
		this(theId, labelKey, theAlignment, theWidth, resizableFlag, theAccessor, null);
	}
	
	public ItemColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, boolean resizableFlag, ItemFieldAccessor<T, V> theAccessor, ValueFormatter[] theFormatters) {
		super(theId, labelKey, theAlignment, theWidth, resizableFlag, null);
		
		accessor = theAccessor;
		formatters = theFormatters;
	}

    public T valueFor(V item) {
        return accessor.valueFor(item);
    }

    // TODO - provide preference value
    private int preferredFormatter() {
    	return formatters != null && formatters.length == 1 ? 0 : -1;
    }
    
    public String textFor(V item) {    	
    	return textFor(item, preferredFormatter());
    }
    
    public String textFor(V item, int formatterIndex) {
    	
    	T value = valueFor(item);
    	
    	return formatterIndex < 0 ?
    		value == null ? "" : String.valueOf(value) :
    		formatters[formatterIndex].format(value);
    }
    
    public Image imageFor(V item) {
        return accessor.imageFor(item);
    }

    public TableColumn buildTableColumn(Table parent, final SortListener sortListener) {

        TableColumn tc = super.buildTableColumn(parent);
      
        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
               sortListener.sortBy(accessor, e.widget);
            }
          });
        
        return tc;
    }
    
    public ValueFormatter[] formatters() {
        return formatters;
    }
}
