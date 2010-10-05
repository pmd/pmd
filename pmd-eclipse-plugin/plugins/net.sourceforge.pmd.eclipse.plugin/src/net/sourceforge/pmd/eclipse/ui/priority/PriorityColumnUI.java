package net.sourceforge.pmd.eclipse.ui.priority;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.ui.ItemColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessorAdapter;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public interface PriorityColumnUI {

	
	ItemFieldAccessor<String, RulePriority> nameAcc = new ItemFieldAccessorAdapter<String, RulePriority>() {
		public String valueFor(RulePriority priority) {	return PriorityDescriptorCache.instance.descriptorFor(priority).label; }
	};
	
	ItemFieldAccessor<String, RulePriority> pmdNameAcc = new ItemFieldAccessorAdapter<String, RulePriority>() {
		public String valueFor(RulePriority priority) {	return priority.getName(); }
	};
	
	ItemFieldAccessor<String, RulePriority> descriptionAcc = new ItemFieldAccessorAdapter<String, RulePriority>() {
		public String valueFor(RulePriority priority) {	return PriorityDescriptorCache.instance.descriptorFor(priority).description;	}	
	};
	
	ItemFieldAccessor<Shape, RulePriority> shapeAcc = new ItemFieldAccessorAdapter<Shape, RulePriority>() {
		public Shape valueFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).shape.shape;	}
	};
	
	ItemFieldAccessor<RGB, RulePriority> colorAcc = new ItemFieldAccessorAdapter<RGB, RulePriority>() {
		public RGB valueFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).shape.rgbColor;	}	
	};
	
	ItemFieldAccessor<Integer, RulePriority> sizeAcc = new ItemFieldAccessorAdapter<Integer, RulePriority>() {
		public Integer valueFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).shape.size;	}	
	};
	
	ItemFieldAccessor<Integer, RulePriority> valueAcc = new ItemFieldAccessorAdapter<Integer, RulePriority>() {
		public Integer valueFor(RulePriority priority) { return priority.getPriority();	}	
	};
	
	ItemFieldAccessor<Image, RulePriority> imageAcc = new ItemFieldAccessorAdapter<Image, RulePriority>() {
		public Image imageFor(RulePriority priority) { return PriorityDescriptorCache.instance.descriptorFor(priority).getImage(Display.getCurrent());	}
	};
	
	ItemColumnDescriptor name   		= new ItemColumnDescriptor("", StringKeys.PRIORITY_COLUMN_NAME, SWT.LEFT, 25, true, nameAcc);
	ItemColumnDescriptor pmdName   		= new ItemColumnDescriptor("", StringKeys.PRIORITY_COLUMN_PMD_NAME, SWT.LEFT, 25, true, pmdNameAcc);
	ItemColumnDescriptor value  		= new ItemColumnDescriptor("", StringKeys.PRIORITY_COLUMN_VALUE, SWT.CENTER, 25, true, valueAcc);
//	PriorityColumnDescriptor size  		= new PriorityColumnDescriptor("", StringKeys.PRIORITY_COLUMN_SIZE, SWT.RIGHT, 25, true, sizeAcc);
	ItemColumnDescriptor image  		= new ItemColumnDescriptor("", StringKeys.PRIORITY_COLUMN_SHAPE, SWT.CENTER, 25, true, imageAcc);
//	PriorityColumnDescriptor color  		= new PriorityColumnDescriptor("", StringKeys.PRIORITY_COLUMN_COLOR, SWT.RIGHT, 25, true, colorAcc);
//	PriorityColumnDescriptor description	= new PriorityColumnDescriptor("", StringKeys.PRIORITY_COLUMN_DESC, SWT.LEFT, 25, true, descriptionAcc);
    
	ItemColumnDescriptor[] VisibleColumns = new ItemColumnDescriptor[] { image, value, name, pmdName }; //, description };

}
