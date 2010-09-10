package net.sourceforge.pmd.eclipse.ui.priority;

import java.util.EnumSet;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.ShapeDescriptor;
import net.sourceforge.pmd.eclipse.ui.ShapePainter;
import net.sourceforge.pmd.eclipse.ui.views.actions.AbstractPMDAction;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public class PriorityDescriptor implements Cloneable {

	public final RulePriority priority;
	public String 			label;
	public String			description;
	public String 			filterText;
	public String			iconId;
	public ShapeDescriptor 	shape;
	
	private static final RGB ProtoTransparentColour = new RGB(1,1,1);	// almost full black, unlikely to be used
	
	private static final char DELIMITER = '_';
	
	public static PriorityDescriptor from(String text) {
		
		String[] values = text.split(Character.toString(DELIMITER));
		if (values.length != 7) return null;
		
		RGB rgb = rgbFrom(values[5]);
		if (rgb == null) return null;
		
		return new PriorityDescriptor(
				RulePriority.valueOf(Integer.parseInt(values[0])),
				values[1],
				values[2],
				values[3],
				shapeFrom(values[4]),
				rgb,
				Integer.parseInt(values[6])
				);
	}
	
	private static Shape shapeFrom(String id) {
		int num = Integer.parseInt(id);
		for (Shape shape : EnumSet.allOf(Shape.class)) {
			if (shape.id == num) return shape;
		}
		return null;
	}
	
	private static RGB rgbFrom(String desc) {
		String[] clrs = desc.split(",");
		if (clrs.length != 3) return null;
		return new RGB(
				Integer.parseInt(clrs[0]),
				Integer.parseInt(clrs[1]),
				Integer.parseInt(clrs[2])
				);
	}
	
	private static void rgbOn(StringBuilder sb, RGB rgb) {
		sb.append(rgb.red).append(',');
		sb.append(rgb.green).append(',');
		sb.append(rgb.blue);
	}
	
	public PriorityDescriptor(RulePriority thePriority, String theLabelKey, String theFilterTextKey, String theIconId, ShapeDescriptor theShape) {
		priority = thePriority;
		label =  AbstractPMDAction.getString(theLabelKey);
		description = "--";		// TODO
		filterText =  AbstractPMDAction.getString(theFilterTextKey);
		iconId = theIconId;
		shape = theShape;
	}
	
	public PriorityDescriptor(RulePriority thePriority, String theLabelKey, String theFilterTextKey, String theIconId, Shape theShape, RGB theColor, int theSize) {
		this(thePriority, theLabelKey, theFilterTextKey, theIconId, new ShapeDescriptor(theShape, theColor, theSize));
	}
	
	private PriorityDescriptor(RulePriority thePriority) {
		priority = thePriority;
	}

	public String storeString() {
		StringBuilder sb = new StringBuilder();
		storeOn(sb);
		return sb.toString();
	}
	
	public boolean equals(Object other) {
		
		if (this == other) return true;
		if (other.getClass() != getClass()) return false;
		
		PriorityDescriptor otherOne = (PriorityDescriptor)other;
		
		return priority.equals(otherOne.priority) &&
			StringUtil.isSame(label, otherOne.label, false, false, false) &&
			shape.equals(otherOne.shape) &&
			StringUtil.isSame(description, otherOne.description, false, false, false) &&
			StringUtil.isSame(filterText, otherOne.filterText, false, false, false) &&
			StringUtil.isSame(iconId, otherOne.iconId, false, false, false);
	}
	
	public int hashCode() {
		return 
			priority.hashCode() ^ shape.hashCode() ^ 
			String.valueOf(label).hashCode() ^ 
			String.valueOf(description).hashCode() ^ 
			String.valueOf(iconId).hashCode();
	}
	
	public void storeOn(StringBuilder sb) {
		sb.append(priority.getPriority()).append(DELIMITER);
		sb.append(label).append(DELIMITER);
//		sb.append(description).append(DELIMITER);
		sb.append(filterText).append(DELIMITER);
		sb.append(iconId).append(DELIMITER);
		sb.append(shape.shape.id).append(DELIMITER);
		rgbOn(sb, shape.rgbColor); sb.append(DELIMITER);
		sb.append(shape.size).append(DELIMITER);
	}
	
	public ImageDescriptor getImageDescriptor() {
		 return PMDPlugin.getImageDescriptor(iconId);
	}
	
	public PriorityDescriptor clone() {
		
		PriorityDescriptor copy = new PriorityDescriptor(priority);
		copy.label = label;
		copy.description = description;
		copy.filterText = filterText;
		copy.iconId = iconId;
		copy.shape = shape.clone();
					
		return copy;
	}
	
	public Image getImage(Display display) {
		
		return ShapePainter.newDrawnImage(
				display, 
				shape.size, 
				shape.size, 
				shape.shape, 
				ProtoTransparentColour, 
				shape.rgbColor	//fillColour
				);
	}
	
	public Image getImage(Display display, int maxDimension) {
		
		return ShapePainter.newDrawnImage(
				display, 
				Math.min(shape.size, maxDimension),
				Math.min(shape.size, maxDimension),
				shape.shape, 
				ProtoTransparentColour, 
				shape.rgbColor	//fillColour
				);
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("RuleDescriptor: ");
		sb.append(priority).append(", ");
		sb.append(label).append(", ");
		sb.append(description).append(", ");
		sb.append(filterText).append(", ");
		sb.append(iconId).append(", ");
		sb.append(shape);
		return sb.toString();
	}
}
