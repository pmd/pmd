/*
 *  Copyright (c) 2002, Ole-Martin Mørk
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

import java.util.Enumeration;
import javax.swing.table.AbstractTableModel;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleProperties;

/** The datamodel for the properties table
 * @author ole martin mørk
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
		values = new String[properties.size()][2];
		Enumeration keys = properties.keys();
		int counter = 0;
		while( keys.hasMoreElements() ) {
			String key = ( String )keys.nextElement();
			values[counter][0] = key;
			values[counter][1] = properties.getProperty( key );
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
	 * @return false
	 */
	public boolean isCellEditable( int rowIndex, int columnIndex ) {
		return false;
	}

}
