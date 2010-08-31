package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.ui.Shape;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public interface PriorityFieldAccessor<T extends Object> {

	T valueFor(RulePriority priority);
	
	Image imageFor(RulePriority priority);
	
	String labelFor(RulePriority priority);
	
	PriorityFieldAccessor<String> name = new PriorityFieldAccessorAdapter<String>() {
		public String valueFor(RulePriority priority) {	return PriorityDescriptorCache.instance.descriptorFor(priority).label;	}
	};
	
	PriorityFieldAccessor<String> description = new PriorityFieldAccessorAdapter<String>() {
		public String valueFor(RulePriority priority) {	return PriorityDescriptorCache.instance.descriptorFor(priority).description;	}	
	};
	
	PriorityFieldAccessor<Shape> shape = new PriorityFieldAccessorAdapter<Shape>() {
		public Shape valueFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).shape.shape;	}
	};
	
	PriorityFieldAccessor<RGB> color = new PriorityFieldAccessorAdapter<RGB>() {
		public RGB valueFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).shape.rgbColor;	}	
	};
	
	PriorityFieldAccessor<Integer> size = new PriorityFieldAccessorAdapter<Integer>() {
		public Integer valueFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).shape.size;	}	
	};
	
	PriorityFieldAccessor<Integer> value = new PriorityFieldAccessorAdapter<Integer>() {
		public Integer valueFor(RulePriority priority) { return priority.getPriority();	}	
	};
	
	PriorityFieldAccessor<Image> image = new PriorityFieldAccessorAdapter<Image>() {
		public Image imageFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).getImage(Display.getCurrent());	}
	};
}
