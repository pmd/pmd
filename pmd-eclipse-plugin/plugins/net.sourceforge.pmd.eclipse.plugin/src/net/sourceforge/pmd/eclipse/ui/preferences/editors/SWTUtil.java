package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
/**
 *
 * @author Brian Remedios
 */
public class SWTUtil {

	private SWTUtil() {}

	private static PMDPlugin plugin = PMDPlugin.getDefault();

	private static final String TOOLTIP_SUFFIX = ".tooltip";

    public static void logInfo(String message) {
    	plugin.logInformation(message);
    }

    public static void logError(String message, Throwable error) {
    	plugin.logError(message, error);
    }    
    
	// TODO move this to to Collections utility
	public static Set<String> asStringSet(String input, char separator) {
		List<String> values = Arrays.asList(input.split(""+separator));
		return new HashSet<String>(values);
	}

	// TODO move this to to Collections utility
	public static String asString(Collection<String> values, char separator) {

		if (values == null || values.isEmpty()) return "";

		String[] strings = values.toArray(new String[values.size()]);
		StringBuilder sb = new StringBuilder(strings[0]);

		for (int i=1; i<strings.length; i++) {
			sb.append(separator).append(strings[i]);
		}
		return sb.toString();
	}

	public static void setEnabled(Control control, boolean flag) {
		if (control == null || control.isDisposed()) return;
		control.setEnabled(flag);
	}

	public static void setEnabled(Control[] controls, boolean state) {
		for (Control control : controls) {
			setEnabled(control, state);
		}
	}
	
	public static void setEnabled(Collection<Control> controls, boolean flag) {
		for (Control control : controls) setEnabled(control, flag);
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
        for (Listener listener : listeners) control.removeListener(listenerType, listener);
    }

    public static String[] labelsIn(Object[][] items, int columnIndex) {

        String[] labels = new String[items.length];
        for (int i=0; i<labels.length; i++) labels[i] = items[i][columnIndex].toString();
        return labels;
    }

    public static void deselectAll(Combo combo) {
    	int count = combo.getItems().length;
    	for (int i=0; i<count; i++) combo.deselect(i);
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
