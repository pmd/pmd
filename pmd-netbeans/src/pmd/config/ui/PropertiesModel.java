/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config.ui;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleProperties;
import pmd.config.PMDOptionsSettings;

/** The datamodel for the properties table
 * @author ole martin mørk
 * @author Gunnlaugur Þór Briem
 * @created 25. november 2002
 */
public class PropertiesModel extends AbstractTableModel {

	/** The values used in the table */	
	private final String values[][];

	/** Creates a new instance of PropertiesModel
	 * @param rule The rule the table should be based upon
	 */
	public PropertiesModel( Rule rule ) {
		if( rule == null ) {
			values = new String[0][0];
			return;
		}
		RuleProperties properties = rule.getProperties();
		Map propertyOverrides = (Map)PMDOptionsSettings.getDefault().getRuleProperties().get(rule.getName());
		if(propertyOverrides == null) {
			propertyOverrides = Collections.EMPTY_MAP;
		}
		values = new String[properties.size()][2];
		Enumeration keys = properties.keys();
		int counter = 0;
		while( keys.hasMoreElements() ) {
			String key = ( String )keys.nextElement();
			values[counter][0] = key;
			
			if( propertyOverrides.containsKey( key ) ) {
				values[counter][1] = (String)propertyOverrides.get( key );
			} else {
				values[counter][1] = properties.getProperty( key );
			}
			counter++;
		}

	}
	
	/** Gets the number of columns in the table
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return 2;
	}


	/** Gets the number of rows in the table
	 * @return the number of rows
	 */
	public int getRowCount() {
		return values.length;
	}


	/** Gets the value at the specified place in the table
	 * @return the value at row-column
	 * @param row The row where the data is
	 * @param column The column where the data is
	 */
	public Object getValueAt( int row, int column ) {
		return values[row][column];
	}


	/** Gets the name of the columns
	 * @param column The column index
	 * @return The name of the column
	 */
	public String getColumnName( int column ) {
		return column == 0 ? "Name" : "Value";
	}


	/** Says if the cell is editable
	 * @param rowIndex The row
	 * @param columnIndex The column
	 * @return true if and only if <code>columnIndex == 1</code>.
	 */
	public boolean isCellEditable( int rowIndex, int columnIndex ) {
		return columnIndex == 1;
	}

	/** Sets the value in the cell at the given coordinates in the table.
	 *
	 * @param obj the new value of the cell.
	 * @param rowIndex The row
	 * @param columnIndex The column
	 * @throws IllegalArgumentException if row is out of range, column is not 1, or obj is not a String.
	 */
	public void setValueAt(Object obj, int rowIndex, int columnIndex) {
		if(columnIndex != 1) {
			throw new IllegalArgumentException("Can only edit property values, not property names");
		}
		if(rowIndex < 0 || rowIndex >= values.length) {
			throw new IllegalArgumentException("Row " + rowIndex + " out of range 0.." + (values.length - 1));
		}
		if(obj instanceof String) {
			String value = (String)obj;
			values[rowIndex][columnIndex] = value;
			fireTableCellUpdated(rowIndex, columnIndex);
		} else {
			throw new IllegalArgumentException("Property value must be String, was: " + String.valueOf(obj));
		}
	}

}
