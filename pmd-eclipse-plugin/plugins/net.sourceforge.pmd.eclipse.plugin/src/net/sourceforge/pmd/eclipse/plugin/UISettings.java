package net.sourceforge.pmd.eclipse.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.nls.StringTable;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.ShapeDescriptor;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.graphics.RGB;

/**
 * 
 * @author Brian Remedios
 */
public class UISettings {

	private static String[] priorityLabels;

    private static Map<Object, ShapeDescriptor> shapesByPriority;
    private static Map<Integer, RulePriority> prioritiesByIntValue;
    
    private static final Map<RulePriority, PriorityDescriptor> uiDescriptorsByPriority = new HashMap<RulePriority, PriorityDescriptor>(5);
    
    static {
    	uiDescriptorsByPriority.put(RulePriority.LOW, 			new PriorityDescriptor(RulePriority.LOW, 		StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_1, StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_1, PMDUiConstants.ICON_BUTTON_PRIO1, Util.shape.triangleSouthEast, new RGB( 0,0,255), 13) );	// blue
    	uiDescriptorsByPriority.put(RulePriority.MEDIUM_LOW, 	new PriorityDescriptor(RulePriority.MEDIUM_LOW, StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_2, StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_2,	PMDUiConstants.ICON_BUTTON_PRIO2, Util.shape.triangleDown, new RGB( 0,255,0), 13) );	// green
    	uiDescriptorsByPriority.put(RulePriority.MEDIUM, 		new PriorityDescriptor(RulePriority.MEDIUM, 	StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_3, StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_3,	PMDUiConstants.ICON_BUTTON_PRIO3, Util.shape.triangleUp, new RGB( 255,255,0), 13) );	// yellow
    	uiDescriptorsByPriority.put(RulePriority.MEDIUM_HIGH, 	new PriorityDescriptor(RulePriority.MEDIUM_HIGH,StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_4, StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_4, PMDUiConstants.ICON_BUTTON_PRIO4, Util.shape.triangleNorthEast, new RGB( 255,0,255), 13) );	// purple
    	uiDescriptorsByPriority.put(RulePriority.HIGH, 			new PriorityDescriptor(RulePriority.HIGH, 		StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_5, StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_5,	PMDUiConstants.ICON_BUTTON_PRIO5, Util.shape.diamond, new RGB( 255,0,0), 13) );	// red
    }
    
    public static RulePriority[] currentPriorities(boolean sortAscending) {
    	
    	RulePriority[] priorities = uiDescriptorsByPriority.keySet().toArray(new RulePriority[uiDescriptorsByPriority.size()]);
    	
    	Arrays.sort(priorities, new Comparator<RulePriority>() {
    		public int compare(RulePriority rpA, RulePriority rbB) {
    			return rpA.getPriority() - rbB.getPriority();
    			}
    		});	
    	return priorities;
    }    
    
    public static PriorityDescriptor descriptorFor(RulePriority priority) {
    	return uiDescriptorsByPriority.get(priority);
    }
    
	public static Map<Object, ShapeDescriptor> shapesByPriority() {
		
		if (shapesByPriority != null) return shapesByPriority;
		
		 Map<Object, ShapeDescriptor> shapesByPriority = new HashMap<Object, ShapeDescriptor>(uiDescriptorsByPriority.size());
		 for (Map.Entry<RulePriority, PriorityDescriptor> entry : uiDescriptorsByPriority.entrySet()) {
			 shapesByPriority.put(entry.getKey(), entry.getValue().shape);
		 }
		 
		 return shapesByPriority;
	}
	
	public static RulePriority priorityFor(int value) {
		
		if (prioritiesByIntValue == null) {
			prioritiesByIntValue = new HashMap<Integer, RulePriority>(uiDescriptorsByPriority.size());
			for (Map.Entry<RulePriority, PriorityDescriptor> entry : uiDescriptorsByPriority.entrySet()) {
				prioritiesByIntValue.put(entry.getKey().getPriority(), entry.getKey());
			 	}			 	
			}
		return prioritiesByIntValue.get(value);
	}
	
    /**
     * Return the priority labels
     * @deprecated - not referenced in the modern UI
     */
    public static String[] getPriorityLabels() {
        if (priorityLabels == null) {
            final StringTable stringTable = PMDPlugin.getDefault().getStringTable();
            priorityLabels = new String[]{
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_ERROR_HIGH),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_ERROR),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_WARNING_HIGH),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_WARNING),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_INFORMATION)
            };
        }

        return priorityLabels; // NOPMD by Herlin on 11/10/06 00:22
    }
    
    public static List<Integer> getPriorityIntValues() {
    	
    	List<Integer> values = new ArrayList<Integer>(uiDescriptorsByPriority.size());
    	for (RulePriority priority : uiDescriptorsByPriority.keySet()) {
    		values.add(priority.getPriority());
    	}
    	return values;
    }
}
