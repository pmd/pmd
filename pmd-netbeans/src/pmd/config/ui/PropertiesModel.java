/*
 * PropertiesModel.java
 *
 * Created on 21. november 2002, 20:59
 */

package pmd.config.ui;

import java.util.Enumeration;
import javax.swing.table.AbstractTableModel;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleProperties;

/**
 *
 * @author  ole martin mørk
 */
public class PropertiesModel extends AbstractTableModel {
	
	private final String values[][];
	/** Creates a new instance of PropertiesModel */
	public PropertiesModel( Rule rule ) {
		if( rule == null ) {
			values = new String[0][0];
			return;
		}
		RuleProperties properties = rule.getProperties();
		values = new String[properties.size()][2];
		Enumeration keys = properties.keys();
		int counter = 0;
		while(keys.hasMoreElements() ) {
			String key = (String)keys.nextElement();
			values[counter][0] = key;
			values[counter][1] = properties.getProperty( key );
			counter++;
		}
		
	}
	
	public int getColumnCount() {
		return 2;
	}
	
	public int getRowCount() {
		return values.length;
	}
	
	public Object getValueAt(int param, int param1) {
		return values[param][param1];
	}
	
	public String getColumnName( int column ) {
		return column == 0 ? "Name" : "Value";
	}
		
	public boolean isCellEditable( int rowIndex, int columnIndex ) {
		return columnIndex == 1;
	}
	
}