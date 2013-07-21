package net.sourceforge.pmd.eclipse.ui;

/**
 * 
 * @author Brian Remedios
 */
public enum Shape {

	square(1, "Square"), circle(2, "Circle"), roundedRect(3, "Rounded rectangle"), diamond(4, "Diamond"),
    
	minus(5, "Dash"),  pipe(6, "Pipe"),
    
	domeRight(7, "Dome right"), domeLeft(8, "Dome left"), domeUp(9,"Dome up"), domeDown(10, "Dome down"),
	
	triangleUp(11,"Triangle up"), triangleDown(12,"Triangle down"), triangleRight(13,"Triangle right"), triangleLeft(14,"Triangle left"), 
	triangleNorthEast(15,"Triangle NE"), triangleSouthEast(16,"Triangle SE"), 
	triangleSouthWest(17,"Triangle SW"), triangleNorthWest(18,"Triangle NW"),

	plus(20,"Plus", new float[] {
			0.333f, 0,
			0.666f, 0,
			0.666f, 0.333f,
			1,		0.333f,
			1,		0.666f,
			0.666f, 0.666f,
			0.666f, 1,
			0.333f, 1,
			0.333f, 0.666f,
			0,		0.666f,
			0,		0.333f,
			0.333f, 0.333f			
			}),
	star(21, "Star", new float[] {
			 0.500f, 1.000f, 
			 0.378f, 0.619f, 
			 0,	 	 0.619f, 
			 0.303f, 0.381f, 
			 0.193f, 0,   
			 0.500f, 0.226f, 
			 0.807f, 0,   
			 0.697f, 0.381f, 
			 1.000f, 0.619f, 
			 0.622f, 0.619f 
			});
	
	public final int id;
	public final String label;
	private final float[] polyPoints;
	
	private Shape(int theId, String theLabel) {
		this(theId, theLabel, null);
	}
	
	private Shape(int theId, String theLabel, float[] optionalPolygonPoints) {
		id = theId;
		label = theLabel;
		polyPoints = optionalPolygonPoints;
	}

	@Override 
	public String toString() {
		return label;
	}

	public int[] scaledPointsTo(int xMax, int yMax, int xOffset, int yOffset, boolean flipX, boolean flipY) {
		
		if (polyPoints == null) return new int[0];
		
		int[] points = new int[polyPoints.length];
		
		for (int i=0; i<points.length; i+=2) {
			points[i] = (int)(xMax * (flipX ? 1-polyPoints[i] : polyPoints[i])) + xOffset;
			points[i+1] = (int)(yMax * (flipY ? 1-polyPoints[i+1] : polyPoints[i])) + yOffset;
		}
		
		return points;
	}
}
