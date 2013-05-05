package net.sourceforge.pmd.eclipse.ui.reports;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;

/**
 *
 * @author Brian Remedios
 */
public class ReportManager {

	private final Renderer[] allRenderers;

	public static final ReportManager instance = new ReportManager();


    public static String DefaultReportPropertyFilename = "reportProperties.xml";
    
	private ReportManager() {
		allRenderers = availableRenderers2();
	}

	public Renderer[] allRenderers() { return allRenderers; }

//    private Renderer[] knownRenderers() {
//
//    	Properties props =  new Properties();
//
//    	return new Renderer[] {
//    		new HTMLRenderer(props),
//    		new SummaryHTMLRenderer(props),
//    		new CSVRenderer(props),
//    		new XMLRenderer(props),
//    		new TextRenderer(props),
//    		new VBHTMLRenderer(props)
//    		};
//    }

    public Renderer[] availableRenderers2() {

    	List<Renderer> renderers = new ArrayList<Renderer>();

    	for (String reportName : RendererFactory.REPORT_FORMAT_TO_RENDERER.keySet()) {
    	    renderers.add(
    	    	RendererFactory.createRenderer(reportName, new Properties())
    	    	);
    		}

    	return renderers.toArray(new Renderer[renderers.size()]);
    }

    public List<Renderer> activeRenderers() {

    	List<Renderer> actives = new ArrayList<Renderer>();
    	IPreferences prefs =  PMDPlugin.getDefault().loadPreferences();

    	for (Renderer renderer : allRenderers) {
    		if (prefs.isActiveRenderer(renderer.getName())) actives.add(renderer);
    	}

    	return actives;
    }

	public static String asString(Map<String, String> propertyDefinitions) {

		if (propertyDefinitions.isEmpty()) return "";

		StringBuilder sb = new StringBuilder();
		String[] keys = propertyDefinitions.keySet().toArray(new String[propertyDefinitions.size()]);

		sb.append(keys[0]).append(": ").append(propertyDefinitions.get(keys[0]));

		for (int i=1; i<keys.length; i++) {
			sb.append(", ").append(keys[i]).append(": ").append(propertyDefinitions.get(keys[i]));
		}

		return sb.toString();
	}

	   /**
     * Derive a map key for the renderer, descriptor pair.
     * 
     * @param renderer Renderer
     * @param desc PropertyDescriptor<?>
     * @return String
     */
    private static String keyOf(Renderer renderer, PropertyDescriptor<?> desc) {
    	return renderer.getName() + "__" + desc.name();
    }

    public static void loadReportProperties() {
    	loadReportProperties(DefaultReportPropertyFilename);
    }
    
    public static void saveReportProperties() {
    	saveReportProperties(DefaultReportPropertyFilename);
    }
    
    /**
     * Load the properties for all renderers from the specified filename.
     * Return whether we succeeded or not.
     * 
     * @param propertyFilename String
     * @return boolean
     */
    private static boolean loadReportProperties(String propertyFilename) {

    	Properties props = new Properties();
    	FileInputStream fis = null;
    	try {
			fis = new FileInputStream(propertyFilename);
			props.loadFromXML(fis);
		} catch (Exception e) {
			return false;
		} finally{
		    try {
		        if (fis != null) {
		            fis.close();
		        }
		    } catch (Exception e) {
		        // ignored
		    }
		}

    	for (Renderer renderer : ReportManager.instance.allRenderers()) {

    		 for (PropertyDescriptor pDesc: renderer.getPropertyDescriptors()) {
    			String key = keyOf(renderer, pDesc);
    			if (props.containsKey(key)) {
    				Object value = pDesc.valueFrom((String)props.get(key));
    				renderer.setProperty(pDesc, value);
    				}
    		 }
    	}
    	
    	return true;
    }

    /**
     * Save the properties of all renderers to the specified filename.
     * 
     * @param propertyFilename String
     */
    private static void saveReportProperties(String propertyFilename) {

    	Properties props = new Properties();

    	for (Renderer renderer : ReportManager.instance.allRenderers()) {
    		 Map<PropertyDescriptor<?>, Object> valuesByProp = renderer.getPropertiesByPropertyDescriptor();
    		 for (Map.Entry<PropertyDescriptor<?>, Object> entry : valuesByProp.entrySet()) {
    			 PropertyDescriptor desc = entry.getKey();
    			 props.put(
    				 keyOf(renderer, desc),
    				 desc.asDelimitedString(entry.getValue())
    				 );

    		 }
    	}

    	FileOutputStream fos = null;

    	try {
			fos = new FileOutputStream(propertyFilename);
			props.storeToXML(fos, "asdf");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    try {
		        if (fos != null) {
		            fos.close();
		        }
		    } catch (Exception e) {
		        // ignored;
		    }
		}
    }
    
}
