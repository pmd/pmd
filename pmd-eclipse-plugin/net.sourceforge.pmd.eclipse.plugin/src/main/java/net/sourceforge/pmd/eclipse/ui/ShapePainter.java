package net.sourceforge.pmd.eclipse.ui;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author br
 *
 */
public class ShapePainter {

	private ShapePainter() {}

	/** Provides a simple cache for the images. */
	private static Map<String, Image> shapes = new HashMap<String, Image>();

	/**
	 * Creates an image initialized with the transparent colour with the shape drawn within.
	 * It will return cached images to avoid creating new images (and using the limited GDI handles
	 * under Windows).
	 */
	public static Image newDrawnImage(Display display, int width, int height, Shape shape, RGB transparentColour, RGB fillColour) {
	    String key = width + "x" + height + " " + shape + " " + transparentColour + " " + fillColour;
	    if (shapes.containsKey(key)) {
	        return shapes.get(key);
	    }

		 Image image = new Image(display, width, height);
		 GC gc = new GC(image);

		 gc.setBackground(PMDPlugin.getDefault().colorFor(transparentColour));
		 gc.fillRectangle(0, 0, width, height);

		 gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		 gc.setBackground(PMDPlugin.getDefault().colorFor(fillColour));

		 drawShape(width-1, height-1, shape, gc, 0, 0, null);

		 ImageData data = image.getImageData();
		 int clrIndex = data.palette.getPixel(transparentColour);
		 data.transparentPixel = clrIndex;

		 Image newImage = new Image(display, data);
		 image.dispose();

		 gc.dispose();

		 shapes.put(key, newImage);
		 return newImage;
	}

    public static void disposeAll() {
        for (Image i : shapes.values()) {
            i.dispose();
        }
        shapes.clear();
    }
	
	public static void drawShape(int width, int height, Shape shapeId, GC gc, int x, int y, String optionalText) {

		// TODO implement the following shapes: star, pentagon, hexagon, octagon, doughnut
		
		switch (shapeId) {
		case square: {
			gc.fillRectangle(x, y, width, height);    // fill it
			gc.drawRectangle(x, y, width, height);    // then the border on top
			break;
		}
		case circle: {
			gc.fillArc(x, y, width, height, 0, 360*64);
			gc.drawArc(x, y, width, height, 0, 360*64);
			break;
		}
		case domeLeft: {
			gc.fillArc(x+width/4, y, width, height, 90, 180);
			gc.drawArc(x+width/4, y, width, height, 90, 180);
			int threeQuarters = width/2 + width/4;
			gc.drawLine(x + threeQuarters, y, x+threeQuarters, y+height);
			break;
		}
		case domeRight: {
			gc.fillArc(x-width/4, y, width, height, 270, 180);
			gc.drawArc(x-width/4, y, width, height, 270, 180);
			gc.drawLine(x + width/4, y, x+width/4, y+height);
			break;
		}
        case pipe: {
          	gc.fillRectangle(x + (width/4), y, width - (width/2), height);
           	gc.drawRectangle(x + (width/4), y, width - (width/2), height); 
           	break;
           	}
//        case plus: {
//            int xA = x + width/3;
//            int xB = x + ((width/3) * 2);
//            int yA = y + height/3;
//            int yB = y+ ((height/3) * 2);
//            int[] points = new int[] { xA,y, xB,y, xB,yA, x+width,yA, x+width,yB, xB,yB, xB,y+height, xA,y+height, xA,yB, x,yB, x,yA, xA,yA };
//            gc.fillPolygon(points);
//			gc.drawPolygon(points);
//			break;
//		}            
		case minus: {
			gc.fillRectangle(x, y + (height/4), width, height - (height/2));
			gc.drawRectangle(x, y + (height/4), width, height - (height/2)); 
			break;
		}
		case triangleDown: {
			gc.fillPolygon(new int[] {x, y, x+width, y, x+(width/2), y+height});
			gc.drawPolygon(new int[] {x, y, x+width, y, x+(width/2), y+height});
			break;
		}
		case triangleUp: {
			gc.fillPolygon(new int[] {x, y+height, x+width, y+height, x+(width/2), y});
			gc.drawPolygon(new int[] {x, y+height, x+width, y+height, x+(width/2), y});
			break;
		}
		case triangleRight: {
			gc.fillPolygon(new int[] {x, y+height, x+width, y+(height/2), x, y});
			gc.drawPolygon(new int[] {x, y+height, x+width, y+(height/2), x, y});
			break;
		}
		case triangleLeft: {
			gc.fillPolygon(new int[] {x, y+(height/2), x+width, y, x+width, y+height});
			gc.drawPolygon(new int[] {x, y+(height/2), x+width, y, x+width, y+height});
			break;
		}
		case triangleNorthEast: {
			gc.fillPolygon(new int[] {x, y, x+width, y, x+width, y+height});
			gc.drawPolygon(new int[] {x, y, x+width, y, x+width, y+height});
			break;
		}
		case triangleNorthWest: {
			gc.fillPolygon(new int[] {x, y, x+width, y, x, y+height});
			gc.drawPolygon(new int[] {x, y, x+width, y, x, y+height});
			break;
		}
		case triangleSouthEast: {
			gc.fillPolygon(new int[] {x, y, x+width, y+height, x, y+height});
			gc.drawPolygon(new int[] {x, y, x+width, y+height, x, y+height});
			break;
		}
		case triangleSouthWest: {
			gc.fillPolygon(new int[] {x, y+height, x+width, y+height, x+width, y});
			gc.drawPolygon(new int[] {x, y+height, x+width, y+height, x+width, y});
			break;
		}
		case roundedRect: {
			gc.fillRoundRectangle(x, y, width, height, width / 2, height / 2);
			gc.drawRoundRectangle(x, y, width, height, width / 2, height / 2);
			break;
		}
		case diamond: {
			gc.fillPolygon(new int[] {x+(width/2), y, x+width, y+(height/2), x+(width/2), y+height, x, y+(height/2)});
			gc.drawPolygon(new int[] {x+(width/2), y, x+width, y+(height/2), x+(width/2), y+height, x, y+(height/2)});
			break;
		}
		default: {
			int[] points = shapeId.scaledPointsTo(width, height, x, y, false, true);
			if (points.length > 1) {
				gc.fillPolygon(points);
				gc.drawPolygon(points);
				}
			}
		}
		if (optionalText != null) gc.drawString(optionalText, x, y);
	}
}
