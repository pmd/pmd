package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public class SWTUtil {
    
    public static void releaseListeners(Control control, int listenerType) {
        Listener[] listeners = control.getListeners(listenerType);
        for (int i=0; i<listeners.length; i++) control.removeListener(listenerType, listeners[i]);
    }
    
    public static String[] labelsIn(Object[][] items, int columnIndex) {
        
        String[] labels = new String[items.length];
        for (int i=0; i<labels.length; i++) labels[i] = items[i][columnIndex].toString();
        return labels;
    }
}
