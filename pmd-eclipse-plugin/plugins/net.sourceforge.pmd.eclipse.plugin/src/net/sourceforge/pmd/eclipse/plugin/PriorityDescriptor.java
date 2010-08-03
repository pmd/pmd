package net.sourceforge.pmd.eclipse.plugin;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.ShapeDescriptor;
import net.sourceforge.pmd.eclipse.ui.views.actions.AbstractPMDAction;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public class PriorityDescriptor {

	public final RulePriority priority;
	public String 			label;
	public String 			filterText;
	public String			iconId;
	public ShapeDescriptor 	shape;
	
	private static final RGB ProtoTransparentColour = new RGB(1,1,1);	// almost black
	
	public PriorityDescriptor(RulePriority thePriority, String theLabelKey, String theFilterTextKey, String theIconId, ShapeDescriptor theShape) {
		priority = thePriority;
		label =  AbstractPMDAction.getString(theLabelKey);
		filterText =  AbstractPMDAction.getString(theFilterTextKey);
		iconId = theIconId;
		shape = theShape;
	}
	
	public PriorityDescriptor(RulePriority thePriority, String theLabelKey, String theFilterTextKey, String theIconId, Util.shape theShape, RGB theColor, int theSize) {
		this(thePriority, theLabelKey, theFilterTextKey, theIconId, new ShapeDescriptor(theShape, theColor, theSize));
	}
	
	public ImageDescriptor getImageDescriptor() {
		 return PMDPlugin.getImageDescriptor(iconId);
	}
	
	public Image getImage(Display display) {
		
		return Util.newDrawnImage(
				display, 
				shape.size, 
				shape.size, 
				shape.shape, 
				ProtoTransparentColour, 
				shape.rgbColor	//fillColour
				);
	}
}
