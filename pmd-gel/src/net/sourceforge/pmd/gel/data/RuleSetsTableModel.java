/*
 * @(#)RuleSetsTableModel.java $Revision$ ($Date$)
 * Copyright (c) 2004
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.gel.data;

import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;


/**
 * An array of <em>RuleSet</em> TableModel.
 *
 * @author Andrey Lumyanski
 * @version $Revision$ ($Date$)
 */
public class RuleSetsTableModel extends AbstractTableModel {
	private static final String[] columnName = {
		"Include", "Name", "File"
	};
	private static final Class[] columnClass = {
		Boolean.class, String.class, String.class
	};
	private RuleSet[] ruleSet;

	/**
	 * Creates a <code>RuleSetsTableModel</code> object.
	 *
	 * @param rs RuleSet[] a array of <code>RuleSet</code>
	 */
	public RuleSetsTableModel(RuleSet[] rs) {
		super();
		ruleSet = rs;
	}

	/**
	 * Returns the columns count.
	 *
	 * @return the columns count.
	 */
	public int getColumnCount() {
		return columnName.length;
	}

	/**
	 * Always returns String.class
	 *
	 * @param index int the column index
	 *
	 * @return String.class
	 */
	public Class getColumnClass(int index) {
		return columnClass[index];
	}

	/**
	 * Returns the column name.
	 *
	 * @param index int the column index
	 *
	 * @return the column name
	 */
	public String getColumnName(int index) {
		return columnName[index];
	}

	/**
	 * Returns the number of rows in the model.
	 *
	 * @return the number of rows in the model.
	 */
	public int getRowCount() {
		return ruleSet.length;
	}

	/**
	 * Returns <code>true</code> for column #0 and <code>false</code> for all
	 * other columns.
	 *
	 * @param rowIndex - the row whose value to be queried
	 * @param columnIndex - the column whose value to be queried
	 *
	 * @return <code>true</code> if the cell is editable
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 *
	 * @param rowIndex  the row whose value is to be queried
	 * @param columnIndex  the column whose value is to be queried
	 *
	 * @return the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;

		switch (columnIndex) {
		case 0:
			value = Boolean.valueOf(ruleSet[rowIndex].include());
			break;
		case 1:
			value = ruleSet[rowIndex].getName();
			break;
		case 2:
			value = ruleSet[rowIndex].getFileName();
			break;
		}

		return value;
	}

	/**
	 * Sets the value in the cell at <code>columnIndex</code>
	 * and <code>rowIndex</code> to <code>value</code>.
	 *
	 * @param value the new value
	 * @param rowIndex  the row whose value is to be changed
	 * @param columnIndex  the column whose value is to be changed
	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			ruleSet[rowIndex].setInclude(((Boolean)value).booleanValue());
			if (ruleSet[rowIndex].include()) {
				Iterator it = ruleSet[rowIndex].getRules().iterator();
				Rule rule = null;
				while (it.hasNext()) {
					rule = (Rule) it.next();
					rule.setInclude(true);
				}
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}