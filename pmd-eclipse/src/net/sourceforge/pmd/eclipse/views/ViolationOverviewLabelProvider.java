package net.sourceforge.pmd.eclipse.views;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.model.FileRecord;
import net.sourceforge.pmd.eclipse.model.PMDRecord;
import net.sourceforge.pmd.eclipse.model.PackageRecord;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * Provides the Violation Overview with Texts and Images
 * 
 * @author SebastianRaffel  ( 09.05.2005 )
 */
public class ViolationOverviewLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	
	private static final String KEY_IMAGE_PACKAGE = "package";
	private static final String KEY_IMAGE_JAVAFILE = "javafile";
	private ViolationOverview violationView;
	
	
	/**
	 * Constructor
	 * 
	 * @param overview
	 */
	public ViolationOverviewLabelProvider(ViolationOverview overview) {
		violationView = overview;
	}
	
	/* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int) */
	public Image getColumnImage(Object element, int columnIndex) {
		Image image = null;
		
		// the second Column gets an Image depending on, 
		// if the Element is a PackageRecord or FileRecord 
		switch (columnIndex) {
			case 1:
				if (element instanceof PackageRecord) {
					image = PMDPlugin.getDefault().getImage( 
						KEY_IMAGE_PACKAGE, PMDPlugin.ICON_PACKAGE );
				} else if (element instanceof FileRecord) {
					image = PMDPlugin.getDefault().getImage( 
						KEY_IMAGE_JAVAFILE, PMDPlugin.ICON_JAVACU );
				}
				return image;
		}
		
		return image;
	}
	
	
	/* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int) */
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
			// show the Element's Name 
			case 2:
				String name = "";
				if (element instanceof PackageRecord) {
					name = ((PackageRecord) element).getName();
				} else if (element instanceof FileRecord) {
					name = ((FileRecord) element).getName();
				}
				return name;
			// show the Number of Violations
			case 3:
				return String.valueOf(violationView.getFilteredViolations(element));
			// show the Number of Violations per Line of Code
			case 4:
				int vioCount = violationView.getFilteredViolations(element);
				int loc = 0;
				
				if (element instanceof PackageRecord) {
					PackageRecord packRec = ((PackageRecord) element);
					Object[] files = packRec.getChildren();
					for (int i=0; i<files.length; i++) {
						loc += ((FileRecord) files[i]).getLinesOfCode();
					}
				} else if (element instanceof FileRecord) {
					loc = ((FileRecord) element).getLinesOfCode();
				}
				
				float vioPerLoc = (float) Math.round( 
					(float) vioCount/loc*100 )/100;
				
				if (vioPerLoc < 0.01)
					return "< 0.01";
				return String.valueOf(vioPerLoc);
			// show the Number of Violations per Number of Methods
			case 5:
				int vioCount2 = violationView.getFilteredViolations(element);
				int numMethods = 0;
				if (element instanceof PackageRecord) {
					PackageRecord packRec = ((PackageRecord) element);
					Object[] files = packRec.getChildren();
					for (int i=0; i<files.length; i++) {
						numMethods += ((FileRecord) files[i]).getNumberOfMethods();
					}
				} else if (element instanceof FileRecord) {
					numMethods = ((FileRecord) element).getNumberOfMethods();
				}
				float vioPerMethod = (float) Math.round((float) vioCount2/numMethods*100)/100;
				if ((vioPerMethod < 0.01) || (numMethods == 0))
					return "< 0.01";
				return String.valueOf(vioPerMethod);
			// show the Project's Name
			case 6:
				PMDRecord projectRec = null;
				if (element instanceof PackageRecord) {
					projectRec = ((PackageRecord) element).getParent();
				} else if (element instanceof FileRecord) {
					projectRec = ((FileRecord) element).getParent().getParent();
				}
				return projectRec.getName();
		}
		
		return "";
	}
}
