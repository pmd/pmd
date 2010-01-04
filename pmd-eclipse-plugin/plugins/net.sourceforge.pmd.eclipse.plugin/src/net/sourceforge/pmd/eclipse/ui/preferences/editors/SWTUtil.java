package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
/**
 * 
 * @author Brian Remedios
 */
public class SWTUtil {
    
	private static PMDPlugin plugin = PMDPlugin.getDefault();
	
	private static final String TOOLTIP_SUFFIX = ".tooltip";
	
	// TODO move this to to Collections utility
	public static Set<String> asStringSet(String input, char separator) {
		List<String> values = Arrays.asList(input.split(""+separator));
		return new HashSet<String>(values);
	}
	
	// TODO move this to to Collections utility
	public static String asString(Set<String> values, char separator) {
		
		String[] strings = values.toArray(new String[values.size()]);
		StringBuilder sb = new StringBuilder(strings[0]);

		for (int i=1; i<strings.length; i++) {
			sb.append(separator).append(strings[i]);
		}
		return sb.toString();
	}	
	
    public static String stringFor(String key) {
        return plugin.getStringTable().getString(key);
    } 
    
    public static String tooltipFor(String key) {
    	String ttKey = key + TOOLTIP_SUFFIX;
        String tooltip = stringFor(ttKey);
        return ttKey.equals(tooltip) ? stringFor(key) : tooltip;
    }
    
    public static void releaseListeners(Control control, int listenerType) {
        Listener[] listeners = control.getListeners(listenerType);
        for (int i=0; i<listeners.length; i++) control.removeListener(listenerType, listeners[i]);
    }
    
    public static String[] labelsIn(Object[][] items, int columnIndex) {
        
        String[] labels = new String[items.length];
        for (int i=0; i<labels.length; i++) labels[i] = items[i][columnIndex].toString();
        return labels;
    }
    
    public static String[] i18lLabelsIn(Object[][] items, int columnIndex) {
        
        String[] labels = labelsIn(items, columnIndex);
        String xlation = null;
        
        for (int i=0; i<labels.length; i++) {
        	xlation = stringFor(labels[i]);
        	labels[i] = xlation == null ? labels[i] : xlation;
        }
        return labels;
    }
}
