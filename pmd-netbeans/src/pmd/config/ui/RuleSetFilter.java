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
	
	public static final int JARS = 1;
	public static final int RULESETS = 2;
	
	private int type;
	public RuleSetFilter( int type ) {
		this.type = type;
	}
	public boolean accept(java.io.File file) {
		if( file.isDirectory() ) {
			return true;
		}
		if( type == RULESETS ) {
			return file.getName().toLowerCase().endsWith( "xml" );
		}
		else {
			return file.getName().toLowerCase().endsWith( "jar" );
		}
	}	

	public String getDescription() {
		if( type == RULESETS ) {
			return "RuleSet(*.xml)";
		}
		else {
			return "Rules(*.jar)";
		}
	}
	
}
