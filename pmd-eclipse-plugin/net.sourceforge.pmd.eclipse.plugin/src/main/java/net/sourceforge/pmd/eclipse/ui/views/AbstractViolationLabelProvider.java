package net.sourceforge.pmd.eclipse.ui.views;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.eclipse.plugin.UISettings;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractViolationLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static Map<Integer, Image> imagesByPriorityIndex;

	static {
		loadImageMap();
	}

	protected AbstractViolationLabelProvider() {
	}

	private static void loadImageMap() {

		Map<Integer, ImageDescriptor> map = UISettings.markerImgDescriptorsByPriority();

		imagesByPriorityIndex = new HashMap<Integer, Image>(map.size());

		for (Map.Entry<Integer, ImageDescriptor> entry : map.entrySet()) {
			imagesByPriorityIndex.put(
					entry.getKey(), 
					entry.getValue().createImage()
			);
		}
	}

	protected static Image getPriorityImage(int priority) {

		return imagesByPriorityIndex.get(priority);
	}
}
