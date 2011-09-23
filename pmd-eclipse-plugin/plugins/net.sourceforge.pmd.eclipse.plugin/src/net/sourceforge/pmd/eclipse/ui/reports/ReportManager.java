package net.sourceforge.pmd.eclipse.ui.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

/**
 * 
 * @author Brian Remedios
 */
public class ReportManager {

	private final Renderer[] allRenderers;
	
	public static final ReportManager instance = new ReportManager();
	
	private ReportManager() { 
		allRenderers = knownRenderers();
	}
	
	public Renderer[] allRenderers() { return allRenderers; }
	
    private Renderer[] knownRenderers() {

    	Properties props =  new Properties();
    	
    	return new Renderer[] {
    		new HTMLRenderer(props),
    		new SummaryHTMLRenderer(props),
    		new CSVRenderer(props),
    		new XMLRenderer(props),
    		new TextRenderer(props),
    		new VBHTMLRenderer(props)
    		};
    }
    
//    public Renderer[] availableRenderers2() {
//
//    	List<Renderer> renderers = new ArrayList<Renderer>();
//    	
//    	for (String reportName : RendererFactory.REPORT_FORMAT_TO_RENDERER.keySet()) {
//    	    renderers.add(
//    	    	RendererFactory.createRenderer(reportName, new Properties())
//    	    	);
//    		}
//    	
//    	return renderers.toArray(new Renderer[renderers.size()]);
//    }

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
    
}
