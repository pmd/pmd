/*
 * RulesetFilter.java
 *
 * Created on 20. februar 2003, 22:52
 */

package pmd.config.ui;

import javax.swing.filechooser.FileFilter;

/**
 *
 * @author  ole martin mørk
 */
public class RuleSetFilter extends FileFilter {
	
	public boolean accept(java.io.File file) {
		return file.getName().toLowerCase().endsWith( "xml" ) || file.isDirectory();
	}	

	public String getDescription() {
		return "RuleSet(*.xml)";
	}
	
}
