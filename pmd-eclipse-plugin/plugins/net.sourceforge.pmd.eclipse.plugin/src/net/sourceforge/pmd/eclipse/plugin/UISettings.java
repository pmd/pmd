package net.sourceforge.pmd.eclipse.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.ShapeDescriptor;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.nls.StringTable;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptor;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptorCache;
import net.sourceforge.pmd.eclipse.util.FontBuilder;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public class UISettings {

	private static String[] priorityLabels;

    private static Map<Object, ShapeDescriptor> shapesByPriority;
    private static Map<Integer, RulePriority> prioritiesByIntValue;
    private static Map<RulePriority, String> labelsByPriority = new HashMap<RulePriority, String>();
    
    private static final int MAX_MARKER_DIMENSION = 9;
    private static IPreferencesManager preferencesManager = PMDPlugin.getDefault().getPreferencesManager();
    private static final Map<RulePriority, PriorityDescriptor> uiDescriptorsByPriority = new HashMap<RulePriority, PriorityDescriptor>(5);


    public static final FontBuilder CodeFontBuilder = new FontBuilder("Courier", 11, SWT.NORMAL);
    
    public static void reloadPriorities() {
    	uiDescriptorsByPriority.clear();
    	uiDescriptorsByPriority();	// cause a reload
    }
    
    private static Map<RulePriority, PriorityDescriptor> uiDescriptorsByPriority() {
    	
    	if (uiDescriptorsByPriority.isEmpty()) {
    		IPreferences preferences = preferencesManager.loadPreferences();
            for (RulePriority rp : currentPriorities(true)) {
            	uiDescriptorsByPriority.put(rp, preferences.getPriorityDescriptor(rp));
            }
    	}
    	
    	return uiDescriptorsByPriority;
    }
       
    public static Shape[] allShapes() {
    	return new Shape[] { Shape.circle, Shape.star, Shape.domeLeft, Shape.domeRight, Shape.diamond, Shape.square, Shape.roundedRect, Shape.minus, Shape.pipe, Shape.plus, Shape.triangleUp, Shape.triangleDown, Shape.triangleRight, Shape.triangleLeft, Shape.triangleNorthEast, Shape.triangleSouthEast, Shape.triangleSouthWest, Shape.triangleNorthWest };
    }
    
    public static RulePriority[] currentPriorities(boolean sortAscending) {
    	
    	RulePriority[] priorities = RulePriority.values();
    	
    	Arrays.sort(priorities, new Comparator<RulePriority>() {
    		public int compare(RulePriority rpA, RulePriority rbB) {
    			return rpA.getPriority() - rbB.getPriority();
    			}
    		});	
    	return priorities;
    }     
        
    public static Map<Shape, ShapeDescriptor> shapeSet(RGB color, int size) {

    	Map<Shape, ShapeDescriptor> shapes = new HashMap<Shape, ShapeDescriptor>();

    	for(Shape shape : EnumSet.allOf(Shape.class)) {
    		shapes.put(shape, new ShapeDescriptor(shape, color, size));
    	}

    	return shapes;
    }
    
    public static String markerFilenameFor(RulePriority priority) {
    	String fileDir = PMDPlugin.getPluginFolder().getAbsolutePath();
    	return fileDir + "/" + relativeMarkerFilenameFor(priority);
    }
    
    public static String relativeMarkerFilenameFor(RulePriority priority) {
    	return "icons/markerP" + priority.getPriority() + ".png";
    }
    
    private static ImageDescriptor getImageDescriptor(final String	fileName) {
    	
    	URL installURL = PMDPlugin.getDefault().getBundle().getEntry("/");
    	try {
    		URL url = new URL(installURL, fileName);
    		return ImageDescriptor.createFromURL(url);
    	}
    	catch (MalformedURLException mue) {
    		mue.printStackTrace();
    		return null;
    	}
    }
    
    public static ImageDescriptor markerDescriptorFor(RulePriority priority) {
    	String path = relativeMarkerFilenameFor(priority);
    	return getImageDescriptor(path);
    }
    
    public static Map<Integer, ImageDescriptor> markerImgDescriptorsByPriority() {
    	
    	RulePriority[] priorities = currentPriorities(true);
    	Map<Integer, ImageDescriptor> overlaysByPriority = new HashMap<Integer, ImageDescriptor>(priorities.length);
		for (RulePriority priority : priorities) {
			overlaysByPriority.put(
				priority.getPriority(), 
				markerDescriptorFor(priority) 
				);
			}
		return overlaysByPriority;
    }
    
    public static void createRuleMarkerIcons(Display display) {
		    	
    	ImageLoader loader = new ImageLoader();
    	
    	PriorityDescriptorCache pdc = PriorityDescriptorCache.instance;
    	
    	for (RulePriority priority : currentPriorities(true)) {    		
    		Image image = pdc.descriptorFor(priority).getImage(display, MAX_MARKER_DIMENSION);
    	    loader.data = new ImageData[] { image.getImageData() };
    	    String fullPath = markerFilenameFor( priority );
    	    loader.save(fullPath, SWT.IMAGE_PNG);
    	    
    	    image.dispose();
    	}
    }
    
    private static String pLabelFor(RulePriority priority, boolean useCustom) {
    	
    	if (! useCustom) return priority.getName();
    	
    	String custom = descriptorFor(priority).label;
    	return StringUtil.isEmpty(custom) ?
    		preferencesManager.defaultDescriptorFor(priority).label :
    		custom;
    }
    
    public static void useCustomPriorityLabels(boolean flag) {
    	
    	labelsByPriority.clear();
    	
    	for (RulePriority priority : currentPriorities(true)) {  
    		labelsByPriority.put(
    			priority, 
    			pLabelFor(priority, flag)
    			);
    	}
    }
    
    public static String descriptionFor(RulePriority priority) {
    	return descriptorFor(priority).description;
    }
    
    public static PriorityDescriptor descriptorFor(RulePriority priority) {
    	return uiDescriptorsByPriority().get(priority);
    }
    
    public static String labelFor(RulePriority priority) {
    	if (labelsByPriority.isEmpty()) {
    		useCustomPriorityLabels(preferencesManager.loadPreferences().useCustomPriorityNames());
    	}
    	return labelsByPriority.get(priority);
    }
    
	public static Map<Object, ShapeDescriptor> shapesByPriority() {
		
		if (shapesByPriority != null) return shapesByPriority;
		
		 Map<Object, ShapeDescriptor> shapesByPriority = new HashMap<Object, ShapeDescriptor>(uiDescriptorsByPriority().size());
		 for (Map.Entry<RulePriority, PriorityDescriptor> entry : uiDescriptorsByPriority().entrySet()) {
			 shapesByPriority.put(entry.getKey(), entry.getValue().shape);
		 }
		 
		 return shapesByPriority;
	}
	
	public static RulePriority priorityFor(int value) {
		
		if (prioritiesByIntValue == null) {
			prioritiesByIntValue = new HashMap<Integer, RulePriority>(uiDescriptorsByPriority().size());
			for (Map.Entry<RulePriority, PriorityDescriptor> entry : uiDescriptorsByPriority().entrySet()) {
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
                stringTable.getString(StringKeys.PRIORITY_ERROR_HIGH),
                stringTable.getString(StringKeys.PRIORITY_ERROR),
                stringTable.getString(StringKeys.PRIORITY_WARNING_HIGH),
                stringTable.getString(StringKeys.PRIORITY_WARNING),
                stringTable.getString(StringKeys.PRIORITY_INFORMATION)
            };
        }

        return priorityLabels; // NOPMD by Herlin on 11/10/06 00:22
    }
    
    public static List<Integer> getPriorityIntValues() {
    	
    	List<Integer> values = new ArrayList<Integer>();
    	for (RulePriority priority : RulePriority.values()) {
    		values.add(priority.getPriority());
    	}
    	return values;
    }
}
