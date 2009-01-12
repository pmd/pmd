package net.sourceforge.pmd.eclipse.util;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Brian Remedios
 */
public class ResourceManager {


    private Map<String, Image> imagesByCode = new HashMap<String, Image>();
    
    private static ResourceManager instance = new ResourceManager();
    
    private ResourceManager() {}
    
    public static Image imageFor(String codePath) {
        
        if (instance.imagesByCode.containsKey(codePath)) {
            return instance.imagesByCode.get(codePath);
        }
        Image image= PMDPlugin.getImageDescriptor(codePath).createImage();
        instance.imagesByCode.put(codePath, image);
        return image;
    }
    
    public static void dispose() {
        
        for (Image image : instance.imagesByCode.values()) {
            image.dispose();
        }
    }
}
