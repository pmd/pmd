/*
 *  ListCellRenderer.java
 *
 *  Created on 14. november 2002, 21:46
 */
package pmd.config.ui;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import net.sourceforge.pmd.Rule;

/**
 * @author ole martin mørk
 * @created 16. november 2002
 */
public class ListCell implements ListCellRenderer {


	/**
	 * Gets the listCellRendererComponent attribute of the ListCell object
	 *
	 * @param jList Description of the Parameter
	 * @param obj Description of the Parameter
	 * @param param Description of the Parameter
	 * @param param3 Description of the Parameter
	 * @param param4 Description of the Parameter
	 * @return The listCellRendererComponent value
	 */
	public java.awt.Component getListCellRendererComponent(
		JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Rule rule = ( Rule )value;
		JLabel box = new JLabel( rule.getName() );
		box.setEnabled( true );
		box.setBorder( isSelected ? UIManager.getBorder( "List.focusCellHighlightBorder" ) : new EmptyBorder( 1, 1, 1, 1 ) );
		return box;
	}

}
